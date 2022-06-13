package com.example.appcontacto;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

/**
 * Clase que controla las conexiones Bluetooth
 */
public class BluetoothConnectionServ {
    private static final String TAG = "BluetoothConnectionServ";
    private String appName = "App Contactos";
    private static final UUID INSECURE_UUID = UUID.fromString("58e1a705-623d-4938-ad2e-2d33ce58b8d0");

    private final BluetoothAdapter bluetoothAdapter;
    /**
     * The Context.
     */
    Context context;
    private AcceptThread acceptThread;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private BluetoothDevice mbtDevice;
    private UUID deviceUUID;
    /**
     * The Mi progress dialogo.
     */
    ProgressDialog miProgressDialogo;


    /**
     * Constructor de la clase
     *
     * @param context Contexto que recibe para interactuar con la clase
     */
    public BluetoothConnectionServ(Context context) {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.context = context;
        start();
    }

    /**
     * Clase que controla el hilo que envia al socket
     */
    private class AcceptThread extends Thread {
        /**
         * Variable del socket local
         */
        private final BluetoothServerSocket btSSocket;

        /**
         * Este hilo se queda esperando a recibir una conexion, Funciona como un server-side client.
         * Sigue ejecutando hasta que se acepta una conexion o se cancela.
         */
        @SuppressLint("MissingPermission")
        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, INSECURE_UUID);
                Log.d(TAG, "AcceptThread: Configurando el servidor" + INSECURE_UUID);
            } catch (IOException e) {
                Log.e(TAG, "AcceptThread: IOException" + e.getMessage());
            }
            btSSocket = tmp;
        }

        /**
         * Lanza el hilo, se mantiene a la escucha hasta que reciba un socket o se produzca una Exception
         */
        public void run() {
            Log.d(TAG, "Ejecutando: AcceptThread ejecutando");
            BluetoothSocket socket = null;
            try {
                Log.d(TAG, "Ejecutando: RFCOM iniciando server socket ");
                socket = btSSocket.accept();
                Log.d(TAG, "Ejecutando: RFCOM conexion aceptada");
            } catch (IOException e) {
                Log.e(TAG, "AcceptThread: IOException" + e.getMessage());
            }
            if (socket != null) {
                connected(socket, mbtDevice);
            }
            Log.d(TAG, "Termian acceptThread");

        }

        /**
         * Metodo que cancela todo tipo de ejecucion del hilo
         */
        public void cancel() {
            Log.d(TAG, "Cancelando AcceptThread");
            try {
                btSSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Cancelando AcceptThread. ServerSocket fallo" + e.getMessage());
            }
        }
    }

    /**
     * Este hilo se ejecuta cuando se intenta establecer una conexcion saliente con un dispositivo.
     * Se ejecuta a traves de la conexion, ya sea si tiene exito o falla
     */
    private class ConnectThread extends Thread {
        private BluetoothSocket btSocket;

        /**
         * Instantiates a new Connect thread.
         *
         * @param btDevice the bt device
         * @param uuid     the uuid
         */
        public ConnectThread(BluetoothDevice btDevice, UUID uuid) {
            mbtDevice = btDevice;
            deviceUUID = uuid;
        }

        /**
         * Metodo que ejecuta el hilo referente a ConnecThread
         */
        @SuppressLint("MissingPermission")
        public void run() {
            BluetoothSocket tmp = null;
            try {

                //Obtiene un BluetoothSocket para una conexion con el BluetoothDevice
                Log.d(TAG, "Intentando crear la conexion usando UUID:" + INSECURE_UUID);
                tmp = mbtDevice.createRfcommSocketToServiceRecord(deviceUUID);
            } catch (IOException e) {
                Log.e(TAG, "Error al crear la conexion" + e.getMessage());
            }
            btSocket = tmp;

            bluetoothAdapter.cancelDiscovery();

            try {
                //Esto es una llamada de solo una llamada con exito y en caso contrario lanza una exception
                btSocket.connect();
                Log.d(TAG, "ConnecThread concectado");
            } catch (IOException e) {
                try {
                    btSocket.close();
                    Log.d(TAG, "Socket cerrado");
                } catch (IOException err) {
                    Log.e(TAG, "Imposible cerrar la coneccion en este socket" + err.getMessage());
                }
                Log.d(TAG, "ConnectThread: No se pudo conectar con la UUID" + INSECURE_UUID);
            }
            connected(btSocket, mbtDevice);
        }

        /**
         * Metodo que cancela todo tipo de ejecucion del hilo
         */
        public void cancel() {

            try {
                Log.d(TAG, "Cerrando socket cliente");
                btSocket.close();
            } catch (IOException e) {
                Log.d(TAG, "Cancelando: close() en ConnectThread fallo" + e.getMessage());
            }
        }
    }


    /**
     * Esto inicia especificamente el AcceptThread para empezar una sesion en el listening mode
     * Se ejecuta en el Activity onResume()
     * Si la conexion es distinto de null, cancela la que se este ejecutando y lanza una nueva vacia
     * Si la conexion es null, crea una nueva y la lanza
     */
    public synchronized void start() {
        Log.d(TAG, "start");
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        if (acceptThread == null) {
            acceptThread = new AcceptThread();
            acceptThread.start();
        }
    }

    /**
     * Este metodo incia ConnectThread
     *
     * @param device el dispositivo que recibe
     * @param uuid   la uuid para hacer posible esa conexion
     */
    public void startClient(BluetoothDevice device, UUID uuid) {
        Log.d(TAG, "Start Client");

        miProgressDialogo = ProgressDialog.show(context, "Conectando Bluetooth",
                "Espere por favor...", true);
        connectThread = new ConnectThread(device, uuid);
        connectThread.start();
    }


    /**
     * Clase que es responsable de manejar los procesos de conexion del BtConnection,
     * eviando y recibiendo la informacion
     */
    private class ConnectedThread extends Thread {
        private BluetoothSocket btSocket;
        private InputStream inputStream;
        private OutputStream outputStream;

        /**
         * Instantiates a new Connected thread.
         *
         * @param btSocket the bt socket
         */
        public ConnectedThread(BluetoothSocket btSocket) {
            this.btSocket = btSocket;
            InputStream tmpInputS = null;
            OutputStream tmpOutputS = null;

            try {
                miProgressDialogo.dismiss();
            }catch (NullPointerException e){
                e.printStackTrace();
            }

            try {
                tmpInputS = btSocket.getInputStream();
                tmpOutputS = btSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream = tmpInputS;
            outputStream = tmpOutputS;
        }

        /**
         * Esperando hasta que el inputStream de algun error
         */
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            while (true) {
                try {
                    bytes = inputStream.read(buffer);
                    String incomingMessage = new String(buffer, 0, bytes);
                    Log.d(TAG, "InputStream: " + incomingMessage);
                } catch (IOException e) {
                    Log.e(TAG, "Error leyendo input: " + e.getMessage());
                    break;
                }
            }
        }

        /**
         * LLamar a este metodo desde la main activity para enviar datos a otro dispositivo
         *
         * @param bytes Bytes que se van a escribir
         */
        public void write(byte[] bytes) {
            String text = new String(bytes, Charset.defaultCharset());
            Log.d(TAG, "Escribiendo OutputStream: " + text);
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                Log.e(TAG, "Error escribiendo OutputStream: " + e.getMessage());

            }
        }

        /**
         * Llamar a este metodo desde la main activity para cancelar cualquier proceso
         */
        public void cancel() {
            try {
                btSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Metodo que controla conexion entre el dispositivo que recibe los datos y el socket del dispositivo servidor
     * @param btSocket Socket del dispositivo servidor
     * @param mbtDevice Dispositivo bluetooth servidor
     */
    private void connected(BluetoothSocket btSocket, BluetoothDevice mbtDevice) {
        Log.d(TAG, "Estableciendo conexion");

        connectedThread = new ConnectedThread(btSocket);
        connectedThread.start();
    }

    /**
     * Escribe en el ConnectedThread
     *
     * @param out los bytes que se van a escribir
     * @see ConnectedThread#write(byte[]) ConnectedThread#write(byte[])ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        ConnectedThread tmpConnectedThread;

        //Llamada al write
        connectedThread.write(out);
    }
}

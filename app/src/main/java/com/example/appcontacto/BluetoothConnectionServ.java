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

public class BluetoothConnectionServ {
    private static final String TAG = "BluetoothConnectionServ";
    private String appName = "App Contactos";
    private static final UUID INSECURE_UUID = UUID.fromString("58e1a705-623d-4938-ad2e-2d33ce58b8d0");

    private final BluetoothAdapter bluetoothAdapter;
    Context context;
    private AcceptThread acceptThread;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private BluetoothDevice mbtDevice;
    private UUID deviceUUID;
    ProgressDialog miProgressDialogo;



    public BluetoothConnectionServ(Context context) {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.context = context;
    }


    private class AcceptThread extends Thread {
        private final BluetoothServerSocket btSSocket;

        @SuppressLint("MissingPermission")
        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, INSECURE_UUID);
                Log.d(TAG, "AcceptThread: Configurando el servidor" + INSECURE_UUID);
            } catch (IOException e) {
                Log.d(TAG, "AcceptThread: IOException" + e.getMessage());
            }
            btSSocket = tmp;
        }

        public void run() {
            Log.d(TAG, "Ejecutando: AcceptThread ejecutando");
            BluetoothSocket socket = null;
            while (true) {
                try {
                    socket = btSSocket.accept();
                    Log.d(TAG, "Ejecutando: RFCOM conexion aceptada");
                } catch (IOException e) {
                    Log.d(TAG, "AcceptThread: IOException" + e.getMessage());
                }
                if (socket != null) {
                    connected(socket, mbtDevice);
                }
            }
        }

        public void cancel(){
            Log.d(TAG,"Cancelando AcceptThread");
            try{
                btSSocket.close();
            }catch (IOException e){
                Log.d(TAG,"Cancelando AcceptThread. ServerSocket fallo" + e.getMessage());
            }
        }
    }


    private class ConnectThread extends Thread{
        private BluetoothSocket btSocket;

        public ConnectThread(BluetoothDevice btDevice, UUID uuid) {
            mbtDevice = btDevice;
            deviceUUID = uuid;
        }

        @SuppressLint("MissingPermission")
        public void run() {
            BluetoothSocket tmp = null;
            try{
                Log.d(TAG, "Intentando crear la conexion usando UUID:" + INSECURE_UUID);
                tmp = mbtDevice.createInsecureRfcommSocketToServiceRecord(deviceUUID);
            }catch (IOException e){
                Log.e(TAG,"Error al crear la conexion" + e.getMessage());
            }
            btSocket = tmp;

            bluetoothAdapter.cancelDiscovery();

            try{
                btSocket.connect();
                Log.d(TAG, "ConnecThread concectado");
            }catch (IOException e){
                try{
                    btSocket.close();
                    Log.d(TAG, "Socket cerrado");
                }catch (IOException err){
                    Log.e(TAG,"Imposible cerrar la coneccion en este socket" + err.getMessage());
                }
                Log.d(TAG,"ConnectThread: No se pudo conectar con la UUID" + INSECURE_UUID);
            }
            connected(btSocket,mbtDevice);
        }
        public void cancel(){

            try{
                Log.d(TAG,"Cerrando socket cliente");
                btSocket.close();
            }catch (IOException e){
                Log.d(TAG,"Cancelando: close() en ConnectThread fallo" + e.getMessage());
            }
        }
        public synchronized void start(){
            Log.d(TAG, "start");
            if(connectThread!=null){
                connectThread.cancel();
                connectThread = null;
            }
            if(acceptThread==null){
                acceptThread = new AcceptThread();
                acceptThread.start();
            }
        }

        public void startClient(BluetoothDevice device, UUID uuid){

            Log.d(TAG, "Start Client");

            miProgressDialogo = ProgressDialog.show(context, "Conectando Bluetooth",
                    "Espere por favor...", true);
            connectThread = new ConnectThread(device,uuid);
            connectThread.start();
        }
    }



    private class ConnectedThread extends Thread{
        private BluetoothSocket btSocket;
        private InputStream inputStream;
        private OutputStream outputStream;

        public ConnectedThread(BluetoothSocket btSocket) {
            this.btSocket = btSocket;
            InputStream tmpInputS = null;
            OutputStream tmpOutputS = null;

            miProgressDialogo.dismiss();

            try{
                tmpInputS = btSocket.getInputStream();
                tmpOutputS = btSocket.getOutputStream();
            }catch (IOException e){
                e.printStackTrace();
            }
            inputStream = tmpInputS;
            outputStream = tmpOutputS;
        }

        public void run(){
            byte[] buffer = new byte[1024];
            int bytes;

            while(true){
                try{
                    bytes = inputStream.read(buffer);
                    String incomingMessage = new String(buffer, 0, bytes);
                    Log.d(TAG,"InputStream: " + incomingMessage);
                }catch (IOException e){
                    Log.e(TAG, "Error leyendo input: "+ e.getMessage());
                    break;
                }
            }
        }

        /**
         * LLamar a este metodo desde la main activity para enviar datos a otro dispositivo
         * @param bytes
         */
        public void write(byte[] bytes){
            String text = new String(bytes, Charset.defaultCharset());
            Log.d(TAG,"Escribiendo OutputStream: "+ text);
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                Log.e(TAG, "Error escribiendo OutputStream: "+ e.getMessage());

            }
        }

        public void cancel(){
            try {
                btSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void connected(BluetoothSocket btSocket, BluetoothDevice mbtDevice) {
        Log.d(TAG, "conectando");
        connectedThread = new ConnectedThread(btSocket);
        connectedThread.start();

    }

    private void write(byte[] out){
        ConnectedThread tmpConnectedThread;
        connectedThread.write(out);
    }
}

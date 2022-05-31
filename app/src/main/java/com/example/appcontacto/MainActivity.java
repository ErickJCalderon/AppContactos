package com.example.appcontacto;


import static android.bluetooth.BluetoothAdapter.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    String TAG = "App Contactos";
    RecyclerView recyclerView;
    ArrayList<Contacto> contactos = new ArrayList<>();
    MainAdapter adapter;
    BluetoothAdapter bluetoothAdapter = getDefaultAdapter();
    Button botonSeleccionar;
    Button iniConect;
    private static final UUID INSECURE_UUID = UUID.fromString("58e1a705-623d-4938-ad2e-2d33ce58b8d0");
    BluetoothConnectionServ bluetoothConnection;
    BluetoothDevice bluetoothDevice;
    public ArrayList<BluetoothDevice> bluetoothDevices = new ArrayList<>();
    String contact;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_conexion, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 1 && requestCode == RESULT_OK) {
            Uri uri = data.getData();
            Cursor cursor = getContentResolver().query(uri, null, null
                    , null
                    , null);


            if (cursor != null && cursor.moveToFirst()) {
                int indiceNombre = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int indiceNumero = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);

                contact = cursor.getString(indiceNombre) + ";" + cursor.getString(indiceNumero);

            }

            cursor.close();
        }

        /**
         * Switch donde se recibe los datos de conexion de ListaDispositivos
         */
        switch (requestCode) {
            case Constants.CONNECT_DEVICE_INSECURE:
                break;
            case Constants.CONNECT_DEVICE_SECURE:
                if (resultCode == Activity.RESULT_OK) {
                    String macAddress = Objects.requireNonNull(data.getExtras()).getString(ListaDispositivos.EXTRA_DEVICE_ADDRESS);
                    Log.d("MI DATO ", macAddress);
                }
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btOn:
                if (!bluetoothAdapter.isEnabled()) {
                    Toast.makeText(this, "Encendiendo Bluetooth", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, 1);

                    IntentFilter btIntent = new IntentFilter(ACTION_STATE_CHANGED);
                    registerReceiver(broadCastReciver1, btIntent);
                } else {
                    Toast.makeText(this, "El Bluetooth ya esta encendido", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btOff:
                if (bluetoothAdapter.isEnabled()) {
                    bluetoothAdapter.disable();
                    Toast.makeText(this, "Apagando Bluetooth", Toast.LENGTH_SHORT).show();
                    IntentFilter btIntent = new IntentFilter(ACTION_STATE_CHANGED);
                    registerReceiver(broadCastReciver1, btIntent);
                } else {
                    Toast.makeText(this, "El Bluetooth ya esta apagado", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btExplorar:
                if (!bluetoothAdapter.isDiscovering()) {
                    Toast.makeText(this, "Haciendo reconocible su dispositivo", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent((ACTION_REQUEST_DISCOVERABLE));
                    intent.putExtra(EXTRA_DISCOVERABLE_DURATION, 200);
                    startActivityForResult(intent, 1);

                    IntentFilter btIntent = new IntentFilter(ACTION_SCAN_MODE_CHANGED);
                    registerReceiver(broadCastReciver2, btIntent);
                }
                break;
            case R.id.btNoEmparejados:
                Toast.makeText(this, "Buscando dispositivos no emparejados", Toast.LENGTH_SHORT).show();
                if (bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();

                    checkBTPermission();
                    Intent intent = new Intent(MainActivity.this, ListaDispositivos.class);
                    bluetoothAdapter.startDiscovery();
                    startActivityForResult(intent, 1);

                    IntentFilter discoverDeviceIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(broadCastReciver3, discoverDeviceIntent);
                }

                if (!bluetoothAdapter.isDiscovering()) {

                    checkBTPermission();
                    Intent intent = new Intent(MainActivity.this, ListaDispositivos.class);
                    bluetoothAdapter.startDiscovery();
                    startActivityForResult(intent, 1);

                    IntentFilter discoverDeviceIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(broadCastReciver3, discoverDeviceIntent);
                } else {
                    Toast.makeText(this, "Por favor, conecta el bluetooth", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btEmparejados:
                if (bluetoothAdapter.isEnabled()) {
                    Intent intent = new Intent(MainActivity.this, ListaDispositivos.class);
                    startActivityForResult(intent, Constants.CONNECT_DEVICE_SECURE);

                    IntentFilter btIntent = new IntentFilter(ACTION_STATE_CHANGED);
                    registerReceiver(broadCastReciver1, btIntent);
                } else {
                    Toast.makeText(this, "Por favor, conecta el bluetooth", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkBTPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0)
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
        } else {
            Log.d(TAG, "checkBTPermissions:No need to check permissions.SDK version<R.");
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * Establecemos un BroadCast cuando el estado del emparejamiento cambie
         */
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(broadCastReciver4, intentFilter);


        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
        //Asignamos la Recycler View
        recyclerView = findViewById(R.id.rcView);

        //Check de los permisos
        checkPermission();

        botonSeleccionar = findViewById(R.id.btBoton);

        iniConect = findViewById(R.id.startConnection);

        iniConect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startConnection();
            }
        });

    }

    public void startBtConnection(BluetoothDevice device, UUID uuid) {
        bluetoothConnection.startClient(device, uuid);
    }

    public void startConnection() {
        startBtConnection(bluetoothDevice, INSECURE_UUID);
    }

    private void checkPermission() {
        // Verificar Condicion
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            //Cuando no se ha dado permiso, solicita el permiso
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_CONTACTS}, 100);
        } else {
            //Cuando se tenga permiso
            getListaContacto();
        }
    }

    /**
     * Obetiene la lista de contactos del dispositivo movil donde este instalada la aplicacion
     */
    private void getListaContacto() {
        //Iniciamos una Uri
        Uri uri = ContactsContract.Contacts.CONTENT_URI;


        //Iniciamos un cursor, para recorrer la lista
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        if (cursor.getCount() > 0) {
            //mientras el cursor sea mayor a 0
            //usamos un bucle while para movernos dentro

            while (cursor.moveToNext()) {
                Cursor tlfCursor = null;
                try {
                    //Id del contacto
                    String id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                    //Nombre del contacto
                    String nombre = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                    //Apellido del contacto


                    //Iniciamos el Uri del telefono
                    Uri uriphone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

                    //Iniciamos una seleccion
                    String seleccion = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?";

                    //Iniciamos un cursor para el telefono
                    tlfCursor = getContentResolver().query(uriphone, null, seleccion, new String[]{id}, null);

                    //Cuando se mueve el cursor
                    while (tlfCursor.moveToNext()) {
                        try {
                            String numero = tlfCursor.getString(tlfCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            //Inicializamos un modelo
                            Contacto contacto = new Contacto();

                            //establecemos el nombre y el telefono y lo añadimos al array
                            contacto.setNombre(nombre);
                            contacto.setNumero(numero);
                            contactos.add(contacto);
                        } catch (IndexOutOfBoundsException ex) {
                            ex.printStackTrace();
                        }

                    }

                } catch (IndexOutOfBoundsException ex) {
                    ex.printStackTrace();
                } finally {
                    if (tlfCursor != null)
                        tlfCursor.close();
                }


            }
            cursor.close();
        }
        //Establecemos el Layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Establecemos el adapter
        adapter = new MainAdapter(this, contactos, new MainAdapter.ItemClickListener() {
            @Override
            public void onItemClick(Contacto contacto) {
                Intent intent = new Intent(MainActivity.this, DetalleContacto.class);
                intent.putExtra("NOMBRECONTACTO", contacto.getNombre());
                intent.putExtra("NUMERO", contacto.getNumero());
                intent.putExtra("APELLIDOCONTACTO", contacto.getApellido());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Check de la condicion
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getListaContacto();
        } else {
            //Cuando el permiso esta denegado
            Toast.makeText(MainActivity.this, "Permiso denegado", Toast.LENGTH_SHORT).show();
            checkPermission();
        }

    }

    public void seleccionarContacto(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(intent, 1);
    }

    /**
     * Crear un BroadcastReciver for ACTION_FOUND a la variable bluetoothAdapter
     */
    private final BroadcastReceiver broadCastReciver1 = new BroadcastReceiver() {
        public void onReceive(Context context, @NonNull Intent intent) {
            String action = intent.getAction();
            if (action.equals(bluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, bluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "State: Off");
                        break;
                    case STATE_TURNING_OFF:
                        Log.d(TAG, "State: Turning Off");
                        break;
                    case STATE_ON:
                        Log.d(TAG, "State: On");
                        break;
                    case STATE_TURNING_ON:
                        Log.d(TAG, "State: Turning On");
                        break;
                }
            }
        }
    };
    private final BroadcastReceiver broadCastReciver2 = new BroadcastReceiver() {
        public void onReceive(Context context, @NonNull Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
                final int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, bluetoothAdapter.ERROR);
                switch (mode) {
                    // Device is in Discoverable Mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadcastReceiver2:Discoverability Enabled.");
                        break;
                    // Device not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2:Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver2:Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2:Connecting ....");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2:Connected.");
                        break;
                }
            }
        }
    };

    private final BroadcastReceiver broadCastReciver3 = new BroadcastReceiver() {

        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            Log.d(TAG, "onReceive: ACTION FOUND");

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                bluetoothDevices.add(device);
                Log.d(TAG, "OnRecieve: " + device.getName() + ": " + device.getAddress());
            }
        }
    };

    public final BroadcastReceiver broadCastReciver4 = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {

                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //case1: alredy bonded
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.d(TAG, "BroadcastReceiver:BOND_BONDING.");
                }
                // case2:creatingabone
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver:BOND_BONDING.");
                }
                // case3:breakingabond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver:BOND_NONE.");
                }

            }
        }
    };
}
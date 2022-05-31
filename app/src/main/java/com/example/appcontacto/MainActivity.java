package com.example.appcontacto;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Contacto> contactos = new ArrayList<>();
    MainAdapter adapter;
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    Button botonSeleccionar;
    Button iniConect;
    private static final UUID INSECURE_UUID = UUID.fromString("58e1a705-623d-4938-ad2e-2d33ce58b8d0");
    BluetoothConnectionServ bluetoothConnection;
    BluetoothDevice bluetoothDevice;
    String contact;





    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();

                    }
                }
            });

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

                contact = cursor.getString(indiceNombre)+";"+cursor.getString(indiceNumero);

            }

            cursor.close();
        }

        switch (requestCode) {
            case Constants.CONNECT_DEVICE_INSECURE:
                break;
            case Constants.CONNECT_DEVICE_SECURE:
                if (resultCode == Activity.RESULT_OK) {
                    String macAddress = Objects.requireNonNull(data.getExtras()).getString(ListaDispositivos.EXTRA_DEVICE_ADDRESS);
                    Log.d("MI DATO", macAddress);
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
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    someActivityResultLauncher.launch(intent);
                } else {
                    Toast.makeText(this, "El Bluetooth ya esta encendido", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btOff:
                if (bluetoothAdapter.isEnabled()) {
                    bluetoothAdapter.disable();
                } else {
                    Toast.makeText(this, "El Bluetooth ya esta apagado", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btExplorar:
                if (!bluetoothAdapter.isDiscovering()) {
                    Toast.makeText(this, "Haciendo reconocible su dispositivo", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent((BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE));
                    someActivityResultLauncher.launch(intent);
                }
                break;
            case R.id.btEmparejados:
                if (bluetoothAdapter.isEnabled()) {
                    Intent intent = new Intent(MainActivity.this, ListaDispositivos.class);
                    someActivityResultLauncher.launch(intent);
                } else {
                    Toast.makeText(this, "Por favor, conecta el bluetooth", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    public void startConnection(){
        startBtConnection(bluetoothDevice,INSECURE_UUID);
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

    public void startBtConnection(BluetoothDevice device, UUID uuid){
        bluetoothConnection.startClient(device,uuid);
    }

}
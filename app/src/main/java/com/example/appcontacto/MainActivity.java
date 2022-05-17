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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView warning;
    ArrayList<Contacto> contactos  = new ArrayList<Contacto>();
    MainAdapter adapter;
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


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

        try {
            switch (requestCode){
                case Constants.CONNECT_DEVICE_INSECURE:
                    break;
                case Constants.CONNECT_DEVICE_SECURE:
                    if (resultCode == Activity.RESULT_OK){
                        String macAddress = Objects.requireNonNull(data.getExtras()).getString(ListaDispositivos.EXTRA_DEVICE_ADDRESS);
                        Log.d("MI DATO", macAddress);
                    }
            }
        }catch (NullPointerException ex){
            ex.printStackTrace();
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
                if(!bluetoothAdapter.isDiscovering()){
                    Toast.makeText(this, "Haciendo reconocible su dispositivo", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent((BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE));
                    someActivityResultLauncher.launch(intent);
                }
                break;
            case R.id.btEmparejados:
                if(bluetoothAdapter.isEnabled()){
                    Intent intent = new Intent(MainActivity.this, ListaDispositivos.class);
                    someActivityResultLauncher.launch(intent);
                }else{
                    Toast.makeText(this, "Por favor, conecta el bluetooth", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    1);
        }


        setContentView(R.layout.activity_main);

        checkPermission();
        if (bluetoothAdapter == null) {
            warning.setText("No hay soporte Bluetooth para su dispositivo");
        } else {
            warning.setText("Su dispositvo cuenta con soporte Bluetooth");
        }


    }

    private void checkPermission() {
    // Verificar Condicion
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED)
        {
            //Cuando no se ha dado permiso, solicita el permiso
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_CONTACTS},100);
        } else {
            //Cuando se tenga permiso
            getListaContacto(); 
        }
    }

    private void getListaContacto() {
        //Iniciamos una Uri
        Uri uri = ContactsContract.Contacts.CONTENT_URI;

        //Ordenacion de manera ascendente
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+"ASC";

        //Iniciamos un cursor, para recorrer la lista
        Cursor cursor = getContentResolver().query(uri,null,null,null,sort);

        if(cursor.getCount()>0){
            //mientras el cursor sea mayor a 0
            //usamos un bucle while para movernos dentro

            while(cursor.moveToNext()){
                //Id del contacto
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                //Nombre del contacto
                String nombre = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                //Iniciamos el Uri del telefono
                Uri uriphone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

                //Iniciamos una seleccion
                String seleccion = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?";

                //Iniciamos un cursor para el telefono
                Cursor tlfCursor = getContentResolver().query(uriphone, null, seleccion, new String[]{id},null);

                //Condicion de comprobacion
                if(cursor.moveToNext()){
                    //Cuando se mueve el cursor
                    String numero = tlfCursor.getString(tlfCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    //Inicializamos un modelo
                    Contacto contacto = new Contacto();

                    //establecemos el nombre y el telefono y lo añadimos al array
                    contacto.setNombre(nombre);
                    contacto.setNumero(" " + numero);
                    contactos.add(contacto);

                    //cerramos el cursor
                    tlfCursor.close();
                }

            }
            cursor.close();
        }
        //Establecemos el Layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Establecemos el adapter
        adapter = new MainAdapter(this, contactos);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Check de la condicion
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getListaContacto();
        }else {
            //Cuando el permiso esta denegado
            Toast.makeText(MainActivity.this, "Permiso denegado", Toast.LENGTH_SHORT).show();
            checkPermission();
        }

    }
}
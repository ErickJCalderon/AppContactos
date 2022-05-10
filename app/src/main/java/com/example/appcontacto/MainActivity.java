package com.example.appcontacto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView warning;
    ListView lvContactos;
    ArrayList<Contacto> contactos;
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    int REQUEST_ENABLE_BT = 0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_conexion, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvContactos = findViewById(R.id.lvContactos);
        warning = findViewById(R.id.btStatus);
        contactos = new ArrayList<>();
        contactos.add(new Contacto("Carlos", "Alvarez", "carlos.alvarez@meloinvento.com", 632542123));
        contactos.add(new Contacto("Andrea", "Gomez", "andrea.gomez@meloinvento.com", 654789123));
        contactos.add(new Contacto("Marcos", "Calderon", "marcos.calderon@meloinvento.com", 698753456));
        contactos.add(new Contacto("Pedro", "Nuñez", "pedro.nuñez@meloinvento.com", 624542123));
        contactos.add(new Contacto("Sara", "Nogales", "sara.nogales@meloinvento.com", 618100000));

        ArrayList<String> nombreContactos = new ArrayList<>();

        for (Contacto contacto : contactos) {
            nombreContactos.add(contacto.getNombre() + " " + contacto.getApellido());
        }
        lvContactos.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nombreContactos));
        lvContactos.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, DetalleContacto.class);
            intent.putExtra("NOMBRECONTACTO", contactos.get(position).getNombre());
            intent.putExtra("APELLIDOCONTACTO", contactos.get(position).getApellido());
            intent.putExtra("EMAILCONTACTO", contactos.get(position).getEmail());
            intent.putExtra("TELEFONOCONTACTO", contactos.get(position).getTelefono());
            startActivity(intent);
        });

        if (bluetoothAdapter == null) {
            warning.setText("No hay soporte Bluetooth para su dispositivo");
        } else {
            warning.setText("Su dispositvo cuenta con soporte Bluetooth");
        }


        /*if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }*/


    }
}
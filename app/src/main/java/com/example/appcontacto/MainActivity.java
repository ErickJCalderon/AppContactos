package com.example.appcontacto;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView warning;
    ListView lvContactos;
    ArrayList<Contacto> contactos;
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;

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
                break;
            case R.id.btEmparejados:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestLocationPermission() {
        final String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_REQUIRED_PERMISSIONS);
            return;
        }
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


    }
}
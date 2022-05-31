package com.example.appcontacto;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.app.Activity;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class ListaDispositivos extends Activity {

    ListView lista_dispositivos;


    public static final String EXTRA_DEVICE_ADDRESS = "device_address";
    private BluetoothAdapter bluetoothAdapter;
    ArrayAdapter<String> dispositivosEmparejadosArray;
    Set<BluetoothDevice> dispositivosVinculados;


    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (bluetoothAdapter != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN},
                        1);
            }
            bluetoothAdapter.cancelDiscovery();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Este dispositivo no tiene soporte de Bluetooth", Toast.LENGTH_SHORT).show();
            finish();
        }


        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_lista_dispositivos);
        setResult(Activity.RESULT_CANCELED);

        dispositivosEmparejadosArray = new ArrayAdapter<>(this, R.layout.item_list_devices);


        lista_dispositivos = findViewById(R.id.lista_dispositivos);
        lista_dispositivos.setAdapter(dispositivosEmparejadosArray);
        lista_dispositivos.setOnItemClickListener(dispositivoClickListener);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS}, 100);
        }
        dispositivosVinculados = bluetoothAdapter.getBondedDevices();

        if (dispositivosVinculados.size() > 0) {
            for (BluetoothDevice device : dispositivosVinculados) {
                dispositivosEmparejadosArray.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            dispositivosEmparejadosArray.add(getString(R.string.texto_no_hay_dispositivos));
        }

    }

    /**
     *Este AdapaterView lo que realiza es enviar el mac adress mediante el intent creado, cada
     * vez que damos click a in item
     */
    private final AdapterView.OnItemClickListener dispositivoClickListener=new AdapterView.OnItemClickListener() {
        @SuppressLint("MissingPermission")
        public void onItemClick(AdapterView<?> av, View v, int arg2, long args) {
            bluetoothAdapter.cancelDiscovery();
            String info = ((TextView) v).getText().toString();
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R){

            }

            if (info.length() > 16) {
                String address = info.substring(info.length() - 17);
                Intent intent = new Intent();
                intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
    };

}
package com.example.appcontacto;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.app.Activity;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * Clase que lista los dispositvos, tanto en la opcion de emparejamiento y la de dispositivos que
 * ya estan o estuvieron emparejados previamente
 */
public class ListaDispositivos extends Activity {

    ListView lista_dispositivos;


    public static final String EXTRA_DEVICE_ADDRESS = "device_address";
    private BluetoothAdapter bluetoothAdapter;
    ArrayAdapter<String> dispositivosEmparejadosArray;
    Set<BluetoothDevice> dispositivosVinculados;
    ArrayList list = new ArrayList();


    /**
     * Metodo que se ejecuta al cerrar la activity correspondiente o al cerrar la aplicacion
     */
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


    /**
     * Metodo onCreate del layout activity_lista_dispositivo
     * @param savedInstanceState Bundle de la Activity actual
     */
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

        //Metodo que estrablece el emparejamiento con el dispositivo seleccionado de la lista de items
        lista_dispositivos.setOnItemClickListener(dispositivoClickListener);





        //Checker de los permisos referentes a BLUETOOTH_CONNECT
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
     * Este metodo AdapaterView lo que realiza es enviar el mac adress mediante el intent creado, cada
     * vez que damos click a in item
     */
    private final AdapterView.OnItemClickListener dispositivoClickListener = new AdapterView.OnItemClickListener() {
        @SuppressLint("MissingPermission")
        public void onItemClick(AdapterView<?> av, View v, int i, long args) {
            bluetoothAdapter.cancelDiscovery();

            String info = ((TextView) v).getText().toString();
            list();

            if (info.length() > 16) {
                String address = info.substring(info.length() - 17);
                Intent intent = new Intent();
                intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
    };

    /**
     * Metodo que lista los dispositivos y hace que puedan ser seleccionados para emparejarlos
     */
    @SuppressLint("MissingPermission")
    public void list() {
        dispositivosVinculados = bluetoothAdapter.getBondedDevices();

        for (BluetoothDevice device : dispositivosVinculados)
            list.add(bluetoothAdapter.getName());
        Toast.makeText(getApplicationContext(), "Showing Paired Devices", Toast.LENGTH_SHORT).show();

        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);

        lista_dispositivos.setAdapter(adapter);

        lista_dispositivos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                String selected = (String) list.get(position);
                for (Iterator<BluetoothDevice> it = dispositivosVinculados.iterator(); it.hasNext(); ) {
                    BluetoothDevice bt = it.next();
                    if (bt.getName().equals(selected)) {
                        bt.createBond();
                    }
                }
            }

        });


    }
}


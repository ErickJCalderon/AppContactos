package com.example.appcontacto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.util.Log;
import android.view.Window;
import android.app.Activity;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Set;

public class ListaDispositivos extends AppCompatActivity {

    ListView lista_dispositivos;
    BluetoothConnectionServ bluetoothConnectionServ;
    BluetoothDevice btDevice;

    public static final String EXTRA_DEVICE_ADDRESS = "device_address";
    private BluetoothAdapter bluetoothAdapter;

    @SuppressLint("MissingPermission")
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (bluetoothAdapter !=null){
            bluetoothAdapter.cancelDiscovery();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null){
            Toast.makeText(this,"Este dispositivo no tiene soporte de Bluetooth", Toast.LENGTH_SHORT).show();
            finish();
        }


        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_lista_dispositivos);

        ArrayAdapter<String> dispositivosEmparejadosArray = new ArrayAdapter<>(this, R.layout.item_list_devices);

        final AdapterView.OnItemClickListener dispositivoClickListener = (av, view, position, id) -> {
            bluetoothAdapter.cancelDiscovery();

            String info = ((TextView) view).getText().toString();

            if(info.length() > 16 ){
                String address = info.substring(info.length() - 17 );
                Intent intent = new Intent();
                intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }

        };


        lista_dispositivos = findViewById(R.id.lista_dispositivos);
        lista_dispositivos.setAdapter(dispositivosEmparejadosArray);
        lista_dispositivos.setOnItemClickListener(dispositivoClickListener);

        Set<BluetoothDevice> dispositivosVinculados = bluetoothAdapter.getBondedDevices();

        if(dispositivosVinculados.size() > 0 ){
            for (BluetoothDevice device: dispositivosVinculados){
                dispositivosEmparejadosArray.add(device.getName() + "\n" + device.getAddress());
                device.createBond();
            }
        } else
        {
            dispositivosEmparejadosArray.add(getString(R.string.texto_no_hay_dispositivos));
        }





    }
}
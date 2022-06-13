package com.example.appcontacto;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Clase que controla un Adapter para listar los dispositivos, que extiende de un ArrayAdapter
 * referente a objetos de tipo BlueToothDevice
 */
public class DeviceListApadapter extends ArrayAdapter<BluetoothDevice> {

    private LayoutInflater mLayoutInflater;
    private ArrayList<BluetoothDevice> mDevices;
    private int  mViewResourceId;

    /**
     * Constructor del DeviceListAdapter
     *
     * @param context      View donde esta contenida
     * @param tvResourceId Id del componente donde se va mostrar la informacion
     * @param devices      ArrayList de objetos BluetoothDevice
     */
    public DeviceListApadapter(Context context, int tvResourceId, ArrayList<BluetoothDevice> devices){
        super(context, tvResourceId,devices);
        this.mDevices = devices;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = tvResourceId;
    }

    /**
     * View sobre la cual se va interactuar
     * @param position Posicion en la que se encuentra
     * @param convertView View sobre la que se va a operar
     * @param parent ViewGroup del cual procede
     * @return View ya transformada
     */
    @SuppressLint("MissingPermission")
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mLayoutInflater.inflate(mViewResourceId, null);
        BluetoothDevice device = mDevices.get(position);

        if (device != null) {
            TextView deviceName = (TextView) convertView.findViewById(R.id.tvDeviceName);
            TextView deviceAdress = (TextView) convertView.findViewById(R.id.tvDeviceAddress);

            if (deviceName != null) {
                deviceName.setText(device.getName());
            }
            if (deviceAdress != null) {
                deviceAdress.setText(device.getAddress());
            }
        }

        return convertView;
    }
}
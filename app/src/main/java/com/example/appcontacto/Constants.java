package com.example.appcontacto;

public interface Constants {
    long STATUS_UPDATE_INTERVAL    = 150;

    // Tipos de mensajes enviados al BluetoothService Handler
    int CONNECT_DEVICE_SECURE           = 9;
    int CONNECT_DEVICE_INSECURE         = 10;

    String DEVICE_NAME                  = "device_name";
    String TOAST                        = "toast";

    int BLUETOOTH_SERVICE_NOTIFICATION_ID           = 100;
}

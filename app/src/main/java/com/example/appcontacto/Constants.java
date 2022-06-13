package com.example.appcontacto;

/**
 * Clase que establece constantes para el control de contextos que se les pasa a los metodos de conexion de Bluetooth
 */
public interface Constants {
    long STATUS_UPDATE_INTERVAL    = 150;

    // Tipos de mensajes enviados al BluetoothService Handler
    int CONNECT_DEVICE_SECURE           = 9;
    int CONNECT_DEVICE_INSECURE         = 10;

    String DEVICE_NAME                  = "device_name";
    String TOAST                        = "toast";

    int BLUETOOTH_SERVICE_NOTIFICATION_ID           = 100;
}

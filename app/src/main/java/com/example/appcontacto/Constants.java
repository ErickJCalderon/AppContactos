package com.example.appcontacto;

/**
 * Interfaz que establece constantes para el control de contextos que se les pasa a los metodos de conexion de Bluetooth
 */
public interface Constants {
    /**
     * The constant STATUS_UPDATE_INTERVAL.
     */
    long STATUS_UPDATE_INTERVAL    = 150;

    /**
     * The constant CONNECT_DEVICE_SECURE.
     */
// Tipos de mensajes enviados al BluetoothService Handler
    int CONNECT_DEVICE_SECURE           = 9;
    /**
     * The constant CONNECT_DEVICE_INSECURE.
     */
    int CONNECT_DEVICE_INSECURE         = 10;

    /**
     * The constant DEVICE_NAME.
     */
    String DEVICE_NAME                  = "device_name";
    /**
     * The constant TOAST.
     */
    String TOAST                        = "toast";

    /**
     * The constant BLUETOOTH_SERVICE_NOTIFICATION_ID.
     */
    int BLUETOOTH_SERVICE_NOTIFICATION_ID           = 100;
}

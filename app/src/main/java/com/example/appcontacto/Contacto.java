package com.example.appcontacto;

/**
 * Clase que define los campos de un contacto para poder establecerlos en la app
 */
public class Contacto {
    String nombre;
    String apellido;
    String email;
    String numero;

    /**
     * Constructor de la clase
     * @param nombre Nombre del contacto
     * @param apellido Apellido del contacto
     * @param email E-mail del contacto
     * @param numero Numero del contacto
     */
    public Contacto(String nombre, String apellido, String email, String numero) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.numero = numero;

    }

    public Contacto() {

    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getApellido() {
        return apellido;
    }


}

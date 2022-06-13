package com.example.appcontacto;

/**
 * Clase que define los campos de un contacto para poder establecerlos en la app
 */
public class Contacto {
    /**
     * The Nombre.
     */
    String nombre;
    /**
     * The Apellido.
     */
    String apellido;
    /**
     * The Email.
     */
    String email;
    /**
     * The Numero.
     */
    String numero;

    /**
     * Constructor de la clase
     *
     * @param nombre   Nombre del contacto
     * @param apellido Apellido del contacto
     * @param email    E-mail del contacto
     * @param numero   Numero del contacto
     */
    public Contacto(String nombre, String apellido, String email, String numero) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.numero = numero;

    }

    /**
     * Instantiates a new Contacto.
     */
    public Contacto() {

    }

    /**
     * Gets nombre.
     *
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Sets nombre.
     *
     * @param nombre the nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Gets numero.
     *
     * @return the numero
     */
    public String getNumero() {
        return numero;
    }

    /**
     * Sets numero.
     *
     * @param numero the numero
     */
    public void setNumero(String numero) {
        this.numero = numero;
    }

    /**
     * Gets apellido.
     *
     * @return the apellido
     */
    public String getApellido() {
        return apellido;
    }


}

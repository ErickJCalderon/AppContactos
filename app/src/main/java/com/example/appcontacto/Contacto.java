package com.example.appcontacto;

public class Contacto {
    String nombre;
    String numero;

    public Contacto(String nombre, String numero) {
        this.nombre = nombre;
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


}

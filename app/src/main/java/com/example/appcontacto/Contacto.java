package com.example.appcontacto;

public class Contacto {
    String nombre;
    String apellido;
    String email;
    String numero;

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

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

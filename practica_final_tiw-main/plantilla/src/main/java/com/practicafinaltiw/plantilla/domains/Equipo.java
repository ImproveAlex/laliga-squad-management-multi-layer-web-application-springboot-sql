package com.practicafinaltiw.plantilla.domains;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

@Entity
public class Equipo {

    @Id
    private String nombre;

    @Lob
    private byte[] foto; // Image content as a byte array


    private int posicion;

    public Equipo() {
        // Constructor sin argumentos necesario para JPA
    }

    // Constructor con todos los argumentos
    public Equipo(String nombre, byte[] foto, int posicion) {
        this.nombre = nombre;
        this.foto = foto;
        this.posicion = posicion;
    }

    // Getters y setters

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }
}
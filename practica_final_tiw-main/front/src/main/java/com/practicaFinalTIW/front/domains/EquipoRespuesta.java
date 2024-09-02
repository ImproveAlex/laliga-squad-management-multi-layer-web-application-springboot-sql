package com.practicaFinalTIW.front.domains;

public class EquipoRespuesta {

    private String nombre;

    private byte[] foto; // Image content as a byte array

    private int posicion;

    public EquipoRespuesta() {
        // Constructor sin argumentos necesario para JPA
    }

    // Constructor con todos los argumentos
    public EquipoRespuesta(String nombre, byte[] foto, int posicion) {
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
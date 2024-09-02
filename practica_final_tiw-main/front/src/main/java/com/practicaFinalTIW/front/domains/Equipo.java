package com.practicaFinalTIW.front.domains;

public class Equipo {

    private String nombre;

    private String rutaFoto; // Ruta del directorio para la foto

    private int posicion;

    public Equipo() {
        // Constructor sin argumentos necesario para JPA
    }

    // Constructor con todos los argumentos
    public Equipo(String nombre, String rutaFoto, int posicion) {
        this.nombre = nombre;
        this.rutaFoto = rutaFoto;
        this.posicion = posicion;
    }

    // Getters y setters

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRutaFoto() {
        return rutaFoto;
    }

    public void setRutaFoto(String rutaFoto) {
        this.rutaFoto = rutaFoto;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }
}
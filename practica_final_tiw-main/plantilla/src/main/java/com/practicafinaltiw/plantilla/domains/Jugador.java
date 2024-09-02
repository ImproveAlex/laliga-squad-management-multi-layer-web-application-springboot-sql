package com.practicafinaltiw.plantilla.domains;

import java.io.Serializable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Jugador implements Serializable {
    public static final long serialVersionUID = 1L;

    @Id
    private String dni;

    private String nombre;
    private String apellidos;
    private String alias;
    private String posicion; // Puede ser 'delantero', 'defensa', 'medio' o 'portero'
    private String rutaFoto; // Ruta a la foto almacenada en el sistema de archivos local
    private String equipo; // Nombre del equipo al que pertenece el jugador

    public Jugador() {
    }

    public String getDni() {
        return this.dni;
    }

    public String getNombre() {
        return this.nombre;
    }

    public String getApellidos() {
        return this.apellidos;
    }

    public String getAlias() {
        return this.alias;
    }

    public String getPosicion() {
        return this.posicion;
    }

    public String getRutaFoto() {
        return this.rutaFoto;
    }

    public String getEquipo() {
        return this.equipo;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setPosicion(String posicion) {
        this.posicion = posicion;
    }

    public void setRutaFoto(String foto) {
        this.rutaFoto = foto;
    }

    public void setEquipo(String equipo) {
        this.equipo = equipo;
    }

}
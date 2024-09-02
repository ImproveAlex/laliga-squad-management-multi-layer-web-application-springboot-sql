package com.practicaFinalTIW.front.domains;

import java.io.Serializable;

public class Mensaje implements Serializable{
    public static final long serialVersionUID = 1L;

    private String emailori;

    private String emaildest;

    private String mensaje;

    public Mensaje() {
    }

    public String getEmailori() {
        return emailori;
    }

    public void setEmailori(String emailori) {
        this.emailori = emailori;
    }

    public String getEmaildest() {
        return emaildest;
    }

    public void setEmaildest(String emaildest) {
        this.emaildest = emaildest;
    }

    public String getMensaje() {
        return this.mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }


}
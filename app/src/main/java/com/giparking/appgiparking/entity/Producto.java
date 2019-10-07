package com.giparking.appgiparking.entity;

/**
 * Created by jledesma on 10/7/19.
 */

public class Producto {

    private String cod_producto;
    private String nombre_producto;
    private String tiene_convenio;

    public String getCod_producto() {
        return cod_producto;
    }

    public void setCod_producto(String cod_producto) {
        this.cod_producto = cod_producto;
    }

    public String getNombre_producto() {
        return nombre_producto;
    }

    public void setNombre_producto(String nombre_producto) {
        this.nombre_producto = nombre_producto;
    }

    public String getTiene_convenio() {
        return tiene_convenio;
    }

    public void setTiene_convenio(String tiene_convenio) {
        this.tiene_convenio = tiene_convenio;
    }
}

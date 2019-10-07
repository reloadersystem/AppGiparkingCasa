package com.giparking.appgiparking.entity;

/**
 * Created by jledesma on 10/7/19.
 */

public class Convenio {

    private String cod_producto;
    private String cod_convenio;
    private String empresa_nombre;

    public String getCod_producto() {
        return cod_producto;
    }

    public void setCod_producto(String cod_producto) {
        this.cod_producto = cod_producto;
    }

    public String getCod_convenio() {
        return cod_convenio;
    }

    public void setCod_convenio(String cod_convenio) {
        this.cod_convenio = cod_convenio;
    }

    public String getEmpresa_nombre() {
        return empresa_nombre;
    }

    public void setEmpresa_nombre(String empresa_nombre) {
        this.empresa_nombre = empresa_nombre;
    }
}

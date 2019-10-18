package com.giparking.appgiparking.entity;

/**
 * Created by jledesma on 10/17/19.
 */

public class Comprobante {

    private String cod_comprobante;
    private String fecha;
    private String numero;
    private String placa;
    private String cliente;
    private String monto;
    private String tipo;

    public String getCod_comprobante() {
        return cod_comprobante;
    }

    public void setCod_comprobante(String cod_comprobante) {
        this.cod_comprobante = cod_comprobante;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getMonto() {
        return monto;
    }

    public void setMonto(String monto) {
        this.monto = monto;
    }
}

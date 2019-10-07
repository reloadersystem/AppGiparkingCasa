package com.giparking.appgiparking.util;

import com.giparking.appgiparking.entity.Convenio;
import com.giparking.appgiparking.entity.Menu;
import com.giparking.appgiparking.entity.Producto;
import com.giparking.appgiparking.entity.TipoPago;

import java.util.List;

/**
 * Created by jledesma on 10/7/19.
 */

public class ContenedorClass {

    private static ContenedorClass instance;
    private List<Producto> list_producto;
    private List<Convenio> list_convenio;
    private List<TipoPago> list_tipopago;
    private List<Menu> list_menu;


    public static synchronized ContenedorClass getInstance(){
        if(instance==null){
            instance=new ContenedorClass();
        }
        return instance;
    }

    public ContenedorClass(){
        super();
    }


    public static void setInstance(ContenedorClass instance) {
        ContenedorClass.instance = instance;
    }

    public List<Producto> getList_producto() {
        return list_producto;
    }

    public void setList_producto(List<Producto> list_producto) {
        this.list_producto = list_producto;
    }

    public List<Convenio> getList_convenio() {
        return list_convenio;
    }

    public void setList_convenio(List<Convenio> list_convenio) {
        this.list_convenio = list_convenio;
    }

    public List<TipoPago> getList_tipopago() {
        return list_tipopago;
    }

    public void setList_tipopago(List<TipoPago> list_tipopago) {
        this.list_tipopago = list_tipopago;
    }

    public List<Menu> getList_menu() {
        return list_menu;
    }

    public void setList_menu(List<Menu> list_menu) {
        this.list_menu = list_menu;
    }
}

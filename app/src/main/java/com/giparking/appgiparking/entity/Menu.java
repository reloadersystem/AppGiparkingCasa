package com.giparking.appgiparking.entity;

/**
 * Created by jledesma on 10/7/19.
 */

public class Menu {

    private String cod_modulo;
    private String cod_menu;
    private String cod_taccion;
    private String menu_cod_padre;
    private String modulo;
    private String menu;
    private String menu_accion;
    private String menu_parametro;


    public String getCod_modulo() {
        return cod_modulo;
    }

    public void setCod_modulo(String cod_modulo) {
        this.cod_modulo = cod_modulo;
    }

    public String getCod_menu() {
        return cod_menu;
    }

    public void setCod_menu(String cod_menu) {
        this.cod_menu = cod_menu;
    }

    public String getCod_taccion() {
        return cod_taccion;
    }

    public void setCod_taccion(String cod_taccion) {
        this.cod_taccion = cod_taccion;
    }

    public String getMenu_cod_padre() {
        return menu_cod_padre;
    }

    public void setMenu_cod_padre(String menu_cod_padre) {
        this.menu_cod_padre = menu_cod_padre;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getMenu_accion() {
        return menu_accion;
    }

    public void setMenu_accion(String menu_accion) {
        this.menu_accion = menu_accion;
    }

    public String getMenu_parametro() {
        return menu_parametro;
    }

    public void setMenu_parametro(String menu_parametro) {
        this.menu_parametro = menu_parametro;
    }
}

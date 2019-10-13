package com.giparking.appgiparking.util;

import java.util.Calendar;

/**
 * Created by resembrink.correa on 10/13/19.
 */

public class ConversionTitulo {

    public String obtenerTitulo(String string)
    {
        String[] arrayNombre_2 = string.split(" ",2);

        int count = arrayNombre_2.length;
        String[] parts_titulo = new String[count];

        for (int i=0; i<arrayNombre_2.length; i++)
        {
            parts_titulo[i] = arrayNombre_2[i];
        }

        StringBuffer titulo= new StringBuffer();

        for (int i=0; i<parts_titulo.length; i++)
        {
            titulo = titulo.append(arrayNombre_2[i] + " \n");
        }

        String nombreFinal = titulo.toString();

        return nombreFinal;
    }
}

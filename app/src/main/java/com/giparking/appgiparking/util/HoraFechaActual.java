package com.giparking.appgiparking.util;

import android.content.Context;

import java.util.Calendar;

/**
 * Created by resembrink.correa on 10/12/19.
 */

public class HoraFechaActual {

    public static String obtenerFecha ()
    {
        Calendar cal = Calendar.getInstance();
        String year = String.valueOf(cal.get(Calendar.YEAR));
        String moth = String.valueOf(cal.get(Calendar.MONTH)+1);
        String dayofmonth = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        String fechaActual= String.format("%s/%s/%s", dayofmonth,moth,year);
        return fechaActual;
    }

    public static String obtenerHora()
    {
        Calendar cal = Calendar.getInstance();
        String hour= String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
        String minute = String.valueOf(cal.get(Calendar.MINUTE));
        String second = String.valueOf(cal.get(Calendar.SECOND));
        String horaActual= String.format("%s:%s:%s", hour, minute,second);
        return horaActual;
    }
}


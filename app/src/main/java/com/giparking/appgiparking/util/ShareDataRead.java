package com.giparking.appgiparking.util;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Silvia on 14/10/2019.
 */

public class ShareDataRead {

    static String PREFS_KEY = "Preferencia_ingreso";

    public static String obtenerValor(Context context, String keyPref) {

        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return preferences.getString(keyPref, "");
    }
    //  sharedata= ShareDataRead.obtenerValor(context, "valores_comprobante");
}

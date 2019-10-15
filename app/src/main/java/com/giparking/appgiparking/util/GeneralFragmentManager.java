package com.giparking.appgiparking.util;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.giparking.appgiparking.R;

/**
 * Created by resembrink.correa on 10/13/19.
 */

public class GeneralFragmentManager {

    public static void setFragmentWithReplace(Activity activity, int contenedor_id, Fragment fragment, Bundle bundle) {
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = ((AppCompatActivity) activity).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(contenedor_id, fragment);
        fragmentTransaction.addToBackStack("");
        fragmentTransaction.commit();
    }
}

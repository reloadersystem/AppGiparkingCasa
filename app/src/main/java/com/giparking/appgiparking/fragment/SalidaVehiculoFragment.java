package com.giparking.appgiparking.fragment;


import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.giparking.appgiparking.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SalidaVehiculoFragment extends Fragment {

    View rootview;
    CardView crd_valAutomatica;
    Fragment fragment;


    public SalidaVehiculoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview= inflater.inflate(R.layout.fragment_salida_vehiculo, container, false);
        getActivity().setTitle("Salida Vehículo");

        crd_valAutomatica = rootview.findViewById(R.id.crd_valAutomatica);


        crd_valAutomatica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragment = new VisorQRFragment();
                changeFragment();


            }
        });

        return rootview;
    }

    private void changeFragment() {

        FragmentManager fmanager = getActivity().getSupportFragmentManager();
        if (fmanager != null) {

            Bundle args = new Bundle();
            args.putString("ACCESO", "Placa");
            fragment.setArguments(args);

            FragmentTransaction ftransaction = fmanager.beginTransaction();
            if (ftransaction != null) {
                ftransaction.replace(R.id.contenedor, fragment);
                ftransaction.addToBackStack("");
                ftransaction.commit();
            }
        }
    }
}

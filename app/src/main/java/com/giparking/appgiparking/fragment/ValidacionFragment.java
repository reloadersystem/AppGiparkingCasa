package com.giparking.appgiparking.fragment;


import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.giparking.appgiparking.R;
import com.giparking.appgiparking.entity.Convenio;
import com.giparking.appgiparking.entity.Producto;
import com.giparking.appgiparking.util.ContenedorClass;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
    public class ValidacionFragment extends Fragment {


    View  rootview;
    
    Spinner sp_convenio;

    String convenioSelec;

    public ValidacionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview= inflater.inflate(R.layout.fragment_validacion, container, false);
        getActivity().setTitle("Validación Automática");

        // empresa_nombre - list_convenio
        
        sp_convenio = rootview.findViewById(R.id.sp_convenio);

        List<Convenio> arrayListConvenio = (List<Convenio>) ContenedorClass.getInstance().getList_convenio();

        int cantList = arrayListConvenio.size();

        ArrayList<String> arrayList = new ArrayList<String>();

        for (int i = 0; i < cantList; i++) {
            String dato = arrayListConvenio.get(i).getEmpresa_nombre().toString();
            arrayList.add(i, dato);
        }

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, arrayList);
        sp_convenio.setAdapter(adapter);

        sp_convenio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                convenioSelec = adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //pago por hora segun convenio


        return rootview;
    }

}

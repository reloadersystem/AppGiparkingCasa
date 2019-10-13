package com.giparking.appgiparking.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.giparking.appgiparking.R;
import com.giparking.appgiparking.entity.Convenio;
import com.giparking.appgiparking.util.ContenedorClass;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ValidacionFragment extends Fragment {


    View rootview;

    TextView placaData, horaFechaIngreso;

    Spinner sp_convenio;

    String convenioSelec;
    String qrResponse;
    String prodSeleccionado;

    TextView tv_Tarifario;

    public ValidacionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_validacion, container, false);
        getActivity().setTitle("Validación Automática");

        // empresa_nombre - list_convenio

        sp_convenio = rootview.findViewById(R.id.sp_convenio);
        tv_Tarifario = rootview.findViewById(R.id.tv_Tarifario);
        placaData = rootview.findViewById(R.id.tv_placa);
        horaFechaIngreso = rootview.findViewById(R.id.tv_fechaHoraIngreso);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            //descrip = bundle.getString("description");
            qrResponse = bundle.getString("QrResponse"); // QR Response llega  en String {hora,fecha,placa,.... trae la data que recupera del QR separado por comas
            prodSeleccionado = bundle.getString("ProductoSeleccionado");
        }


        String[] datosQRSeparator = qrResponse.split(",");


        String part1 = datosQRSeparator[0]; //hora
        String part2 = datosQRSeparator[1]; //fecha
        String part3 = datosQRSeparator[2]; //placa

        placaData.setText(part3);
        horaFechaIngreso.setText(part2 + " " + part1);
        tv_Tarifario.setText(prodSeleccionado);

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

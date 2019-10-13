package com.giparking.appgiparking.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.giparking.appgiparking.R;
import com.giparking.appgiparking.entity.Producto;
import com.giparking.appgiparking.util.ContenedorClass;
import com.giparking.appgiparking.util.HoraFechaActual;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SalidaVehiculoFragment extends Fragment {

    View rootview;
    CardView crd_valAutomatica;
    Fragment fragment;
    TextView horaIngreso, horaSalida;
    Spinner sp_producto;
    String prodSelec = " ";


    public SalidaVehiculoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_salida_vehiculo, container, false);
        getActivity().setTitle("Salida Vehículo");

        crd_valAutomatica = rootview.findViewById(R.id.crd_valAutomatica);
        horaIngreso = rootview.findViewById(R.id.tv_hora_ingreso);
        horaSalida = rootview.findViewById(R.id.tv_hora_salida);
        sp_producto = rootview.findViewById(R.id.sp_producto);

        List<Producto> arrayListProducto = (List<Producto>) ContenedorClass.getInstance().getList_producto();

        int cantList = arrayListProducto.size();

        ArrayList<String> arrayList = new ArrayList<String>();

        for (int i = 0; i < cantList; i++) {
            String dato = arrayListProducto.get(i).getNombre_producto().toString();
            arrayList.add(i, dato);
        }

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, arrayList);

        //Cargo el spinner con los datos
        sp_producto.setAdapter(adapter);

       sp_producto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                prodSelec = adapterView.getSelectedItem().toString();
           }

           @Override
           public void onNothingSelected(AdapterView<?> adapterView) {

           }
       });



        horaSalida.setText(HoraFechaActual.obtenerHora());


        crd_valAutomatica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragment = new ValidacionAutomaticaFragment();
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
            args.putString("HoraSalida", horaSalida.getText().toString());
            args.putString("ProductoSeleccionado",prodSelec);

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

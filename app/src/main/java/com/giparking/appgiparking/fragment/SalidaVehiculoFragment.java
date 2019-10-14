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

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class SalidaVehiculoFragment extends Fragment {

    @BindView(R.id.tv_placa)
    TextView tv_placa;

    @BindView(R.id.edt_fecha_ingreso)
    TextView edt_fecha_ingreso;

    @BindView(R.id.edt_hora_ingreso)
    TextView edt_hora_ingreso;



    View rootview;
    CardView crd_valAutomatica;
    Fragment fragment;
    TextView horaIngreso, horaSalida;
    Spinner sp_producto;
    String prodSelec = " ";

    String cod_movimiento = "";
    String nro_placa = "";
    String ingreso_fecha = "";
    String hora_fecha = "";



    public SalidaVehiculoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_salida_vehiculo, container, false);
        ButterKnife.bind(this,rootview);
        getActivity().setTitle("Salida Vehículo");

        crd_valAutomatica = rootview.findViewById(R.id.crd_valAutomatica);
        horaIngreso = rootview.findViewById(R.id.tv_hora_ingreso);
        horaSalida = rootview.findViewById(R.id.tv_hora_salida);
        sp_producto = rootview.findViewById(R.id.sp_producto);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            //descrip = bundle.getString("description");
            cod_movimiento = bundle.getString("cod_movimiento");
            nro_placa = bundle.getString("nro_placa");
            ingreso_fecha = bundle.getString("ingreso_fecha");
            hora_fecha = bundle.getString("hora_fecha");

            tv_placa.setText(nro_placa);
            edt_fecha_ingreso.setText(ingreso_fecha);
            edt_hora_ingreso.setText(hora_fecha);
        }

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



        //horaSalida.setText(HoraFechaActual.obtenerHora());


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

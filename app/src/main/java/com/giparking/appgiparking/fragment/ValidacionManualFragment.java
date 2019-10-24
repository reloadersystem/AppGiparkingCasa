package com.giparking.appgiparking.fragment;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.giparking.appgiparking.R;
import com.giparking.appgiparking.entity.GenericoSpinner;
import com.giparking.appgiparking.entity.Producto;
import com.giparking.appgiparking.util.ContenedorClass;
import com.giparking.appgiparking.util.TimePickerDialogFragment;
import com.google.android.gms.flags.IFlagProvider;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ValidacionManualFragment extends Fragment implements TimePickerDialog.OnTimeSetListener {

    TextView txt_Reloj,txt_Fecha;

    @BindView(R.id.edt_numPlaca)
    EditText edt_numPlaca;

    @BindView(R.id.sp_producto_manual)
    Spinner sp_producto_manual;

    Fragment fragment;

    String prodSelec = " ";
    String prodSelecItem = " ";

    CardView crd_valAutomatica;
    CardView crd_valManual;
    CardView crd_valSinValidacion;
    String convenio = " ";

    private static final String CERO = "0";
    private static final String BARRA = "/";

    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();

    //Variables para obtener la fecha
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);

    View rootview;


    public ValidacionManualFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_validacion_manual, container, false);
        ButterKnife.bind(this,rootview);
        getActivity().setTitle("Validación Manual");

        txt_Reloj = rootview.findViewById(R.id.txt_Reloj);
        txt_Fecha = rootview.findViewById(R.id.txt_Fecha);

        crd_valAutomatica = rootview.findViewById(R.id.cardview_v_automatica);
        crd_valManual = rootview.findViewById(R.id.cardview_v_manual);
        crd_valSinValidacion = rootview.findViewById(R.id.cardview_sin_validacion);

        String dia_formateado;
        String mes_formateado;

        int mes_uno = mes+1;

        if (String.valueOf(dia).length()==1){
            dia_formateado = "0" + dia;
        }
        else{
            dia_formateado = String.valueOf(dia);
        }

        if (String.valueOf(mes_uno).length()==1){
            mes_formateado = "0" + mes_uno;
        }
        else{
            mes_formateado = String.valueOf(mes_uno);
        }



        txt_Fecha.setText(dia_formateado + BARRA + mes_formateado + BARRA + anio);

        List<Producto> arrayListProducto = (List<Producto>) ContenedorClass.getInstance().getList_producto();

        //  int cantList = arrayListProducto.size();

        //  ArrayList<String> arrayList = new ArrayList<String>();

        //  for (int i = 0; i < cantList; i++) {
        //      String dato = arrayListProducto.get(i).getNombre_producto().toString();
        //      arrayList.add(i, dato);
        //  }

        processOnResponse(arrayListProducto,sp_producto_manual,"");

        //  ArrayAdapter<CharSequence> adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, arrayList);

        //Cargo el spinner con los datos
        //  sp_producto.setAdapter(adapter);

        sp_producto_manual.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {

                //  prodSelec = adapterView.getSelectedItem().toString();
                // prodSelecItem = adapterView.getSelectedItem().toString();

                prodSelec = ((GenericoSpinner) parent.getItemAtPosition(position)).name;
                prodSelecItem = ((GenericoSpinner) parent.getItemAtPosition(position)).id;
                convenio = ((GenericoSpinner) parent.getItemAtPosition(position)).convenio;


                if (convenio.equals("1")){

                    crd_valAutomatica.setVisibility(View.VISIBLE);
                    crd_valManual.setVisibility(View.VISIBLE);
                    crd_valSinValidacion.setVisibility(View.VISIBLE);

                }else{

                    crd_valAutomatica.setVisibility(View.GONE);
                    crd_valManual.setVisibility(View.GONE);
                    crd_valSinValidacion.setVisibility(View.VISIBLE);
                    fragment = new ValidacionDetalleSinValidacionFragment();
                    changeFragment(3);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        txt_Fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //llamarCalendario();
                obtenerFecha();
            }
        });


        txt_Reloj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                llamarReloj();

            }});



        return rootview;
    }

    private void llamarCalendario() {

        final Calendar calendario = Calendar.getInstance();
        int yy = calendario.get(Calendar.YEAR);
        int mm = calendario.get(Calendar.MONTH);
        int dd = calendario.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                String fecha = String.valueOf(dayOfMonth) + "/"+String.valueOf(monthOfYear)
                        +"/"+String.valueOf(year);
                txt_Fecha.setText(fecha);

            }
        }, yy, mm, dd);
        datePicker.getDatePicker().setMinDate(System.currentTimeMillis());
        datePicker.show();
    }

    private void llamarReloj() {
        TimePickerDialogFragment newFragment = new TimePickerDialogFragment();
        newFragment.setListener(this);
        newFragment.show(getChildFragmentManager(), "timePicker");
    }



    private void obtenerFecha() {
        DatePickerDialog recogerFecha = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
                final int mesActual = month + 1;
                //Formateo el día obtenido: antepone el 0 si son menores de 10
                String diaFormateado = (dayOfMonth < 10) ? CERO + String.valueOf(dayOfMonth) : String.valueOf(dayOfMonth);
                //Formateo el mes obtenido: antepone el 0 si son menores de 10
                String mesFormateado = (mesActual < 10) ? CERO + String.valueOf(mesActual) : String.valueOf(mesActual);
                //Muestro la fecha con el formato deseado
                txt_Fecha.setText(diaFormateado + BARRA + mesFormateado + BARRA + year);

            }


            //Estos valores deben ir en ese orden, de lo contrario no mostrara la fecha actual
            /**
             *También puede cargar los valores que usted desee
             */
        }, anio, mes, dia);
        //Muestro el widget
        recogerFecha.show();

    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        String AM_PM;

        String hourOfDay_input,minute_input;

        if (String.valueOf(hourOfDay).length() == 1){
            hourOfDay_input = "0"+String.valueOf(hourOfDay);

        }else{
            hourOfDay_input = String.valueOf(hourOfDay);
        }

        if (String.valueOf(minute).length() == 1){
            minute_input = "0"+String.valueOf(minute);

        }else{
            minute_input = String.valueOf(minute);
        }

        txt_Reloj.setText(hourOfDay_input+":"+minute_input);
    }

    @OnClick(R.id.cardview_v_automatica)
    public void irPantallaValidacionAutomatica(){


        fragment = new ScanQrConvenioFragment();
        changeFragment(1);
    }

    @OnClick(R.id.cardview_v_manual)
    public void irPantallaValidacionManual(){


        fragment = new ValidacionDetalleValidacionManualFragment();
        changeFragment(2);
    }

    @OnClick(R.id.cardview_sin_validacion)
    public void irPantallaSinValidacion(){


        fragment = new ValidacionDetalleSinValidacionFragment();
        changeFragment(3);
    }

    private void changeFragment(int tipo) {

        FragmentManager fmanager = getActivity().getSupportFragmentManager();
        if (fmanager != null) {

            if (tipo == 1){

                Bundle args = new Bundle();
                args.putString("placa", edt_numPlaca.getText().toString());
                args.putString("fecha", txt_Fecha.getText().toString());
                args.putString("hora", txt_Reloj.getText().toString());
                args.putString("cliente",prodSelec);
                args.putString("clienteid",prodSelecItem);
                args.putString("cod_movimiento","0");

                fragment.setArguments(args);
            }


            if (tipo == 3){

                Bundle args = new Bundle();
                args.putString("placa", edt_numPlaca.getText().toString());
                args.putString("fecha", txt_Fecha.getText().toString());
                args.putString("hora", txt_Reloj.getText().toString());
                args.putString("cliente",prodSelec);
                args.putString("clienteid",prodSelecItem);
                args.putString("cod_movimiento","0");

                fragment.setArguments(args);
            }

            if (tipo == 2){

                Bundle args = new Bundle();
                args.putString("placa", edt_numPlaca.getText().toString());
                args.putString("fecha", txt_Fecha.getText().toString());
                args.putString("hora", txt_Reloj.getText().toString());
                args.putString("cliente",prodSelec);
                args.putString("clienteid",prodSelecItem);
                args.putString("cod_movimiento","0");

                fragment.setArguments(args);
            }

           /* Bundle args = new Bundle();
            args.putString("ACCESO", "Placa");
            args.putString("HoraSalida", horaSalida.getText().toString());
            args.putString("ProductoSeleccionado",prodSelec);

            fragment.setArguments(args);*/

            FragmentTransaction ftransaction = fmanager.beginTransaction();
            if (ftransaction != null) {
                ftransaction.replace(R.id.contenedor, fragment);
                ftransaction.addToBackStack("");
                ftransaction.commit();
            }
        }
    }

    public void processOnResponse(List<Producto> result, Spinner spinner, String label) {
        LinkedList locations = new LinkedList();



        for (Producto itemLocation : result) {
            GenericoSpinner locationSpinner = new GenericoSpinner();
            locationSpinner.name = itemLocation.getNombre_producto();
            locationSpinner.id = itemLocation.getCod_producto();
            locationSpinner.convenio = itemLocation.getTiene_convenio();

            locations.add(locationSpinner);

        }

        //ArrayAdapter spinner_adapter = new ArrayAdapter(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, locations);
        ArrayAdapter spinner_adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, locations);
        //Añadimos el layout para el menú y se lo damos al spinner
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spinner_adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item_custom);
        spinner.setAdapter(spinner_adapter);
    }



}

package com.giparking.appgiparking.fragment;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.giparking.appgiparking.R;
import com.giparking.appgiparking.util.TimePickerDialogFragment;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class ValidacionManualFragment extends Fragment implements TimePickerDialog.OnTimeSetListener {

    TextView txt_Reloj,txt_Fecha;


    View rootview;


    public ValidacionManualFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_validacion_manual, container, false);

        txt_Reloj = rootview.findViewById(R.id.txt_Reloj);
        txt_Fecha = rootview.findViewById(R.id.txt_Fecha);

        txt_Fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llamarCalendario();
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

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        String AM_PM;
        if(hourOfDay < 12) {
            AM_PM = "a.m.";
        } else {
            AM_PM = "p.m.";
        }
        txt_Reloj.setText(hourOfDay + ":" + minute +" " + AM_PM);
    }



}

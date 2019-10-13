package com.giparking.appgiparking.util;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by resembrink.correa on 10/9/19.
 */

public class TimePickerDialogFragment extends DialogFragment  {

    private TimePickerDialog.OnTimeSetListener mListener;
    private Context context;

    public void setListener(TimePickerDialog.OnTimeSetListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        return new TimePickerDialog(context, mListener, hour, minute, DateFormat.is24HourFormat(context));
    }

    public String obtenerFechaActual()
    {
        Calendar cal = Calendar.getInstance();
        String year = String.valueOf(cal.get(Calendar.YEAR));
        String moth = String.valueOf(cal.get(Calendar.MONTH)+1);
        String dayofmonth = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        String fechaActual= String.format("%s/%s/%s", dayofmonth,moth,year);
        return fechaActual;
    }

    public String obtenerHoraActual()
    {
        Calendar cal = Calendar.getInstance();
        String hour= String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
        String minute = String.valueOf(cal.get(Calendar.MINUTE));
        String second = String.valueOf(cal.get(Calendar.SECOND));
        String horaActual= String.format("%s:%s:%s", hour, minute,second);

        return horaActual;
    }







}
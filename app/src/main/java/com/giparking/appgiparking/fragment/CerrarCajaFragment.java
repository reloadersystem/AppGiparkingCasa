package com.giparking.appgiparking.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.giparking.appgiparking.R;
import com.giparking.appgiparking.adapter.CerrarCajaAdapter;
import com.giparking.appgiparking.adapter.MovimientoAdapter;
import com.giparking.appgiparking.entity.Comprobante;
import com.giparking.appgiparking.entity.Movimiento;
import com.giparking.appgiparking.rest.HelperWs;
import com.giparking.appgiparking.rest.MethodWs;
import com.giparking.appgiparking.util.str_global;
import com.giparking.appgiparking.view.LoguinActivity;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class CerrarCajaFragment extends Fragment {

    SweetAlertDialog pd;

    @BindView(R.id.recycler_cierre)
    RecyclerView recycler_cierre;

    @BindView(R.id.recycler_resumen)
    RecyclerView recycler_resumen;

    @BindView(R.id.recycler_movimiento)
    RecyclerView recycler_movimiento;

    @BindView(R.id.recycler_compro)
    RecyclerView recycler_compro;

    private str_global a_str_global = str_global.getInstance();
    String descripcion_respuesta = "";

    ArrayList<Movimiento> list_movimiento_datos = new ArrayList<>();
    ArrayList<Movimiento> list_movimiento_resumen = new ArrayList<>();
    ArrayList<Movimiento> list_movimiento_movimiento = new ArrayList<>();
    ArrayList<Movimiento> list_movimiento_comprobantes = new ArrayList<>();

    public CerrarCajaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cerrar_caja, container, false);
        ButterKnife.bind(this, view);


        pd = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pd.getProgressHelper().setBarColor(Color.parseColor("#102670"));
        pd.setContentText("Por favor, espere...");
        pd.setCancelable(false);
        pd.show();

        String cod_cefectivo = a_str_global.getCod_cefectivo();

        MethodWs methodWs = HelperWs.getConfiguration().create(MethodWs.class);
        Call<ResponseBody> responseBodyCall = methodWs.cierreCajaMostrar(cod_cefectivo);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {

                    ResponseBody informacion = response.body();
                    try {
                        String cadena_respuesta = informacion.string();

                        String[] parts = cadena_respuesta.split("©");
                        String respuesta_validacion = parts[0];

                        String[] parts_validacion = respuesta_validacion.split("¦");
                        String codigo_respuesta = parts_validacion[0];
                        if (!codigo_respuesta.equals("0")) {
                            descripcion_respuesta = parts_validacion[1];
                        }

                        if (codigo_respuesta.equals("0")) {

                            //Extraemos valores datos generales
                            String valores_datos_generales = parts[1];
                            String[] parts_valores_datos_generaleso = valores_datos_generales.split("¬");

                            for (int i = 0; i < parts_valores_datos_generaleso.length; i++) {

                                String linea = parts_valores_datos_generaleso[i];

                                //Extraemos valores de cada dato
                                String[] parts_valores_datos_generaleso_detalle = linea.split("¦");

                                Movimiento movimiento = new Movimiento();
                                movimiento.setTitulo(parts_valores_datos_generaleso_detalle[0]);
                                movimiento.setValor(parts_valores_datos_generaleso_detalle[1]);
                                list_movimiento_datos.add(movimiento);
                            }

                            //Extraemos valores resumen
                            String valores_resumen = parts[2];
                            String[] parts_valores_resumen = valores_resumen.split("¬");

                            for (int i = 0; i < parts_valores_resumen.length; i++) {

                                String linea = parts_valores_resumen[i];

                                //Extraemos valores de cada dato
                                String[] parts_valores_resumen_detalle = linea.split("¦");

                                Movimiento movimiento = new Movimiento();
                                movimiento.setTitulo(parts_valores_resumen_detalle[0]);
                                movimiento.setValor(parts_valores_resumen_detalle[1]);
                                list_movimiento_resumen.add(movimiento);
                            }

                            //Extraemos valores movimiento
                            String valores_movimiento = parts[3];
                            String[] parts_valores_movimiento = valores_movimiento.split("¬");

                            for (int i = 0; i < parts_valores_movimiento.length; i++) {

                                String linea = parts_valores_movimiento[i];

                                //Extraemos valores de cada dato
                                String[] parts_valores_movimiento_detalle = linea.split("¦");

                                Movimiento movimiento = new Movimiento();
                                movimiento.setTitulo(parts_valores_movimiento_detalle[0]);
                                movimiento.setValor(parts_valores_movimiento_detalle[1]);
                                list_movimiento_movimiento.add(movimiento);
                            }

                            //Extraemos valores comprobante
                            String valores_comprobante = parts[4];
                            String[] parts_valores_comprobante = valores_comprobante.split("¬");

                            for (int i = 0; i < parts_valores_comprobante.length; i++) {

                                String linea = parts_valores_comprobante[i];

                                //Extraemos valores de cada dato
                                String[] parts_valores_comprobante_detalle = linea.split("¦");

                                Movimiento movimiento = new Movimiento();
                                movimiento.setTitulo(parts_valores_comprobante_detalle[0]);
                                movimiento.setValor(parts_valores_comprobante_detalle[1]);
                                list_movimiento_comprobantes.add(movimiento);
                            }

                            configurarAdapter(list_movimiento_datos, list_movimiento_resumen, list_movimiento_movimiento, list_movimiento_comprobantes);
                            pd.dismiss();
                        } else {

                            //Loguin Invalido

                            pd.dismiss();
                            pd = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
                            pd.getProgressHelper().setBarColor(Color.parseColor("#03A9F4"));
                            pd.setContentText(descripcion_respuesta);
                            pd.setCancelable(false);
                            pd.show();
                            return;
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        pd.dismiss();
                    }
                } else {
                    //ERROR
                    pd.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("jledesma", t.getMessage().toString());
                pd.dismiss();
            }
        });


        return view;
    }

    public void configurarAdapter(ArrayList<Movimiento> list_movimiento_datos, ArrayList<Movimiento> list_movimiento_resumen,
                                  ArrayList<Movimiento> list_movimiento_movimiento, ArrayList<Movimiento> list_movimiento_comprobantes) {

        recycler_cierre.setAdapter(new CerrarCajaAdapter(getContext(), list_movimiento_datos));
        recycler_resumen.setAdapter(new CerrarCajaAdapter(getContext(), list_movimiento_resumen));
        recycler_movimiento.setAdapter(new CerrarCajaAdapter(getContext(), list_movimiento_movimiento));
        recycler_compro.setAdapter(new CerrarCajaAdapter(getContext(), list_movimiento_comprobantes));

        recycler_cierre.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_resumen.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_movimiento.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_compro.setLayoutManager(new LinearLayoutManager(getContext()));

    }


    @OnClick(R.id.btn_cerrar_caja)
    public void cerrarCaja() {

        createLoginDialogo().show();
    }

    public AlertDialog createLoginDialogo() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_cerrar_caja, null);

        builder.setView(v);

        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                pd = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
                pd.getProgressHelper().setBarColor(Color.parseColor("#102670"));
                pd.setContentText("Por favor, espere...");
                pd.setCancelable(false);
                pd.show();

                String cod_cefectivo = a_str_global.getCod_cefectivo();

                MethodWs methodWs = HelperWs.getConfiguration().create(MethodWs.class);
                Call<ResponseBody> responseBodyCall = methodWs.cierreCajaGrabar(cod_cefectivo);
                responseBodyCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        if (response.isSuccessful()) {

                            ResponseBody informacion = response.body();
                            try {
                                String cadena_respuesta = informacion.string();

                                String[] parts = cadena_respuesta.split("©");
                                String respuesta_validacion = parts[0];

                                String[] parts_validacion = respuesta_validacion.split("¦");
                                String codigo_respuesta = parts_validacion[0];
                                if (!codigo_respuesta.equals("0")) {
                                    descripcion_respuesta = parts_validacion[1];
                                }

                                if (codigo_respuesta.equals("0")) {

                                    pd.dismiss();
                                    Toast.makeText(getContext(),"Caja cerrada!!",Toast.LENGTH_LONG).show();
                                    irLoguin();

                                } else {

                                    pd.dismiss();
                                    pd = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
                                    pd.getProgressHelper().setBarColor(Color.parseColor("#03A9F4"));
                                    pd.setContentText(descripcion_respuesta);
                                    pd.setCancelable(false);
                                    pd.show();
                                    return;
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                                pd.dismiss();
                            }
                        } else {
                            //ERROR
                            pd.dismiss();
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d("jledesma", t.getMessage().toString());
                        pd.dismiss();
                    }
                });

            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                    }
                });


        return builder.create();
    }

    public void irLoguin(){

        Intent i = new Intent(getContext(), LoguinActivity.class);
        startActivity(i);
    }


}

package com.giparking.appgiparking.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.giparking.appgiparking.R;
import com.giparking.appgiparking.rest.HelperWs;
import com.giparking.appgiparking.rest.MethodWs;
import com.giparking.appgiparking.util.str_global;
import com.giparking.appgiparking.view.LoguinActivity;

import java.io.IOException;

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
public class SalidaManualFragment extends Fragment {

    @BindView(R.id.edt_placa_parte_una)
    EditText edt_placa_parte_una;

    @BindView(R.id.edt_placa_parte_dos)
    EditText edt_placa_parte_dos;

    SweetAlertDialog pd;
    String descripcion_respuesta = "";

    private str_global a_str_global = str_global.getInstance();

    Fragment fragment = new SalidaVehiculoFragment();

    public SalidaManualFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_salida_manual, container, false);
        ButterKnife.bind(this, view);


        return view;
    }

    @OnClick(R.id.btn_dar_salida)
    public void DarSalidaManual() {


        String bus_criterio = "PLACA";
        String cod_sucursal = a_str_global.getCod_sucursal().toString();
        String cod_movimiento = "0";
        String nro_placa = "AAA123";

        pd = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pd.getProgressHelper().setBarColor(Color.parseColor("#102670"));
        pd.setContentText("Por favor, espere...");
        pd.setCancelable(false);
        pd.show();

        MethodWs methodWs = HelperWs.getConfiguration().create(MethodWs.class);
        Call<ResponseBody> responseBodyCall = methodWs.ControlAutoSalidaBusca(bus_criterio, cod_sucursal, cod_movimiento, nro_placa);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {

                    ResponseBody informacion = response.body();
                    try {
                        String cadena_respuesta = informacion.string();
                        String[] parts = cadena_respuesta.split("¬");
                        String respuesta_validacion = parts[0];

                        String[] parts_validacion = respuesta_validacion.split("¦");
                        String codigo_respuesta = parts_validacion[0];
                        if (!codigo_respuesta.equals("0")) {
                            descripcion_respuesta = parts_validacion[1];
                        }

                        if (codigo_respuesta.equals("0")) {

                            //Datos del movimiento
                            String valores_usuario = parts[1];
                            String[] parts_valores_usuario = valores_usuario.split("¦");

                            String cod_movimiento = parts_valores_usuario[0].toString();
                            String nro_placa = parts_valores_usuario[1].toString();;
                            String ingreso_fecha = parts_valores_usuario[2].toString();;
                            String hora_fecha = parts_valores_usuario[3].toString();;

                            /*Bundle bundle = new Bundle();
                            bundle.putString("cod_movimiento",cod_movimiento);
                            bundle.putString("nro_placa",nro_placa);
                            bundle.putString("ingreso_fecha",ingreso_fecha);
                            bundle.putString("hora_fecha",hora_fecha);*/
                            pd.dismiss();

                            FragmentManager fmanager = getActivity().getSupportFragmentManager();
                            if (fmanager != null) {

                                Bundle args = new Bundle();
                                args.putString("cod_movimiento",cod_movimiento);
                                args.putString("nro_placa",nro_placa);
                                args.putString("ingreso_fecha",ingreso_fecha);
                                args.putString("hora_fecha",hora_fecha);

                                fragment.setArguments(args);

                                FragmentTransaction ftransaction = fmanager.beginTransaction();
                                if (ftransaction != null) {
                                    ftransaction.replace(R.id.contenedor, fragment);
                                    ftransaction.addToBackStack("");
                                    ftransaction.commit();
                                }
                            }


                        }else{

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


}

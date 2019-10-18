package com.giparking.appgiparking.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.giparking.appgiparking.MenuActivity;
import com.giparking.appgiparking.R;
import com.giparking.appgiparking.rest.HelperWs;
import com.giparking.appgiparking.rest.MethodWs;
import com.giparking.appgiparking.util.str_global;
import com.giparking.appgiparking.view.LoguinActivity;

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
public class ValidacionDetalleSinValidacionFragment extends Fragment {

    @BindView(R.id.tv_placa_sin_validacion)
    TextView tv_placa_sin_validacion;

    @BindView(R.id.tv_fecha_hora_ingreso_sin_validacion)
    TextView tv_fecha_hora_ingreso_sin_validacion;

    @BindView(R.id.tv_tarifario_sin_validacion)
    TextView tv_tarifario_sin_validacion;

    @BindView(R.id.tv_tiempo_sin_validacion)
    TextView tv_tiempo_sin_validacion;

    @BindView(R.id.tv_monto_sin_validacion)
    TextView tv_monto_sin_validacion;

    @BindView(R.id.rb_boleta_sin_validacion)
    RadioButton rb_boleta_sin_validacion;

    @BindView(R.id.rb_factura_sin_validacion)
    RadioButton rb_factura_sin_validacion;

    @BindView(R.id.linear_grupo_ruc_sin_validacion)
    LinearLayout linear_grupo_ruc_sin_validacion;

    @BindView(R.id.edt_ruc_sin_validacion)
    EditText edt_ruc_sin_validacion;

    @BindView(R.id.edt_resultado_ruc_sin_validacion)
    EditText edt_resultado_ruc_sin_validacion;

    @BindView(R.id.btn_validar_sin_validacion)
    Button btn_validar_sin_validacion;

    String nro_placa, fecha, hora, cliente, clienteid;

    private str_global a_str_global = str_global.getInstance();

    SweetAlertDialog pd;
    String descripcion_respuesta ="";
    String cod_movimiento_input = "";
    String ruc_input = "0";

    public ValidacionDetalleSinValidacionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_validacion_detalle_sin_validacion, container, false);
        ButterKnife.bind(this, view);

        init();
        configurarEventos();
        callApiRestControlAutoSalida();


        return view;
    }

    private void configurarEventos() {

        rb_factura_sin_validacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                linear_grupo_ruc_sin_validacion.setVisibility(View.VISIBLE);
                edt_ruc_sin_validacion.setText("");
                edt_resultado_ruc_sin_validacion.setText("");

                btn_validar_sin_validacion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String ruc = edt_ruc_sin_validacion.getText().toString();

                        pd = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
                        pd.getProgressHelper().setBarColor(Color.parseColor("#102670"));
                        pd.setContentText("Por favor, espere...");
                        pd.setCancelable(false);
                        pd.show();


                        MethodWs methodWs = HelperWs.getConfiguration().create(MethodWs.class);
                        Call<ResponseBody> responseBodyCall = methodWs.comprobanteEmpresaBuscar(ruc);
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
                                        if (!codigo_respuesta.equals("0")) { //0 error en la validación de negocio
                                            descripcion_respuesta = parts_validacion[1];
                                        }

                                        //0¦¬10421586255¦ARANDA ROSALES JAIME JESUS¦150118¦AV. LIMA NORTE CHOSICA Nro.230

                                        if (codigo_respuesta.equals("0")) {

                                            String cliente = parts[1];
                                            String[] parts_cliente = cliente.split("¦");

                                            ruc_input =  parts_cliente[0];
                                            String razon_social = parts_cliente[1];
                                            String ubigeo = parts_cliente[2];
                                            String direccion = parts_cliente[3];

                                            edt_resultado_ruc_sin_validacion.setText(razon_social);

                                            pd.dismiss();

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
                });
            }
        });

        rb_boleta_sin_validacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                linear_grupo_ruc_sin_validacion.setVisibility(View.GONE);
                edt_ruc_sin_validacion.setText("");
                edt_resultado_ruc_sin_validacion.setText("");

                ruc_input = "0";
            }
        });

        if (rb_factura_sin_validacion.isChecked()){


        }
        else {


        }
    }

    private void callApiRestControlAutoSalida() {

        String t_validacion = "1";
        String cod_sucursal = a_str_global.getCod_sucursal().toString();
        String cod_producto = clienteid;
        final String cod_convenio = "0";
        String fecha_input = fecha;
        String hora_input = hora;
        String info = "";

        pd = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pd.getProgressHelper().setBarColor(Color.parseColor("#102670"));
        pd.setContentText("Por favor, espere...");
        pd.setCancelable(false);
        pd.show();

        MethodWs methodWs = HelperWs.getConfiguration().create(MethodWs.class);
        Call<ResponseBody> responseBodyCall = methodWs.ControlAutoSalidaCalcular(t_validacion, cod_sucursal, cod_producto, cod_convenio, fecha_input, hora_input, info, info, info, info, info, "0");
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
                        if (!codigo_respuesta.equals("0")) { //0 error en la validación de negocio
                            descripcion_respuesta = parts_validacion[1];
                        }

                        //0¦¬0¦19:39¦140.00

                        if (codigo_respuesta.equals("0")) {

                            String respuesta = parts[1];
                            String[] parts_respuesta = respuesta.split("¦");
                            String cod_convenio =  parts_respuesta[0];
                            String tiempo = parts_respuesta[1];
                            String monto = parts_respuesta[2];

                            tv_tiempo_sin_validacion.setText(tiempo);
                            tv_monto_sin_validacion.setText(monto);

                            pd.dismiss();

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

    @OnClick(R.id.btn_registrar_pago_sin_validacion)
    public void registrarPagoSinValidacion(){

        String tipo;

        String cod_corpempresa = a_str_global.getCod_corpempresa().toString();
        String cod_sucursal = a_str_global.getCod_sucursal().toString();
        String cod_cefectivo = a_str_global.getCod_cefectivo().toString();
        String cod_caja = a_str_global.getCod_caja().toString();;
        String cod_usuario = a_str_global.getCod_usuario().toString();

        if (rb_factura_sin_validacion.isChecked()){
            tipo = "1";
        }else{
            tipo = "2";
        }
        String i_cod_tcomprobante = tipo;
        String i_cod_movimiento = cod_movimiento_input;
        String i_cod_tvalidacion = "1";
        String i_cod_producto = clienteid;
        String i_cod_convenio = "0";
        String i_ingresa_fecha = fecha;
        String i_ingreso_hora = hora;
        String i_emp_ruc = ruc_input;


        String i_nro_placa = nro_placa;
        String i_conve_codigo = "";
        String i_conve_fecha = "";
        String i_conve_tipo = "";
        String i_conve_serie = "";
        String i_conve_numero = "";
        String i_conve_monto = "0";

        pd = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pd.getProgressHelper().setBarColor(Color.parseColor("#102670"));
        pd.setContentText("Por favor, espere...");
        pd.setCancelable(false);
        pd.show();


        MethodWs methodWs = HelperWs.getConfiguration().create(MethodWs.class);
        Call<ResponseBody> responseBodyCall = methodWs.controlAutoSalidaGrabar(cod_corpempresa,cod_sucursal,
                cod_cefectivo,cod_caja,cod_usuario,i_cod_tcomprobante,i_cod_movimiento,i_cod_tvalidacion,i_cod_producto,
                i_cod_convenio,i_ingresa_fecha,i_ingreso_hora,i_emp_ruc,i_nro_placa,i_conve_codigo,i_conve_fecha,
                i_conve_tipo,i_conve_serie,i_conve_numero,i_conve_monto);
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
                        if (!codigo_respuesta.equals("0")) { //0 error en la validación de negocio
                            descripcion_respuesta = parts_validacion[1];
                        }

                        //0¦¬0¦19:39¦140.00

                        if (codigo_respuesta.equals("0")) {

                            pd.dismiss();
                            Toast.makeText(getContext(),"Registrado correctamente!!",Toast.LENGTH_SHORT).show();
                            irMenuPrincipal();


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

    private void irMenuPrincipal() {

        Intent i = new Intent(getContext(), MenuActivity.class);
        startActivity(i);
    }

    private void init() {

        linear_grupo_ruc_sin_validacion.setVisibility(View.GONE);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            //descrip = bundle.getString("description");
            nro_placa = bundle.getString("placa");
            fecha = bundle.getString("fecha");
            hora = bundle.getString("hora");
            cliente = bundle.getString("cliente");
            clienteid = bundle.getString("clienteid");
            cod_movimiento_input = bundle.getString("cod_movimiento");


            tv_placa_sin_validacion.setText(nro_placa);
            tv_fecha_hora_ingreso_sin_validacion.setText(fecha + " " + hora);
            tv_tarifario_sin_validacion.setText(cliente);
        }
    }



}

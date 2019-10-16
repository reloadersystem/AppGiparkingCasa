package com.giparking.appgiparking.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.giparking.appgiparking.R;
import com.giparking.appgiparking.entity.Convenio;
import com.giparking.appgiparking.entity.GenericoSpinner;
import com.giparking.appgiparking.entity.Producto;
import com.giparking.appgiparking.rest.HelperWs;
import com.giparking.appgiparking.rest.MethodWs;
import com.giparking.appgiparking.util.ContenedorClass;
import com.giparking.appgiparking.util.str_global;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ValidacionDetalleValidacionManualFragment extends Fragment {

    @BindView(R.id.tv_placa_validacion_manual)
    TextView tv_placa_validacion_manual;

    @BindView(R.id.tv_fecha_hora_ingreso_validacion_manual)
    TextView tv_fecha_hora_ingreso_validacion_manual;

    @BindView(R.id.tv_tarifario_validacion_manual)
    TextView tv_tarifario_validacion_manual;

    @BindView(R.id.tv_tiempo_validacion_manual)
    TextView tv_tiempo_validacion_manual;

    @BindView(R.id.tv_monto_validacion_manual)
    TextView tv_monto_validacion_manual;

    @BindView(R.id.rb_boleta_validacion_manual)
    RadioButton rb_boleta_validacion_manual;

    @BindView(R.id.rb_factura_validacion_manual)
    RadioButton rb_factura_validacion_manual;

    @BindView(R.id.linear_grupo_ruc_validacion_manual)
    LinearLayout linear_grupo_ruc_validacion_manual;

    @BindView(R.id.edt_ruc_validacion_manual)
    EditText edt_ruc_validacion_manual;

    @BindView(R.id.edt_resultado_ruc_validacion_manual)
    EditText edt_resultado_ruc_validacion_manual;

    @BindView(R.id.btn_validar_validacion_manual)
    Button btn_validar_validacion_manual;

    @BindView(R.id.sp_convenio_validacion_manual)
    Spinner sp_convenio_validacion_manual;

    String nro_placa, fecha, hora, cliente, clienteid;

    private str_global a_str_global = str_global.getInstance();

    SweetAlertDialog pd;
    String descripcion_respuesta ="";

    String  cod_movimiento_input ="";

    String convenio,codigo_convenio;


    public ValidacionDetalleValidacionManualFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_validacion_detalle_validacion_manual, container, false);
        ButterKnife.bind(this,view);

        init();
        configurarEventos();
        //callApiRestControlAutoSalida();

        return view;
    }

    private void configurarEventos() {

        sp_convenio_validacion_manual.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {

                //  prodSelec = adapterView.getSelectedItem().toString();
                // prodSelecItem = adapterView.getSelectedItem().toString();

                convenio = ((GenericoSpinner) parent.getItemAtPosition(position)).name;
                codigo_convenio = ((GenericoSpinner) parent.getItemAtPosition(position)).id;

                if (codigo_convenio.equals("-1")){

                    tv_tiempo_validacion_manual.setText("");
                    tv_monto_validacion_manual.setText("");
                }
                else{

                    callApiRestControlAutoSalida();
                }



            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        rb_factura_validacion_manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                linear_grupo_ruc_validacion_manual.setVisibility(View.VISIBLE);
                edt_ruc_validacion_manual.setText("");
                edt_resultado_ruc_validacion_manual.setText("");

                btn_validar_validacion_manual.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String ruc = edt_ruc_validacion_manual.getText().toString();

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

                                            String ruc =  parts_cliente[0];
                                            String razon_social = parts_cliente[1];
                                            String ubigeo = parts_cliente[2];
                                            String direccion = parts_cliente[3];

                                            edt_resultado_ruc_validacion_manual.setText(razon_social);

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

        rb_boleta_validacion_manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                linear_grupo_ruc_validacion_manual.setVisibility(View.GONE);
                edt_ruc_validacion_manual.setText("");
                edt_resultado_ruc_validacion_manual.setText("");
            }
        });


    }

    private void callApiRestControlAutoSalida() {

        String t_validacion = "2";
        String cod_sucursal = a_str_global.getCod_sucursal().toString();
        String cod_producto = clienteid;
        String cod_convenio = codigo_convenio;
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

                            tv_tiempo_validacion_manual.setText(tiempo);
                            tv_monto_validacion_manual.setText(monto);

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

    private void init() {

        linear_grupo_ruc_validacion_manual.setVisibility(View.GONE);

        List<Convenio> arrayListProducto = (List<Convenio>) ContenedorClass.getInstance().getList_convenio();

        processOnResponse(arrayListProducto,sp_convenio_validacion_manual,"Seleccione un convenio");

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            //descrip = bundle.getString("description");
            nro_placa = bundle.getString("placa");
            fecha = bundle.getString("fecha");
            hora = bundle.getString("hora");
            cliente = bundle.getString("cliente");
            clienteid = bundle.getString("clienteid");
            cod_movimiento_input = bundle.getString("cod_movimiento");

            tv_placa_validacion_manual.setText(nro_placa);
            tv_fecha_hora_ingreso_validacion_manual.setText(fecha + " " + hora);
            tv_tarifario_validacion_manual.setText(cliente);
        }
    }

    public void processOnResponse(List<Convenio> result, Spinner spinner, String label) {
        LinkedList locations = new LinkedList();

        GenericoSpinner prioridadSpinnerLabel = new GenericoSpinner();
        prioridadSpinnerLabel.name = label;
        prioridadSpinnerLabel.id = "-1";
        locations.add(prioridadSpinnerLabel);

        for (Convenio itemLocation : result) {
            GenericoSpinner locationSpinner = new GenericoSpinner();
            locationSpinner.name = itemLocation.getEmpresa_nombre();
            locationSpinner.id = itemLocation.getCod_convenio();

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

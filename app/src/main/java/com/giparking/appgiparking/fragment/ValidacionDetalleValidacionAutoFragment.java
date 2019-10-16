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
public class ValidacionDetalleValidacionAutoFragment extends Fragment {

    @BindView(R.id.tv_placa_validacion_auto)
    TextView tv_placa_validacion_auto;

    @BindView(R.id.tv_fecha_hora_ingreso_validacion_auto)
    TextView tv_fecha_hora_ingreso_validacion_auto;

    @BindView(R.id.tv_tarifario_validacion_auto)
    TextView tv_tarifario_validacion_auto;

    @BindView(R.id.tv_tiempo_validacion_auto)
    TextView tv_tiempo_validacion_auto;

    @BindView(R.id.tv_monto_validacion_auto)
    TextView tv_monto_validacion_auto;

    @BindView(R.id.rb_boleta_validacion_auto)
    RadioButton rb_boleta_validacion_auto;

    @BindView(R.id.rb_factura_validacion_auto)
    RadioButton rb_factura_validacion_auto;

    @BindView(R.id.linear_grupo_ruc_validacion_auto)
    LinearLayout linear_grupo_ruc_validacion_auto;

    @BindView(R.id.edt_ruc_validacion_auto)
    EditText edt_ruc_validacion_auto;

    @BindView(R.id.edt_resultado_ruc_validacion_auto)
    EditText edt_resultado_ruc_validacion_auto;

    @BindView(R.id.btn_validar_validacion_auto)
    Button btn_validar_validacion_auto;

    @BindView(R.id.sp_convenio_validacion_auto)
    Spinner sp_convenio_validacion_auto;

    String nro_placa, fecha, hora, cliente, clienteid,qrDataRecieve;
    String ruc,tipo_documento,serie,numero,total_igv,total_comprobante,fecha_emision,tipo_documento_adquiriente,
    numero_documento_adquiriente,valor_resumen;

    private str_global a_str_global = str_global.getInstance();

    SweetAlertDialog pd;
    String descripcion_respuesta ="";

    String convenio,codigo_convenio;

    List<Convenio> arrayListProducto;


    public ValidacionDetalleValidacionAutoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_validacion_detalle_validacion_auto, container, false);
        ButterKnife.bind(this,view);

        init();
        configurarEventos();
        callApiRestControlAutoSalida();

        return view;
    }

    private void configurarEventos() {



        sp_convenio_validacion_auto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {

                //  prodSelec = adapterView.getSelectedItem().toString();
                // prodSelecItem = adapterView.getSelectedItem().toString();

                convenio = ((GenericoSpinner) parent.getItemAtPosition(position)).name;
                codigo_convenio = ((GenericoSpinner) parent.getItemAtPosition(position)).id;

              /*  if (codigo_convenio.equals("-1")){

                    tv_tiempo_validacion_auto.setText("");
                    tv_monto_validacion_auto.setText("");
                }
                else{

                    callApiRestControlAutoSalida();
                }*/



            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        rb_factura_validacion_auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                linear_grupo_ruc_validacion_auto.setVisibility(View.VISIBLE);
                edt_ruc_validacion_auto.setText("");
                edt_resultado_ruc_validacion_auto.setText("");

                btn_validar_validacion_auto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String ruc = edt_ruc_validacion_auto.getText().toString();

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

                                            edt_resultado_ruc_validacion_auto.setText(razon_social);

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

        rb_boleta_validacion_auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                linear_grupo_ruc_validacion_auto.setVisibility(View.GONE);
                edt_ruc_validacion_auto.setText("");
                edt_resultado_ruc_validacion_auto.setText("");
            }
        });
    }

    private void init() {

        linear_grupo_ruc_validacion_auto.setVisibility(View.GONE);

        sp_convenio_validacion_auto.setEnabled(false);

        arrayListProducto = (List<Convenio>) ContenedorClass.getInstance().getList_convenio();

        processOnResponse(arrayListProducto,sp_convenio_validacion_auto,"Seleccione un convenio");

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            //descrip = bundle.getString("description");

            qrDataRecieve = bundle.getString("qrDataRecieve");
            nro_placa = bundle.getString("placa");
            fecha = bundle.getString("fecha");
            hora = bundle.getString("hora");
            cliente = bundle.getString("cliente");
            clienteid = bundle.getString("clienteid");

            String cadena_respuesta = qrDataRecieve;
            String[] parts = cadena_respuesta.split("\\|");
            ruc = parts[0];
            tipo_documento = parts[1];
            serie = parts[2];
            numero = parts[3];
            total_comprobante = parts[5];
            fecha_emision = parts[6];


            tv_placa_validacion_auto.setText(nro_placa);
            tv_fecha_hora_ingreso_validacion_auto.setText(fecha + " " + hora);
            tv_tarifario_validacion_auto.setText(cliente);
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

    private void callApiRestControlAutoSalida() {

        String t_validacion = "3";
        String cod_sucursal = a_str_global.getCod_sucursal().toString();
        String cod_producto = clienteid;
        String cod_convenio = "0";
        String fecha_input = fecha;
        String hora_input = hora;
        String conve_codigo = ruc;
        String conve_fecha = fecha_emision;
        String conve_tipo = tipo_documento;
        String conve_serie = serie;
        String conve_numero = numero;
        String conve_monto = total_comprobante;

        pd = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pd.getProgressHelper().setBarColor(Color.parseColor("#102670"));
        pd.setContentText("Por favor, espere...");
        pd.setCancelable(false);
        pd.show();

        MethodWs methodWs = HelperWs.getConfiguration().create(MethodWs.class);
        Call<ResponseBody> responseBodyCall = methodWs.ControlAutoSalidaCalcular(t_validacion, cod_sucursal, cod_producto, cod_convenio, fecha_input, hora_input, conve_codigo, conve_fecha, conve_tipo, conve_serie, conve_numero, conve_monto);
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

                            tv_tiempo_validacion_auto.setText(tiempo);
                            tv_monto_validacion_auto.setText(monto);
                            sp_convenio_validacion_auto.setEnabled(true);

                            int position_ubica = f_read_ubicacion_position(cod_convenio);
                            sp_convenio_validacion_auto.setSelection(position_ubica);



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

    public int f_read_ubicacion_position(String ubicacion) {
        int li_return = -1;
        if (arrayListProducto == null) {
            return li_return;
        }
        int row = arrayListProducto.size();
        for (int i = 0; i < row; i++) {
            if (arrayListProducto.get(i).getCod_convenio().equals(ubicacion)) {
                li_return = i+1;
                break;
            }
        }
        return li_return;
    }

}

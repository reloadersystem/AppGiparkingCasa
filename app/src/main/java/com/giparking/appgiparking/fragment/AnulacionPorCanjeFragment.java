package com.giparking.appgiparking.fragment;


import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.giparking.appgiparking.R;
import com.giparking.appgiparking.adapter.AnularPorCanjeAdapter;
import com.giparking.appgiparking.adapter.AnularPorErrorAdapter;
import com.giparking.appgiparking.entity.Comprobante;
import com.giparking.appgiparking.rest.HelperWs;
import com.giparking.appgiparking.rest.MethodWs;
import com.giparking.appgiparking.util.str_global;

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
public class AnulacionPorCanjeFragment extends Fragment {


    @BindView(R.id.rb_comprobante_anular)
    RadioButton rb_comprobante_anular;

    @BindView(R.id.rb_todos_anular)
    RadioButton rb_todos_anular;

    @BindView(R.id.edt_serie_buscar)
    EditText edt_serie_buscar;

    @BindView(R.id.edt_numero_buscar)
    EditText edt_numero_buscar;

    @BindView(R.id.btn_buscar_comprobantes_anular)
    Button btn_buscar_comprobantes_anular;

    @BindView(R.id.recycler_comprobantes_anular)
    RecyclerView recycler_comprobantes_anular;

    SweetAlertDialog pd;

    String descripcion_respuesta = "";

    private str_global a_str_global = str_global.getInstance();

    String serie = "";
    String numero = "";
    String bus_criterio_input = "TODOS";
    String cod_caja_input = a_str_global.getCod_caja();

    AnularPorCanjeAdapter adapter;

    public AnulacionPorCanjeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_anulacion_por_canje, container, false);

        ButterKnife.bind(this, view);

        getActivity().setTitle("Anulacion");

        init();
        configurarEventos();
        callApiRestImprimirMostrar();

        return view;
    }

    private void init() {

        edt_numero_buscar.setEnabled(false);
        edt_serie_buscar.setEnabled(false);
    }

    public void configurarAdapter(ArrayList<Comprobante> list_comprobante) {

        adapter = new AnularPorCanjeAdapter(getContext(), list_comprobante);
        recycler_comprobantes_anular.setAdapter(adapter);
        recycler_comprobantes_anular.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter.setOnItemClickListener(new AnularPorErrorAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Comprobante comprobante) {

              //  createLoginDialogoPrimero(comprobante).show();
            }
        });

    }





    private void configurarEventos() {

        rb_comprobante_anular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                edt_numero_buscar.setEnabled(true);
                edt_serie_buscar.setEnabled(true);
                //cod_caja_input = "0";
            }
        });

        rb_todos_anular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                edt_numero_buscar.setEnabled(false);
                edt_serie_buscar.setEnabled(false);
                edt_numero_buscar.setText("");
                edt_serie_buscar.setText("");
                //cod_caja_input = a_str_global.getCod_sucursal();
                bus_criterio_input = "TODOS";
            }
        });


    }

    @OnClick(R.id.btn_buscar_comprobantes_anular)
    public void buscarComprobante() {
        callApiRestImprimirMostrar();
    }

    public void callApiRestImprimirMostrar() {

        pd = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pd.getProgressHelper().setBarColor(Color.parseColor("#102670"));
        pd.setContentText("Por favor, espere...");
        pd.setCancelable(false);
        pd.show();

        if (rb_comprobante_anular.isChecked()) {
            //  nro_placa = edt_placa_buscar.getText().toString();
            bus_criterio_input = "DOCUMENTO";

            if ( edt_serie_buscar.getText().toString().equals("")){

                pd = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
                pd.getProgressHelper().setBarColor(Color.parseColor("#102670"));
                pd.setContentText("Debe ingresar la serie");
                pd.setCancelable(false);
                pd.show();
            }

            if ( edt_numero_buscar.getText().toString().equals("")){

                pd = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
                pd.getProgressHelper().setBarColor(Color.parseColor("#102670"));
                pd.setContentText("Debe ingresar el numero");
                pd.setCancelable(false);
                pd.show();
            }

        }

        String bus_criterio = bus_criterio_input;
        String cod_sucursal = a_str_global.getCod_sucursal(); //"217"; //
        String cod_caja = cod_caja_input; //"0"; //
        String serie = edt_serie_buscar.getText().toString();

        String numero;
        if (edt_serie_buscar.getText().toString().equals("")) {
            numero = "0";
        } else {
            numero = edt_serie_buscar.getText().toString();
        }


        MethodWs methodWs = HelperWs.getConfiguration().create(MethodWs.class);
        Call<ResponseBody> responseBodyCall = methodWs.comprobanteAnularCanjeMostrar(bus_criterio, cod_sucursal, cod_caja, serie, numero);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                //2170192225¦16/10/2019 15:06¦BOLETA DE VENTA¦BB07-2225¦1A9394¦CLIENTES VARIOS¦3.00¬
                //COD_COMPROBANTE¦FECHA¦TIPO COMPROBANTE¦NUM_COMPROBANTE¦NRO_PLACA¦CLIENTE¦TOTAL_DOCUMENTO
                if (response.isSuccessful()) {

                    ArrayList<Comprobante> list_comprobante = new ArrayList<>();
                    ResponseBody informacion = response.body();
                    try {

                        String cadena_respuesta = informacion.string();

                        if (cadena_respuesta.equals("")) {

                            configurarAdapter(list_comprobante);
                            pd.dismiss();
                        }

                        String[] parts_valores = cadena_respuesta.split("¬");

                        for (int i = 0; i < parts_valores.length; i++) {

                            String linea = parts_valores[i];

                            //Extraemos valores de cada producto
                            String[] parts_valores_detalle = linea.split("¦");


                            Comprobante comprobante = new Comprobante();
                            comprobante.setCod_comprobante(parts_valores_detalle[0]);
                            comprobante.setFecha(parts_valores_detalle[1]);
                            comprobante.setTipo(parts_valores_detalle[2]);
                            comprobante.setNumero(parts_valores_detalle[3]);
                            comprobante.setPlaca(parts_valores_detalle[4]);
                            comprobante.setCliente(parts_valores_detalle[5]);
                            comprobante.setMonto(parts_valores_detalle[6]);

                            list_comprobante.add(comprobante);

                        }

                        configurarAdapter(list_comprobante);
                        pd.dismiss();

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

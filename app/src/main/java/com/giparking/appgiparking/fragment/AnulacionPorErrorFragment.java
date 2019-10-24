package com.giparking.appgiparking.fragment;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
import android.widget.Toast;

import com.giparking.appgiparking.R;
import com.giparking.appgiparking.adapter.AnularPorErrorAdapter;
import com.giparking.appgiparking.adapter.ComprobanteAdapter;
import com.giparking.appgiparking.entity.Comprobante;
import com.giparking.appgiparking.entity.Menu;
import com.giparking.appgiparking.rest.HelperWs;
import com.giparking.appgiparking.rest.MethodWs;
import com.giparking.appgiparking.util.PrinterCommands;
import com.giparking.appgiparking.util.Utils;
import com.giparking.appgiparking.util.str_global;
import com.giparking.appgiparking.view.LoguinActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.giparking.appgiparking.fragment.IngresoPrintFragment.PREFS_KEY;

/**
 * A simple {@link Fragment} subclass.
 */
public class AnulacionPorErrorFragment extends Fragment {

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
    SweetAlertDialog pd2;

    String descripcion_respuesta = "";

    private str_global a_str_global = str_global.getInstance();

    String serie = "";
    String numero = "";
    String bus_criterio_input = "TODOS";
    String cod_caja_input = a_str_global.getCod_caja();

    AnularPorErrorAdapter adapter;

    //data print
    String cod_comprobanteComp;
    String comprobante_tipo;
    String comprobante_numero;
    String comprobante_fecha;
    String comprobante_usuario_loguin;
    String cliente_tipo;
    String cliente_documento;
    String cliente_nombre;
    String documento_referencial;
    String comprobamte_total_operacion_gravadas;
    String comprobamte_total_impuesto;
    String comprobamte_total_documento;

    String movimiento_nro_placa;
    String movimiento_hora_ingreso;
    String movimiento_hora_salida;
    String movimiento_tiempo_calculado;
    String detalle_producto_nombre;
    String detalle_importe;
    String datos_qr;

    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;
    BluetoothDevice bluetoothDevice;
    OutputStream outputStream;
    InputStream inputStream;
    Bitmap bitmap;
    volatile boolean stopWorker;


    public AnulacionPorErrorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_anulacion_por_error, container, false);
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

        adapter = new AnularPorErrorAdapter(getContext(), list_comprobante);
        recycler_comprobantes_anular.setAdapter(adapter);
        recycler_comprobantes_anular.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter.setOnItemClickListener(new AnularPorErrorAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Comprobante comprobante) {

                createLoginDialogo(comprobante).show();
            }
        });

    }

    public AlertDialog createLoginDialogo(final Comprobante comprobante) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_anular, null);

        builder.setView(v);

        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                pd = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
                pd.getProgressHelper().setBarColor(Color.parseColor("#102670"));
                pd.setContentText("Por favor, espere...");
                pd.setCancelable(false);
                pd.show();

                String comprobante_input = comprobante.getCod_comprobante().toString();
                String cod_sucursal = a_str_global.getCod_sucursal();
                String cod_cefectivo = a_str_global.getCod_cefectivo();
                String cod_caja = a_str_global.getCod_caja();
                String cod_usuario = a_str_global.getCod_usuario();

                MethodWs methodWs = HelperWs.getConfiguration().create(MethodWs.class);
                Call<ResponseBody> responseBodyCall = methodWs.comprobanteAnularErrorGrabar(comprobante_input, cod_sucursal, cod_cefectivo, cod_caja, cod_usuario);
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

                                    String respuesta_imprimir = parts[1];
                                    String[] partSplit = respuesta_imprimir.split("¦");

                                    cod_comprobanteComp = partSplit[0];
                                    comprobante_tipo = partSplit[1];
                                    comprobante_numero = partSplit[2];
                                    comprobante_fecha = partSplit[3];
                                    comprobante_usuario_loguin = partSplit[4];
                                    cliente_tipo = partSplit[5];
                                    cliente_documento = partSplit[6];
                                    cliente_nombre = partSplit[7];
                                    documento_referencial = partSplit[8];
                                    comprobamte_total_operacion_gravadas = partSplit[9];
                                    comprobamte_total_impuesto = partSplit[10];
                                    comprobamte_total_documento = partSplit[11];

                                    movimiento_nro_placa = partSplit[12];
                                    movimiento_hora_ingreso = partSplit[13];
                                    movimiento_hora_salida = partSplit[14];
                                    movimiento_tiempo_calculado = partSplit[15];
                                    detalle_producto_nombre = partSplit[16];
                                    detalle_importe = partSplit[17];
                                    datos_qr = partSplit[18];


                                    FindBluetoothDevice();
                                    openBluetoothPrinter(datos_qr);
                                    try {
                                        disconnectBT();
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }

                                  /*  new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("Informativo")
                                            .setContentText("Comprobante Anulado!")
                                            .setConfirmText("Continuar")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {

                                                    callApiRestImprimirMostrar();
                                                    adapter.notifyDataSetChanged();

                                                }
                                            }).show();*/

                                    pd.dismiss();
                                    pd2 = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
                                    pd2.getProgressHelper().setBarColor(Color.parseColor("#03A9F4"));
                                    pd2.setContentText("Comprobante anulado!!");
                                    pd2.setCancelable(false);
                                    pd2.show();

                                    callApiRestImprimirMostrar();
                                    adapter.notifyDataSetChanged();

                                    return;


                                } else {

                                    pd.dismiss();
                                    pd = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
                                    pd.getProgressHelper().setBarColor(Color.parseColor("#03A9F4"));
                                    pd.setContentText(descripcion_respuesta);
                                    pd.setCancelable(false);
                                    pd.show();
                                    return;
                                }

                            } catch (IOException ex) {
                                ex.printStackTrace();
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
        }).

                setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                    }
                });


        return builder.create();
    }

    private void FindBluetoothDevice() {

        try {

            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                // lblPrinterName.setText("No Bluetooth Adapter found");
            }
            if (bluetoothAdapter.isEnabled()) {
                Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBT, 0);
            }

            Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();

            String printerPortable = obtenerValor(getContext(), "key_printer");
            if (pairedDevice.size() > 0) {
                for (BluetoothDevice pairedDev : pairedDevice) {

                    // My Bluetoth printer name is BTP_F09F1A
                    if (pairedDev.getName().equals(printerPortable)) {
                        bluetoothDevice = pairedDev;
                        //  lblPrinterName.setText("Bluetooth Printer Attached: " + pairedDev.getName());
                        break;
                    }
                }
            }

            //  lblPrinterName.setText("Bluetooth Printer Attached");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void openBluetoothPrinter(String msg) throws IOException {

        try {

            //Standard uuid from string //


            // beginListenData();
            try {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                // String msg = "QRPARKING FACIL";

                BitMatrix bitMatrix = multiFormatWriter.encode(msg, BarcodeFormat.QR_CODE, 200, 200);  //2000, 2000
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                bitmap = barcodeEncoder.createBitmap(bitMatrix);

                UUID uuidSting = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
                bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuidSting);
                bluetoothSocket.connect();
                outputStream = bluetoothSocket.getOutputStream();
                inputStream = bluetoothSocket.getInputStream();

                String nombreEmpresa = str_global.getInstance().getVar_cabecera_c_0();
                String direccionEmpresa = str_global.getInstance().getVar_cabecera_t_1() + " \n" + str_global.getInstance().getVar_cabecera_t_2();
                String cajaNum = str_global.getInstance().getCaja_nombre();
                byte[] printformat = new byte[]{0x1B, 0x21, 0x03};
                outputStream.write(printformat);
                printCustom(nombreEmpresa, 1, 1);
                printCustom(direccionEmpresa, 0, 1);
                printNewLine();
                printCustom(comprobante_tipo + " Nro:", 0, 0);
                printCustom(comprobante_numero, 0, 2);
                printCustom("Fecha Hora: " + comprobante_fecha, 0, 1);
                printCustom("Cajero: " + cajaNum, 0, 1);
                printCustom(new String(new char[32]).replace("\0", "."), 0, 1);
                printCustom("Nro Placa: " + movimiento_nro_placa, 0, 0);
                printCustom("Hora Ingreso: " + movimiento_hora_ingreso, 0, 0);
                printCustom("Hora Salida: " + movimiento_hora_salida, 0, 0);
                printCustom("Tiempo Calculado: " + movimiento_tiempo_calculado, 0, 0);
                printCustom(new String(new char[32]).replace("\0", "."), 0, 1);
                printPhoto(bitmap);
                printNewLine();
                printCustom("Descripcion:            Importe ", 0, 0);
                printCustom("TARIFA GENERAL:          " + detalle_importe, 0, 0);
                printCustom(new String(new char[32]).replace("\0", "."), 0, 1);
                printNewLine();
                printCustom("Op. Grava:" + comprobamte_total_operacion_gravadas, 0, 2);
                printCustom("IGV:" + comprobamte_total_impuesto, 0, 2);
                printCustom("Importe Total:" + comprobamte_total_documento, 0, 2);
                printNewLine();
                printCustom("Gracias por su Preferencia!", 0, 1);
                printNewLine();
                printNewLine();
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (Exception ex) {

        }
    }

    public void printPhoto(Bitmap bitImage) {
        try {
//            Bitmap bmp = BitmapFactory.decodeResource(getResources(),
//                    img);

            Bitmap bmp = bitImage;
            if (bmp != null) {
                byte[] command = Utils.decodeBitmap(bmp);
                outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                printText(command);
            } else {
                Log.e("Print Photo error", "the file isn't exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PrintTools", "the file isn't exists");
        }
    }

    private void printText(byte[] msg) {
        try {
            // Print normal text
            outputStream.write(msg);
            printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printNewLine() {
        try {
            outputStream.write(PrinterCommands.FEED_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void printCustom(String msg, int size, int align) {
        //Print config "mode"
        byte[] cc = new byte[]{0x1B, 0x21, 0x03};  // 0- normal size text
        //byte[] cc1 = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        byte[] bb = new byte[]{0x1B, 0x21, 0x08};  // 1- only bold text
        byte[] bb2 = new byte[]{0x1B, 0x21, 0x20}; // 2- bold with medium text
        byte[] bb3 = new byte[]{0x1B, 0x21, 0x10}; // 3- bold with large text
        try {
            switch (size) {
                case 0:
                    outputStream.write(cc);
                    break;
                case 1:
                    outputStream.write(bb);
                    break;
                case 2:
                    outputStream.write(bb2);
                    break;
                case 3:
                    outputStream.write(bb3);
                    break;
            }

            switch (align) {
                case 0:
                    //left align
                    outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
                    break;
                case 1:
                    //center align
                    outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                    break;
                case 2:
                    //right align
                    outputStream.write(PrinterCommands.ESC_ALIGN_RIGHT);
                    break;
            }
            outputStream.write(msg.getBytes());
            outputStream.write(PrinterCommands.LF);
            //outputStream.write(cc);
            //printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void disconnectBT() throws IOException {
        try {
            stopWorker = true;
            outputStream.close();
            inputStream.close();
            bluetoothSocket.close();
            //lblPrinterName.setText("Printer Disconnected.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
        Call<ResponseBody> responseBodyCall = methodWs.comprobanteAnularErrorMostrar(bus_criterio, cod_sucursal, cod_caja, serie, numero);
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
                            return;
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

    public static String obtenerValor(Context context, String keyPref) {

        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return preferences.getString(keyPref, "");

    }

}

package com.giparking.appgiparking.fragment;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.giparking.appgiparking.MenuActivity;
import com.giparking.appgiparking.R;
import com.giparking.appgiparking.entity.Comprobante;
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
    String descripcion_respuesta = "";
    String cod_movimiento_input = "";
    String ruc_input = "0";

    //Impresion

    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;
    BluetoothDevice bluetoothDevice;
    OutputStream outputStream;
    InputStream inputStream;
    Bitmap bitmap;
    volatile boolean stopWorker;

    //Datos

    String boletaNum;
    String fechaHora;
    String tarifaGeneral;
    String usuarioLogin;
    String opGrabadas;
    String impuestoT;
    String totalDoc;
    String horaIngreso;
    String horaSalida;
    String tiempoCalculado;
    String tipoComp;

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


        btn_validar_sin_validacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String ruc = edt_ruc_sin_validacion.getText().toString();

                if (ruc.length() < 11){

                    pd = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
                    pd.getProgressHelper().setBarColor(Color.parseColor("#03A9F4"));
                    pd.setContentText("Ingrese un numero de RUC correcto");
                    pd.setCancelable(false);
                    pd.show();
                    return;
                }

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

                                    ruc_input = parts_cliente[0];
                                    String razon_social = parts_cliente[1];
                                    String ubigeo = parts_cliente[2];
                                    String direccion = parts_cliente[3];

                                    edt_resultado_ruc_sin_validacion.setText(razon_social);

                                    pd.dismiss();

                                } else {
                                    edt_resultado_ruc_sin_validacion.setText("");
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
                            String cod_convenio = parts_respuesta[0];
                            String tiempo = parts_respuesta[1];
                            String monto = parts_respuesta[2];

                            tv_tiempo_sin_validacion.setText(tiempo);
                            tv_monto_sin_validacion.setText(monto);

                            pd.dismiss();

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
    public void registrarPagoSinValidacion() {

        final String tipo;

        String cod_corpempresa = a_str_global.getCod_corpempresa().toString();
        String cod_sucursal = a_str_global.getCod_sucursal().toString();
        String cod_cefectivo = a_str_global.getCod_cefectivo().toString();
        String cod_caja = a_str_global.getCod_caja().toString();
        ;
        String cod_usuario = a_str_global.getCod_usuario().toString();

        if (rb_factura_sin_validacion.isChecked()) {
            tipo = "1";
        } else {
            tipo = "2";
        }

        if (tipo.equals("1")){

            String ruc_input = edt_ruc_sin_validacion.getText().toString();

            if (ruc_input.length() < 11){

                pd = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
                pd.getProgressHelper().setBarColor(Color.parseColor("#03A9F4"));
                pd.setContentText("Ingrese un numero de RUC correcto");
                pd.setCancelable(false);
                pd.show();
                return;
            }
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
        Call<ResponseBody> responseBodyCall = methodWs.controlAutoSalidaGrabar(cod_corpempresa, cod_sucursal,
                cod_cefectivo, cod_caja, cod_usuario, i_cod_tcomprobante, i_cod_movimiento, i_cod_tvalidacion, i_cod_producto,
                i_cod_convenio, i_ingresa_fecha, i_ingreso_hora, i_emp_ruc, i_nro_placa, i_conve_codigo, i_conve_fecha,
                i_conve_tipo, i_conve_serie, i_conve_numero, i_conve_monto);
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

                            pd.dismiss();

                            new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Informativo")
                                    .setContentText("Registrado correctamente!!")
                                    .setConfirmText("Continuar")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {

                                            irMenuPrincipal();
                                        }
                                    }).show();


                            // Toast.makeText(getContext(),"Registrado correctamente!!",Toast.LENGTH_LONG).show();
                            // irMenuPrincipal();

                           // Toast.makeText(getContext(),"Registrado correctamente!!",Toast.LENGTH_LONG).show();
                           // irMenuPrincipal();

                        }else if(codigo_respuesta.equals("99")){


                           // Toast.makeText(getContext(),descripcion_respuesta,Toast.LENGTH_LONG).show();

                            pd.dismiss();
                            new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Informativo")
                                    .setContentText("Registrado correctamente!!")
                                    .setConfirmText("Continuar")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {

                                            irMenuPrincipal();
                                        }
                                    }).show();


                            // Toast.makeText(getContext(),"Registrado correctamente!!",Toast.LENGTH_LONG).show();
                            // irMenuPrincipal();
                            //irMenuPrincipal();

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

    //Bluetooh Conexion y DATA

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
                printCustom(comprobante_numero, 0, 1);
                printCustom(cliente_tipo+" :" + cliente_documento, 0, 0);
                printCustom(cliente_nombre, 0, 0);
                printCustom(comprobante_numero, 0, 1);
                printCustom("Fecha Hora: " + comprobante_fecha, 0, 1);
                printCustom("Cajero: " + cajaNum, 0, 1);
                if (!documento_referencial.equalsIgnoreCase("-"))
                {
                    printCustom(documento_referencial, 0, 1);
                }
                printCustom(new String(new char[32]).replace("\0", "."), 0, 1);
                printCustom("Nro Placa: " + movimiento_nro_placa, 0, 0);
                printCustom("Hora Ingreso: " + movimiento_hora_ingreso, 0, 0);
                printCustom("Hora Salida: " + movimiento_hora_salida, 0, 0);
                printCustom("Tiempo Calculado: " + movimiento_tiempo_calculado, 0, 0);
                printCustom(new String(new char[32]).replace("\0", "."), 0, 1);
                printPhoto(bitmap);
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

    private void printNewLine() {
        try {
            outputStream.write(PrinterCommands.FEED_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String obtenerValor(Context context, String keyPref) {

        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return preferences.getString(keyPref, "");

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

}

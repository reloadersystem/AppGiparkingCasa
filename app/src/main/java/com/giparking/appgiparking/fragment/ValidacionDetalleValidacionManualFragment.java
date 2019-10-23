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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.giparking.appgiparking.MenuActivity;
import com.giparking.appgiparking.R;
import com.giparking.appgiparking.entity.Convenio;
import com.giparking.appgiparking.entity.GenericoSpinner;
import com.giparking.appgiparking.entity.Producto;
import com.giparking.appgiparking.rest.HelperWs;
import com.giparking.appgiparking.rest.MethodWs;
import com.giparking.appgiparking.util.ContenedorClass;
import com.giparking.appgiparking.util.PrinterCommands;
import com.giparking.appgiparking.util.Utils;
import com.giparking.appgiparking.util.str_global;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
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


            }
        });

        rb_boleta_validacion_manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                linear_grupo_ruc_validacion_manual.setVisibility(View.GONE);
                edt_ruc_validacion_manual.setText("");
                edt_resultado_ruc_validacion_manual.setText("");

                ruc_input = "0";
            }
        });

        btn_validar_validacion_manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String ruc = edt_ruc_validacion_manual.getText().toString();

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

                                    ruc_input =  parts_cliente[0];
                                    String razon_social = parts_cliente[1];
                                    String ubigeo = parts_cliente[2];
                                    String direccion = parts_cliente[3];

                                    edt_resultado_ruc_validacion_manual.setText(razon_social);

                                    pd.dismiss();

                                }else{
                                    edt_resultado_ruc_validacion_manual.setText("");
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

    @OnClick(R.id.btn_registrar_pago_validacion_manual)
    public void registrarPagoValidacionManual(){

        final String tipo;

        String cod_corpempresa = a_str_global.getCod_corpempresa().toString();
        String cod_sucursal = a_str_global.getCod_sucursal().toString();
        String cod_cefectivo = a_str_global.getCod_cefectivo().toString();
        String cod_caja = a_str_global.getCod_caja().toString();;
        String cod_usuario = a_str_global.getCod_usuario().toString();

        if (rb_factura_validacion_manual.isChecked()){
            tipo = "1";
        }else{
            tipo = "2";
        }

        if (codigo_convenio.equals("-1")){

            pd = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
            pd.getProgressHelper().setBarColor(Color.parseColor("#03A9F4"));
            pd.setContentText("Debe seleccionar un convenio");
            pd.setCancelable(false);
            pd.show();
            return;
        }

        if (tipo.equals("1")){

            String ruc_input = edt_ruc_validacion_manual.getText().toString();

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
        String i_cod_tvalidacion = "2";
        String i_cod_producto = clienteid;
        String i_cod_convenio = codigo_convenio;
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

                            //IMPRIMIR
                            String valores_comprobante = "2170192100¦BOLETA DE VENTA¦BB07-2100¦14/10/2019 15:59:22¦USER1¦DNI¦00000000¦CLIENTES VARIOS¦-¦2.54¦0.46¦3.00¦\n" +
                                    "V2W691¦15:20:00¦15:59:22¦00:39¦\n" +
                                    "TARIFA GENERAL¦3.00¦\n" +
                                    "20492490718|03|BB07|2100|0.46|3.00|2019-10-14|00|00000000|-¬";

                            String[] partSplit = valores_comprobante.split("¦");


                            if (tipo.equals("1")) {
                                tipoComp = "FACTURA";
                                boletaNum = edt_ruc_validacion_manual.getText().toString();
                            }else
                            {
                                tipoComp = partSplit[1];
                                boletaNum = partSplit[2];
                            }

                            fechaHora = partSplit[3];
                            usuarioLogin = partSplit[4];
                            opGrabadas = partSplit[9];
                            impuestoT = partSplit[10];
                            totalDoc = partSplit[11];
                            tarifaGeneral = partSplit[17];
                            horaIngreso = partSplit[13];
                            horaSalida = partSplit[14];
                            tiempoCalculado = partSplit[15];

                            FindBluetoothDevice();
                            openBluetoothPrinter(valores_comprobante);
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


                        }else if(codigo_respuesta.equals("99")){


                            //Toast.makeText(getContext(),descripcion_respuesta,Toast.LENGTH_LONG).show();

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


//QR CALL
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
                printCustom(tipoComp +" Nro: " + boletaNum, 0, 1);
                printCustom("Fecha Hora: " + fechaHora, 0, 1);
                printCustom("Cajero: " + cajaNum, 0, 1);
                printCustom(new String(new char[32]).replace("\0", "."), 0, 1);
                printCustom("Nro Placa:" + nro_placa, 0, 0);
                printCustom("Hora de Ingreso: " + horaIngreso, 0, 0);
                printCustom("Hora de Salida: " + horaSalida, 0, 0);
                printCustom("Tiempo Calculado: " + tiempoCalculado, 0, 0);
                printCustom(new String(new char[32]).replace("\0", "."), 0, 1);
                printCustom("Descripcion #1    Importe", 0, 1);
                printCustom("TARIFA GENERAL      " + tarifaGeneral, 0, 1);
                printCustom(new String(new char[32]).replace("\0", "."), 0, 1);
                printCustom("Op. Grava:" + opGrabadas, 0, 2);
                printCustom("IGV:" + impuestoT, 0, 2);
                printCustom("Importe Total:" + totalDoc, 0, 2);
                printPhoto(bitmap);
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

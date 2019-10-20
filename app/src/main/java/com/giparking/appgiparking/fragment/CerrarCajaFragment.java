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
import android.widget.Toast;

import com.giparking.appgiparking.R;
import com.giparking.appgiparking.adapter.CerrarCajaAdapter;
import com.giparking.appgiparking.entity.Movimiento;
import com.giparking.appgiparking.rest.HelperWs;
import com.giparking.appgiparking.rest.MethodWs;
import com.giparking.appgiparking.util.PrinterCommands;
import com.giparking.appgiparking.util.Utils;
import com.giparking.appgiparking.util.str_global;
import com.giparking.appgiparking.view.LoguinActivity;
import com.google.zxing.MultiFormatWriter;

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

    //Impresion

    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;
    BluetoothDevice bluetoothDevice;
    OutputStream outputStream;
    InputStream inputStream;
    Bitmap bitmap;
    volatile boolean stopWorker;

    String numero0, valor0, numero1, valor1, numero2, valor2, numero3, valor3, numero4, valor4,numero5,valor5;

    String numeroRes0, valorRes0, numeroRes1, valorRes1, numeroRes2, valorRes2, numeroRes3, valorRes3;


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

                            numero0 = list_movimiento_datos.get(0).getTitulo();
                            valor0 = list_movimiento_datos.get(0).getValor();

                            numero1 = list_movimiento_datos.get(1).getTitulo();
                            valor1 = list_movimiento_datos.get(1).getValor();

                            numero2 = list_movimiento_datos.get(2).getTitulo();
                            valor2 = list_movimiento_datos.get(2).getValor();

                            numero3 = list_movimiento_datos.get(3).getTitulo();
                            valor3 = list_movimiento_datos.get(3).getValor();

                            numero4 = list_movimiento_datos.get(4).getTitulo();
                            valor4 = list_movimiento_datos.get(4).getValor();

                            numero5 = list_movimiento_datos.get(5).getTitulo();
                            valor5 = list_movimiento_datos.get(5).getValor();


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

                            numeroRes0 = list_movimiento_resumen.get(0).getTitulo();
                            valorRes0 = list_movimiento_resumen.get(0).getValor();

                            numeroRes1 = list_movimiento_resumen.get(1).getTitulo();
                            valorRes1 = list_movimiento_resumen.get(1).getValor();

                            numeroRes2 = list_movimiento_resumen.get(2).getTitulo();
                            valorRes2 = list_movimiento_resumen.get(2).getValor();

                            numeroRes3 = list_movimiento_resumen.get(3).getTitulo();
                            valorRes3 = list_movimiento_resumen.get(3).getValor();




                            //Extraemos valores movimiento  : error
//                            String valores_movimiento = parts[3];  //java.lang.IndexOutOfBoundsException : Invalid array range: 3 to 3
//                            String[] parts_valores_movimiento = valores_movimiento.split("¬");
//
//                            for (int i = 0; i < parts_valores_movimiento.length; i++) {
//
//                                String linea = parts_valores_movimiento[i];
//
//                                //Extraemos valores de cada dato
//                                String[] parts_valores_movimiento_detalle = linea.split("¦");
//
//                                Movimiento movimiento = new Movimiento();
//                                movimiento.setTitulo(parts_valores_movimiento_detalle[0]);
//                                movimiento.setValor(parts_valores_movimiento_detalle[1]);
//                                list_movimiento_movimiento.add(movimiento);
//                            }

                            //Extraemos valores comprobante
//                            String valores_comprobante = parts[4];
//                            String[] parts_valores_comprobante = valores_comprobante.split("¬");
//
//                            for (int i = 0; i < parts_valores_comprobante.length; i++) {
//
//                                String linea = parts_valores_comprobante[i];
//
//                                //Extraemos valores de cada dato
//                                String[] parts_valores_comprobante_detalle = linea.split("¦");
//
//                                Movimiento movimiento = new Movimiento();
//                                movimiento.setTitulo(parts_valores_comprobante_detalle[0]);
//                                movimiento.setValor(parts_valores_comprobante_detalle[1]);
//                                list_movimiento_comprobantes.add(movimiento);
//                            }

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


//        fechaComp = list_movimiento_resumen.get(1).getTitulo();
//        tipoComp = list_movimiento_datos.get(1).getValor();
//        numComp = list_movimiento_movimiento.get(1).getTitulo();
//        placaComp = list_comprobante.get(position).getPlaca();
//        clienteComp = list_comprobante.get(position).getCliente();
//        montoComp = list_comprobante.get(position).getMonto();

        try {
            FindBluetoothDevice();
            openBluetoothPrinter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            disconnectBT();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

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
                                    Toast.makeText(getContext(), "Caja cerrada!!", Toast.LENGTH_LONG).show();
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

    public void irLoguin() {

        Intent i = new Intent(getContext(), LoguinActivity.class);
        startActivity(i);
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

    private void openBluetoothPrinter() throws IOException {

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
                printCustom("Cierre de Caja:        " , 1, 0);// 30 caracteres
                printNewLine();
                printCustom(numero0 + ":               " + valor0, 0, 0);
                printCustom(numero1 + ":                  " + valor1, 0, 0);
                printCustom(numero2 + ":               " + valor2, 0, 0);
                printCustom(numero3 + ":                       " + valor3, 0, 0);
                printCustom(numero4 + ": " + valor4, 0, 0);
                printCustom(numero5 + ": " + valor5, 0, 0);
                printNewLine();
                printCustom(new String(new char[32]).replace("\0", "."), 0, 1);
                printCustom("Datos del Resumen:        " , 1, 0);
                printNewLine();
                printCustom(numeroRes0 + ":     " + valorRes0, 0, 0);
                printCustom(numeroRes1 + ":       " + valorRes1, 0, 0);
                printCustom(numeroRes2 + ":        " + valorRes2, 0, 0);
                printCustom(numeroRes3 + ":   " + valorRes3, 0, 0);
                printNewLine();
                printCustom(new String(new char[32]).replace("\0", "."), 0, 1);
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

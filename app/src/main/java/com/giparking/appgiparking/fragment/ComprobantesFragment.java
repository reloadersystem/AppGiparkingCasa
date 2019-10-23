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
import com.giparking.appgiparking.adapter.ComprobanteAdapter;
import com.giparking.appgiparking.entity.Comprobante;
import com.giparking.appgiparking.interfaces.OnPrintListener;
import com.giparking.appgiparking.rest.HelperWs;
import com.giparking.appgiparking.rest.MethodWs;
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
public class ComprobantesFragment extends Fragment {

    @BindView(R.id.rb_placa)
    RadioButton rb_placa;

    @BindView(R.id.rb_todos)
    RadioButton rb_todos;

    @BindView(R.id.edt_placa_buscar)
    EditText edt_placa_buscar;

    @BindView(R.id.btn_buscar_comprobantes)
    Button btn_buscar_comprobantes;

    @BindView(R.id.recycler_comprobantes)
    RecyclerView recycler_comprobantes;

    SweetAlertDialog pd;

    private str_global a_str_global = str_global.getInstance();

    String nro_placa = "";
    String cod_caja_input = a_str_global.getCod_caja();
    ;
    String bus_criterio_input = "TODOS";

    RecyclerView.Adapter adapter;
    ArrayList<Comprobante> list_comprobante;

    //Impresion

    String cadena_respuesta;

    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;
    BluetoothDevice bluetoothDevice;
    OutputStream outputStream;
    InputStream inputStream;
    Bitmap bitmap;
    volatile boolean stopWorker;

    //data
    String fechaComp, tipoComp, numComp, placaComp, clienteComp, montoComp;


    public ComprobantesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comprobantes, container, false);
        ButterKnife.bind(this, view);

        getActivity().setTitle("Reimprimir");

        init();
        configurarEventos();
        callApiRestImprimirMostrar();

        return view;
    }

    private void init() {

        edt_placa_buscar.setEnabled(false);
    }

    private void callApiRestImprimirMostrar() {


        if (rb_placa.isChecked()) {
            nro_placa = edt_placa_buscar.getText().toString();
            bus_criterio_input = "PLACA";

            if (edt_placa_buscar.getText().toString().length() < 6){

                pd = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
                pd.getProgressHelper().setBarColor(Color.parseColor("#102670"));
                pd.setContentText("Numero de placa debe tener 6 digitos");
                pd.setCancelable(false);
                pd.show();
            }
        }

        pd = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pd.getProgressHelper().setBarColor(Color.parseColor("#102670"));
        pd.setContentText("Por favor, espere...");
        pd.setCancelable(false);
        pd.show();



        String bus_criterio = bus_criterio_input; //"PLACA";
        String cod_sucursal = a_str_global.getCod_sucursal(); //"217"; //
        String cod_caja = cod_caja_input; //"0"; //
        String placa = nro_placa;

        MethodWs methodWs = HelperWs.getConfiguration().create(MethodWs.class);
        Call<ResponseBody> responseBodyCall = methodWs.comprobanteImprimirMostrar(bus_criterio, cod_sucursal, cod_caja, placa);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                //2170192225¦16/10/2019 15:06¦BOLETA DE VENTA¦BB07-2225¦1A9394¦CLIENTES VARIOS¦3.00¬
                //COD_COMPROBANTE¦FECHA¦TIPO COMPROBANTE¦NUM_COMPROBANTE¦NRO_PLACA¦CLIENTE¦TOTAL_DOCUMENTO
                if (response.isSuccessful()) {

                    list_comprobante = new ArrayList<>();
                    ResponseBody informacion = response.body();
                    try {

                         cadena_respuesta = informacion.string();

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

    private void configurarEventos() {

        rb_placa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                edt_placa_buscar.setEnabled(true);
                //cod_caja_input = "0";
            }
        });

        rb_todos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                edt_placa_buscar.setEnabled(false);
                edt_placa_buscar.setText("");
                nro_placa = "";
                //cod_caja_input = a_str_global.getCod_sucursal();
                bus_criterio_input = "TODOS";
            }
        });
    }

    public void configurarAdapter(final ArrayList<Comprobante> list_comprobante) {

        recycler_comprobantes.setHasFixedSize(true);
        recycler_comprobantes.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ComprobanteAdapter(getContext(), list_comprobante);

        ((ComprobanteAdapter) adapter).setOnPrintListener(new OnPrintListener() {
            @Override
            public void onAccionClicked(int position) {

                fechaComp = list_comprobante.get(position).getFecha();
                tipoComp = list_comprobante.get(position).getTipo();
                numComp = list_comprobante.get(position).getCod_comprobante();
                placaComp = list_comprobante.get(position).getPlaca();
                clienteComp = list_comprobante.get(position).getCliente();
                montoComp = list_comprobante.get(position).getMonto();

                try {
                    FindBluetoothDevice();
                    //imagen suma de los datos...
                    openBluetoothPrinter(cadena_respuesta);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    disconnectBT();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        recycler_comprobantes.setAdapter(adapter);

        //recycler_comprobantes.setAdapter(new ComprobanteAdapter(getContext(), list_comprobante));


    }


    @OnClick(R.id.btn_buscar_comprobantes)
    public void buscarComprobante() {

        callApiRestImprimirMostrar();
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

                printCustom(tipoComp + " Nro:" + numComp, 0, 1);
                printCustom("Fecha Hora: " + fechaComp, 0, 1);
                printCustom("Cajero: " + cajaNum, 0, 1);
                printCustom(new String(new char[32]).replace("\0", "."), 0, 1);
                printCustom("Nro Placa:" + placaComp, 0, 0);
                printCustom("Cliente: " + clienteComp, 0, 0);
                printCustom(new String(new char[32]).replace("\0", "."), 0, 1);
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

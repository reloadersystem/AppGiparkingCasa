package com.giparking.appgiparking.fragment;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.giparking.appgiparking.ConverterToPDF.TemplatePDF;
import com.giparking.appgiparking.R;
import com.giparking.appgiparking.rest.HelperWs;
import com.giparking.appgiparking.rest.MethodWs;
import com.giparking.appgiparking.util.HoraFechaActual;
import com.giparking.appgiparking.util.PrinterCommands;
import com.giparking.appgiparking.util.ShareDataRead;
import com.giparking.appgiparking.util.Utils;
import com.giparking.appgiparking.util.str_global;
import com.giparking.appgiparking.view.LoguinActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Length;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class IngresoPrintFragment extends Fragment implements Validator.ValidationListener {

    View rootview;

    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;
    BluetoothDevice bluetoothDevice;
    OutputStream outputStream;
    InputStream inputStream;

    @BindView(R.id.cardview_grupo_reimprimir)
    CardView cardview_grupo_reimprimir;

    @BindView(R.id.tv_informacion_vehiculo)
    TextView tv_informacion_vehiculo;

    @BindView(R.id.btn_reimprimir)
    Button btn_reimprimir;

    @Length(max = 10, min = 6)
    EditText edTexto;

    Button btnGerar, btnPrint;
    ImageView ivQRCode;
    Bitmap bitmap;
    TextView txtVistaPrevia;
    private TemplatePDF templatePDF;

    SweetAlertDialog pd;

    String descripcion_respuesta = "";
    String valores_comprobante_output = "";

    private String shorText = "_____________________";
    private String longText = "Gracias por preferirnos";

    protected Validator validator;
    protected boolean validated;
    volatile boolean stopWorker;

    int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 1;

    private str_global a_str_global = str_global.getInstance();


    public IngresoPrintFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_ingreso_print, container, false);
        ButterKnife.bind(this, rootview);

        getActivity().setTitle("Ingreso");

        //Verificar si existe una preferencia
        recuperarPreferencias();

        if (valores_comprobante_output.equals("")) {

            cardview_grupo_reimprimir.setVisibility(View.GONE);

        } else {

            cardview_grupo_reimprimir.setVisibility(View.VISIBLE);
            tv_informacion_vehiculo.setText(valores_comprobante_output);
        }

        btn_reimprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO Reimprimir

                String shareGetPreference = ShareDataRead.obtenerValor(getContext(), "valores_comprobante");
                //111019298425¦xtjlfhl¦14/10/2019¦19:45:44¦Espacios Libres: 93


                try {
                    FindBluetoothDevice();
                    openBluetoothPrinter(shareGetPreference);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                pd = new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE);
                pd.getProgressHelper().setBarColor(Color.parseColor("#03A9F4"));
                pd.setContentText("Re Impresion correcto!!");
                // pd.setCancelable(false);
                pd.show();

                try {
                    disconnectBT();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }


            }
        });



        validator = new Validator(this);
        validator.setValidationListener(this);

        edTexto = rootview.findViewById(R.id.edTexto);
        btnGerar = rootview.findViewById(R.id.btnGerar);
        ivQRCode = rootview.findViewById(R.id.ivQRCode);
        txtVistaPrevia = rootview.findViewById(R.id.txtPreview);

        edTexto.requestFocus();


        int verificarPermisoWrite = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (verificarPermisoWrite != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                solicitarPermiso(); // sino  ha aceptado los  permisos
            } else
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
        }

        btnGerar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validator.validate();
            }
        });


        txtVistaPrevia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                VistaPreviaPrintFragment fragmeVistaPrevia = new VistaPreviaPrintFragment();

                String fechaImpresion = HoraFechaActual.obtenerFecha();

                templatePDF = new TemplatePDF(getContext(), bitmap);

                templatePDF.openDocument();
                templatePDF.addMetadata("Parqueando", "GOParking", "ParkFacil");
                //templatePDF.addTitles("QR", "QRPrinter",fechaImpresion );
                //templatePDF.addParagraph(shorText);
                //templatePDF.addParagraph(longText);
                templatePDF.closeDocument();
                // templatePDF.viewPDF(getActivity(), fragmeVistaPrevia);
            }
        });
        return rootview;
    }


    private void solicitarPermiso() {

        new AlertDialog.Builder(getContext())
                .setTitle("Autorización")
                .setMessage("Necesito permiso para Almacenar Archivos")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                    }


                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }


    @Override
    public void onValidationFailed(List<ValidationError> errors) {

        validated = false;

        for (ValidationError error : errors) {
            View view = error.getView();
            String message = "ingrese datos correctos";


            // Display error messages
            if (view instanceof Spinner) {
                Spinner sp = (Spinner) view;
                view = ((LinearLayout) sp.getSelectedView()).getChildAt(0);        // we are actually interested in the text view spinner has
            }

            if (view instanceof TextView) {
                TextView et = (TextView) view;
                et.setError(message);
            }
        }
    }


    protected boolean validate() {
        if (validator != null)
            validator.validate();
        return validated;           // would be set in one of the callbacks below
    }

    @Override
    public void onValidationSucceeded() {
        validated = true;

        if (edTexto.length() < 6){

            pd = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
            pd.getProgressHelper().setBarColor(Color.parseColor("#03A9F4"));
            pd.setContentText("Placa debe tener 6 digitos");
            pd.setCancelable(false);
            pd.show();
            return;
        }

        String cod_corpempresa = a_str_global.getCod_corpempresa().toString();
        String cod_sucursal = a_str_global.getCod_sucursal().toString();
        String cod_usuario = a_str_global.getCod_usuario().toString();
        String cod_cefectivo = a_str_global.getCod_cefectivo().toString();
        String nro_placa = edTexto.getText().toString();

        pd = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pd.getProgressHelper().setBarColor(Color.parseColor("#102670"));
        pd.setContentText("Por favor, espere...");
        pd.setCancelable(false);
        pd.show();


        MethodWs methodWs = HelperWs.getConfiguration().create(MethodWs.class);
        Call<ResponseBody> responseBodyCall = methodWs.controlIngresoGrabar(cod_corpempresa, cod_sucursal, cod_usuario, cod_cefectivo, nro_placa);
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

                        if (codigo_respuesta.equals("0")) { //0 regla de negocio Ok



                            //Datos que debe imprimirse en el qr
                            String valores_comprobante = parts[1]; //TODO ingreso y hora pasar document, placa
                            //TODO Aca debe de mandar a imprimir [valores_comprobante]

                            FindBluetoothDevice();

                            openBluetoothPrinter(valores_comprobante);

                            // gerarQRCode(valores_comprobante);

                            //Grabarlo en un sharepreferences
                            guardarPreferencia(valores_comprobante);
                            cardview_grupo_reimprimir.setVisibility(View.VISIBLE);
                            tv_informacion_vehiculo.setText(valores_comprobante);

                            //gerarQRCode();
                            pd.dismiss();
                            pd = new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE);
                            pd.getProgressHelper().setBarColor(Color.parseColor("#03A9F4"));
                            pd.setContentText("Ingreso correcto!!");
                           // pd.setCancelable(false);
                            pd.show();

                            try {
                                        disconnectBT();
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }


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



    public void guardarPreferencia(String valores_comprobante) {

        SharedPreferences.Editor editor = getContext().getSharedPreferences("Preferencia_ingreso", 0).edit();
        editor.putString("valores_comprobante", valores_comprobante);
        editor.commit();

    }

    public void recuperarPreferencias() {

        SharedPreferences pref = getContext().getSharedPreferences("Preferencia_ingreso", 0);
        valores_comprobante_output = pref.getString("valores_comprobante", "");
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

    private void FindBluetoothDevice() {

        try {

            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                //lblPrinterName.setText("No Bluetooth Adapter found");
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

                    //key_printer


                    if (pairedDev.getName().equals(printerPortable)) {
                        bluetoothDevice = pairedDev;
                        //  lblPrinterName.setText("Bluetooth Printer Attached: " + pairedDev.getName());
                        break;
                    }
                }
            }

            // lblPrinterName.setText("Bluetooth Printer Attached");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    static String PREFS_KEY = "PortablePrinter";

    public static String obtenerValor(Context context, String keyPref) {

        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return preferences.getString(keyPref, "");

    }

    private void openBluetoothPrinter(String valores_comprobante) throws IOException {
        try {
            try {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                //String msg = "QRPARKING FACIL";

                BitMatrix bitMatrix = multiFormatWriter.encode(valores_comprobante, BarcodeFormat.QR_CODE, 200, 200);  //2000, 2000//200,200
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                bitmap = barcodeEncoder.createBitmap(bitMatrix);

                UUID uuidSting = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
                bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuidSting);
                bluetoothSocket.connect();
                outputStream = bluetoothSocket.getOutputStream();
                inputStream = bluetoothSocket.getInputStream();

                String nombreEmpresa = str_global.getInstance().getVar_cabecera_c_0();
                String direccionEmpresa = str_global.getInstance().getVar_cabecera_t_1() + " \n" + str_global.getInstance().getVar_cabecera_t_2();
                String [] splitValores_comprobante = valores_comprobante.split("¦");
                String part1 =splitValores_comprobante[1];
                String fechaHora = splitValores_comprobante[2] + " " + splitValores_comprobante[3];


                byte[] printformat = new byte[]{0x1B,0x21,0x03};
                outputStream.write(printformat);
                printCustom(nombreEmpresa,1,1); //2,1
                printNewLine();
                printCustom(direccionEmpresa,0,1);
                printCustom(new String(new char[32]).replace("\0", "."),0,1);
                printPhoto(bitmap);
                printCustom("PLACA: " + part1,0,1);
                printCustom(fechaHora,0,1);
                printNewLine();
                printCustom(new String(new char[32]).replace("\0", "."),0,1);
                printCustom("Gracias por su Preferencia",0,1);
                printNewLine();
                printNewLine();

                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (Exception ex) {

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

}

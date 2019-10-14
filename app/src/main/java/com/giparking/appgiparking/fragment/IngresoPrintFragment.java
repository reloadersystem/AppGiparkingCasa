package com.giparking.appgiparking.fragment;


import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.giparking.appgiparking.util.Save;
import com.giparking.appgiparking.util.str_global;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Length;

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
public class IngresoPrintFragment extends Fragment implements Validator.ValidationListener {

    View rootview;

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

    private String shorText = "Hola";
    private String longText = "iOS Studio";

    protected Validator validator;
    protected boolean validated;

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
        ButterKnife.bind(this,rootview);

        getActivity().setTitle("ParkFácil");

        //Verificar si existe una preferencia
        recuperarPreferencias();

        if (valores_comprobante_output.equals("")){

            cardview_grupo_reimprimir.setVisibility(View.GONE);

        }else{

            cardview_grupo_reimprimir.setVisibility(View.VISIBLE);
            tv_informacion_vehiculo.setText(valores_comprobante_output);
            btn_reimprimir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //TODO Reimprimir
                }
            });
        }

        validator = new Validator(this);
        validator.setValidationListener(this);

        edTexto = rootview.findViewById(R.id.edTexto);
        btnGerar = rootview.findViewById(R.id.btnGerar);
        ivQRCode = rootview.findViewById(R.id.ivQRCode);
        txtVistaPrevia = rootview.findViewById(R.id.txtPreview);


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

                templatePDF = new TemplatePDF(getContext(), bitmap);
                templatePDF.openDocument();
                templatePDF.addMetadata("Parqueando", "Resembrink", "Libros");
                templatePDF.addTitles("QR", "QRPrinter", "03/10/2019");
                templatePDF.addParagraph(shorText);
                templatePDF.addParagraph(longText);
                templatePDF.closeDocument();
                templatePDF.viewPDF(getActivity(), fragmeVistaPrevia);
            }
        });
        return rootview;
    }

    private void gerarQRCode() {

        String texto = edTexto.getText().toString();

        String fechaActual = HoraFechaActual.obtenerFecha();
        String horaActual = HoraFechaActual.obtenerHora();

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        try {

            String dataInfoQR = String.format("%s ,%s ,%s", horaActual, fechaActual, texto); // QR contiene fecha, hora, Placa
            BitMatrix bitMatrix = multiFormatWriter.encode(dataInfoQR, BarcodeFormat.QR_CODE, 2000, 2000);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);
            ivQRCode.setImageBitmap(bitmap);


// MARK : guarda imagen
            Save save = new Save();
            save.SaveImage(getContext(), bitmap);
            txtVistaPrevia.setTextColor(getResources().getColor(R.color.colorBlack));
            txtVistaPrevia.setEnabled(true);

        } catch (WriterException e) {
            Log.i("error", e.toString());
        }

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
        Call<ResponseBody> responseBodyCall = methodWs.controlIngresoGrabar(cod_corpempresa,cod_sucursal,cod_usuario,cod_cefectivo,nro_placa);
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

                            //Datos que debe imprimirse en el qr
                            String valores_comprobante = parts[1];
                            //TODO Aca debe de mandar a imprimir [valores_comprobante]

                            //Grabarlo en un sharepreferences
                            guardarPreferencia(valores_comprobante);

                            //gerarQRCode();
                            pd.dismiss();
                            pd = new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE);
                            pd.getProgressHelper().setBarColor(Color.parseColor("#03A9F4"));
                            pd.setContentText("Ingreso correcto!!");
                            pd.setCancelable(false);
                            pd.show();
                            return;

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

    public void guardarPreferencia(String valores_comprobante){

        SharedPreferences.Editor editor = getContext().getSharedPreferences("Preferencia_ingreso",0).edit();
        editor.putString("valores_comprobante",valores_comprobante);
        editor.commit();

    }

    public void recuperarPreferencias(){

        SharedPreferences pref = getContext().getSharedPreferences("Preferencia_ingreso", 0);
        valores_comprobante_output = pref.getString("valores_comprobante","");


    }

}

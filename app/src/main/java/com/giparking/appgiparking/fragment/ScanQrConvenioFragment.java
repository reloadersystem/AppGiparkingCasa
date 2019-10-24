package com.giparking.appgiparking.fragment;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ListView;

import com.giparking.appgiparking.R;
import com.giparking.appgiparking.rest.HelperWs;
import com.giparking.appgiparking.rest.MethodWs;
import com.giparking.appgiparking.util.str_global;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScanQrConvenioFragment extends Fragment {

    Fragment fragment;

    private static int REQUEST_ENABLED = 0;
    private static int REQUEST_DISCOVERABLE = 0;

    SurfaceView surface;

    private String qrResponse = "";
    private String qrResponseAnterior = "";
    String cod_movimiento = "";

    String nro_placa, fecha, hora, cliente, clienteid;

    SweetAlertDialog pd;

    public ScanQrConvenioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scan_qr_convenio, container, false);

        surface = (SurfaceView) view.findViewById(R.id.surfaceView_convenio);

        init();

        int rc = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);


        if (rc == PackageManager.PERMISSION_GRANTED) {
            setUp();
        } else {
            requestCamaraPermission();
        }

        return view;
    }

    private void init() {

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            //descrip = bundle.getString("description");
            nro_placa = bundle.getString("placa");
            fecha = bundle.getString("fecha");
            hora = bundle.getString("hora");
            cliente = bundle.getString("cliente");
            clienteid = bundle.getString("clienteid");
            cod_movimiento = bundle.getString("cod_movimiento");

        }
    }

    private void requestCamaraPermission() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                }
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
                return;
            }
        }
    }

    private void setUp() {
        BarcodeDetector bar = new BarcodeDetector.Builder(getContext())
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        final CameraSource camara = new CameraSource.Builder(getContext(), bar)
                .setAutoFocusEnabled(true)
                .build();

        bar.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcode = detections.getDetectedItems();
                if (barcode.size() > 0) {

                    qrResponse = barcode.valueAt(0).displayValue;

                    if (!qrResponse.equals(qrResponseAnterior)) {
                        qrResponseAnterior = qrResponse;
                        Log.i("qrResponse", qrResponse);

                        if (URLUtil.isValidUrl(qrResponse)) {

                            //un QR que tiene una direccion de URL y  segun la URL abre  la pagina.
//                           Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(qrResponse));
//                            startActivity(browserIntent);
                        } else {
                            //fragment = new ValidacionFragment();
                            String qrDataRecieve = qrResponse;

                            fragment = new ValidacionDetalleValidacionAutoFragment();
                            Bundle args = new Bundle();
                            args.putString("qrDataRecieve", qrDataRecieve);
                            args.putString("placa", nro_placa);//
                            args.putString("fecha", fecha);//
                            args.putString("hora", hora);//
                            args.putString("cliente",cliente);
                            args.putString("clienteid",clienteid);
                            args.putString("cod_movimiento",cod_movimiento);//

                            changeFragment(args);

                        }

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    synchronized (this) {
                                        wait(5000);
                                        // limpiamos el qrResponse
                                        qrResponseAnterior = "";

                                    }
                                } catch (InterruptedException e) {
                                    Log.e("Error", "Espera");
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }
            }
        });

        surface.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    camara.start(surface.getHolder());
                } catch (Exception ex) {

                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                camara.stop();
            }


        });
    }

    private void changeFragment(Bundle bundle) {

        FragmentManager fmanager = getActivity().getSupportFragmentManager();
        if (fmanager != null) {
            fragment.setArguments(bundle);
            FragmentTransaction ftransaction = fmanager.beginTransaction();
            if (ftransaction != null) {
                ftransaction.replace(R.id.contenedor, fragment);
                ftransaction.addToBackStack("");
                ftransaction.commit();
            }
        }
    }

}

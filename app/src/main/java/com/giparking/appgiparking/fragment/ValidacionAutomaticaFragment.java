package com.giparking.appgiparking.fragment;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.giparking.appgiparking.R;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.util.ArrayList;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class ValidacionAutomaticaFragment extends Fragment {
    Button b_on, b_off, b_list, b_disc;
    BluetoothAdapter bluetoothAdapter;
    Fragment fragment;
    ListView list;

    private static int REQUEST_ENABLED = 0;
    private static int REQUEST_DISCOVERABLE = 0;

    SurfaceView surface;

    private String qrResponse = "";
    private String qrResponseAnterior = "";

    String placaNum,horaSalida,prodSelec;


    View rootview;


    public ValidacionAutomaticaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_validacion_automatica, container, false);
        getActivity().setTitle("Validación Automática");

        b_on = rootview.findViewById(R.id.b_on);
        b_off = rootview.findViewById(R.id.b_off);
        b_list = rootview.findViewById(R.id.b_list);
        b_disc = rootview.findViewById(R.id.b_discoverable);



        Bundle bundle = this.getArguments();
        if (bundle != null) {
            //descrip = bundle.getString("description");
            placaNum = bundle.getString("Placa");
            horaSalida = bundle.getString("HoraSalida");
            prodSelec = bundle.getString("ProductoSeleccionado");
        }

        Toast.makeText(getContext(), placaNum + horaSalida + prodSelec, Toast.LENGTH_SHORT).show();

        surface = (SurfaceView)  rootview.findViewById(R.id.surfaceView);

        int rc = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);


        if (rc == PackageManager.PERMISSION_GRANTED) {
            setUp();
        } else {
            requestCamaraPermission();
        }

        list =  rootview.findViewById(R.id.list);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(getContext(), "Tu Dispositivo no tiene Soporte Bluetooth", Toast.LENGTH_SHORT).show();

        }

        b_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, REQUEST_ENABLED);

            }
        });

        b_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bluetoothAdapter.disable();

            }
        });


        b_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                ArrayList<String> deviced = new ArrayList<String>();

                for (BluetoothDevice bt : pairedDevices) {
                    deviced.add(bt.getName());
                }

                ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_expandable_list_item_1, deviced);

                list.setAdapter(arrayAdapter);


            }
        });


        b_disc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!bluetoothAdapter.isDiscovering()) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivityForResult(intent, REQUEST_DISCOVERABLE);
                }


            }
        });





        return rootview;
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

        final CameraSource camara = new CameraSource.Builder(getContext(),bar).build();

        bar.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcode = detections.getDetectedItems();
                if(barcode.size()>0){

                    qrResponse = barcode.valueAt(0).displayValue;

                    if(!qrResponse.equals(qrResponseAnterior))
                    {
                        qrResponseAnterior=qrResponse;
                        Log.i("qrResponse", qrResponse);

                        if(URLUtil.isValidUrl(qrResponse)){

                            //un QR que tiene una direccion de URL y  segun la URL abre  la pagina.
//                           Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(qrResponse));
//                            startActivity(browserIntent);
                        }else
                        {
                            fragment = new ValidacionFragment();
                            changeFragment();

//                            Intent shareIntent = new Intent();
//                            shareIntent.setAction(Intent.ACTION_SEND);
//                            shareIntent.putExtra(Intent.EXTRA_TEXT, qrResponse);
//                            shareIntent.setType("text/plain");
//                            startActivity(shareIntent);shareIntent



                        }

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    synchronized (this)
                                    {
                                        wait(5000);
                                        // limpiamos el qrResponse
                                        qrResponseAnterior = "";

                                    }
                                }catch (InterruptedException e)
                                {
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
                try{
                    if(ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                        return ;
                    }
                    camara.start(surface.getHolder());
                }catch (Exception ex){

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

    private void changeFragment() {

        FragmentManager fmanager = getActivity().getSupportFragmentManager();
        if (fmanager != null) {

            Bundle args = new Bundle();
            args.putString("QrResponse", qrResponse);
            args.putString("ProductoSeleccionado", prodSelec);

            fragment.setArguments(args);

            FragmentTransaction ftransaction = fmanager.beginTransaction();
            if (ftransaction != null) {
                ftransaction.replace(R.id.contenedor, fragment);
                ftransaction.addToBackStack("");
                ftransaction.commit();
            }
        }
    }

}

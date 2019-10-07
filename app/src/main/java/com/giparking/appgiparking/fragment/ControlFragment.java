package com.giparking.appgiparking.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.giparking.appgiparking.R;
import com.giparking.appgiparking.rest.HelperWs;
import com.giparking.appgiparking.rest.MethodWs;
import com.giparking.appgiparking.util.str_global;
import com.giparking.appgiparking.view.LoguinActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class ControlFragment extends Fragment {

    private str_global a_str_global = str_global.getInstance();
    SweetAlertDialog pd;

    public ControlFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_control, container, false);
        ButterKnife.bind(this,view);


       /* pd = new SweetAlertDialog(LoguinActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pd.getProgressHelper().setBarColor(Color.parseColor("#102670"));
        pd.setContentText("Por favor, espere...");
        pd.setCancelable(false);
        pd.show();

        String cod_corpempresa = a_str_global.getCod_corpempresa().toString();
        String cod_sucursal = a_str_global.getCod_sucursal().toString();
        String cod_usuario = a_str_global.getCod_usuario().toString();
        String cod_cefectivo = a_str_global.getCod_cefectivo().toString();

        MethodWs methodWs = HelperWs.getConfiguration().create(MethodWs.class);
        Call<ResponseBody> responseBodyCall = methodWs.aperturarCaja(llave, cod_sucursal,cod_usuario,cod_caja,caja_nombre);
*/

        return view;
    }

    @OnClick(R.id.btn_ingresar)
    public void ingresar(){


    }

    @OnClick(R.id.btn_salida_qr)
    public void salidaQr(){

    }

    @OnClick(R.id.btn_salida_manual)
    public void salidaManual(){

    }

}
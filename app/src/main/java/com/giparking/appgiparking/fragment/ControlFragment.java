package com.giparking.appgiparking.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.giparking.appgiparking.R;
import com.giparking.appgiparking.util.str_global;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class ControlFragment extends Fragment {

    private str_global a_str_global = str_global.getInstance();
    SweetAlertDialog pd;

    private Unbinder unbinder;

    Fragment fragment;


    public ControlFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_control, container, false);

        unbinder = ButterKnife.bind(this,view);

        getActivity().setTitle("Control Automático");



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

    @OnClick(R.id.btn_ingreso)
    public void ingresar(){
        fragment = new IngresoPrintFragment();
        changeFragment();

    }



    @OnClick(R.id.btn_salida_qr)
    public void salidaQr(){

        //fragment = new SalidaVehiculoFragment();
        fragment = new ValidacionAutomaticaFragment();
        changeFragment();

    }

    @OnClick(R.id.btn_salida_manual)
    public void salidaManual(){

        fragment = new SalidaManualFragment();
        changeFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void changeFragment() {

        FragmentManager fmanager = getActivity().getSupportFragmentManager();
        if (fmanager != null) {

            Bundle args = new Bundle();
            args.putString("ACCESO", "Placa");
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

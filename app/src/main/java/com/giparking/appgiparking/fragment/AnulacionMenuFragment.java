package com.giparking.appgiparking.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.giparking.appgiparking.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class AnulacionMenuFragment extends Fragment {

    Fragment fragment;


    public AnulacionMenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_anulacion_menu, container, false);
        ButterKnife.bind(this,view);


        return view;
    }


    @OnClick(R.id.btn_anulacion_por_error)
    public void anulacionPorError(){

        fragment = new AnulacionPorErrorFragment();
        changeFragment();
    }

    @OnClick(R.id.btn_anulacion_por_canje)
    public void anulacionPorCanje(){


    }

    private void changeFragment() {

        FragmentManager fmanager = getActivity().getSupportFragmentManager();
        if (fmanager != null) {



            FragmentTransaction ftransaction = fmanager.beginTransaction();
            if (ftransaction != null) {
                ftransaction.replace(R.id.contenedor, fragment);
                ftransaction.addToBackStack("");
                ftransaction.commit();
            }
        }
    }



}

package com.giparking.appgiparking;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


import com.giparking.appgiparking.entity.Convenio;
import com.giparking.appgiparking.fragment.AnulacionMenuFragment;
import com.giparking.appgiparking.fragment.CerrarCajaFragment;
import com.giparking.appgiparking.fragment.ComprobantesFragment;
import com.giparking.appgiparking.fragment.ControlFragment;
import com.giparking.appgiparking.fragment.IngresoPrintFragment;
import com.giparking.appgiparking.fragment.MovimientoFragment;
import com.giparking.appgiparking.fragment.ValidacionManualFragment;
import com.giparking.appgiparking.util.Constantes;
import com.giparking.appgiparking.util.ContenedorClass;
import com.giparking.appgiparking.util.str_global;
import com.giparking.appgiparking.view.LoguinActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Fragment fragment;

    private str_global a_str_global = str_global.getInstance();
    String caja;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        setTitle("ParkFacil");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        caja = a_str_global.getCaja_nombre().toString();

        TextView txtMovil = navigationView.getHeaderView(0).findViewById(R.id.txt_movil);
        txtMovil.setText(caja);

        iniciarMenu(navigationView);
        List<com.giparking.appgiparking.entity.Menu> arrayListProducto = (List<com.giparking.appgiparking.entity.Menu>) ContenedorClass.getInstance().getList_menu();

        for (int i=0;i<arrayListProducto.size();i++){

            if (arrayListProducto.get(i).getMenu().equals(Constantes.MENU_CONTROL)){

                navigationView.getMenu().findItem(R.id.nav_control).setVisible(true);
            }
            if (arrayListProducto.get(i).getMenu().equals(Constantes.MENU_CONTROL_MANUAL)){

                navigationView.getMenu().findItem(R.id.nav_control_manual).setVisible(true);
            }
            if (arrayListProducto.get(i).getMenu().equals(Constantes.MENU_REIMPRIMIR_COMPROBANTE)){

                navigationView.getMenu().findItem(R.id.nav_control_reimprimir).setVisible(true);
            }
            if (arrayListProducto.get(i).getMenu().equals(Constantes.MENU_ANULACION)){

                navigationView.getMenu().findItem(R.id.nav_anulacion).setVisible(true);
            }
            if (arrayListProducto.get(i).getMenu().equals(Constantes.MENU_MOVIMIENTO)){

                navigationView.getMenu().findItem(R.id.nav_movimiento).setVisible(true);
            }
            if (arrayListProducto.get(i).getMenu().equals(Constantes.MENU_CIERRE_CAJA)){

                navigationView.getMenu().findItem(R.id.nav_cierre_caja).setVisible(true);
            }
        }

        if (arrayListProducto.size()>0){

            String menu = arrayListProducto.get(0).getMenu();

            if (menu.equals(Constantes.MENU_CONTROL)){

                fragment = new ControlFragment();
                insertarFragmento();
            }
            if (menu.equals(Constantes.MENU_CONTROL_MANUAL)){

                fragment = new ValidacionManualFragment ();
                insertarFragmento();
            }
            if (menu.equals(Constantes.MENU_REIMPRIMIR_COMPROBANTE)){
                fragment = new ComprobantesFragment ();
                insertarFragmento();

            }
            if (menu.equals(Constantes.MENU_ANULACION)){

                fragment = new AnulacionMenuFragment();
                insertarFragmento();
            }
            if (menu.equals(Constantes.MENU_MOVIMIENTO)){


            }
            if (menu.equals(Constantes.MENU_CIERRE_CAJA)){

            }


        }

    }

    private void iniciarMenu(NavigationView navigationView) {

        navigationView.getMenu().findItem(R.id.nav_control).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_control_manual).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_control_reimprimir).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_anulacion).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_movimiento).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_cierre_caja).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_salir).setVisible(true);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_control) {
            // Handle the camera action
            fragment = new ControlFragment();
            insertarFragmento();

        } else if (id == R.id.nav_control_manual) {

            fragment = new ValidacionManualFragment();
            insertarFragmento();

        } else if (id == R.id.nav_control_reimprimir) {

            fragment = new ComprobantesFragment();
            insertarFragmento();

        }
        else if (id == R.id.nav_anulacion) {

            fragment = new AnulacionMenuFragment();
            insertarFragmento();

        } else if (id == R.id.nav_movimiento) {

            fragment = new MovimientoFragment();
            insertarFragmento();

        } else if (id == R.id.nav_cierre_caja) {

            fragment = new CerrarCajaFragment();
            insertarFragmento();

        } else if(id == R.id.nav_salir) {
            irLoguin();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void irLoguin() {
        Intent i = new Intent(MenuActivity.this, LoguinActivity.class);
        startActivity(i);
    }

    private void insertarFragmento(){

        if (fragment!=null){

            getSupportFragmentManager().beginTransaction().replace(R.id.contenedor,fragment).commit();
        }
    }
}

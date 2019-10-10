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


import com.giparking.appgiparking.fragment.ControlFragment;
import com.giparking.appgiparking.fragment.IngresoPrintFragment;
import com.giparking.appgiparking.fragment.ValidacionManualFragment;
import com.giparking.appgiparking.view.LoguinActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView txtMovil = navigationView.getHeaderView(0).findViewById(R.id.txt_movil);
        txtMovil.setText("CAJA MOVIL 02");
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

            Fragment fragment = new ValidacionManualFragment();
            FragmentManager fmanager = this.getSupportFragmentManager();
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

        } else if (id == R.id.nav_anulacion) {

        } else if (id == R.id.nav_movimiento) {

        } else if (id == R.id.nav_cierre_caja) {

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

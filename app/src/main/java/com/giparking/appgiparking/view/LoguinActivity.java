package com.giparking.appgiparking.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.giparking.appgiparking.MenuActivity;
import com.giparking.appgiparking.R;
import com.giparking.appgiparking.entity.Convenio;
import com.giparking.appgiparking.entity.Menu;
import com.giparking.appgiparking.entity.Producto;
import com.giparking.appgiparking.entity.TipoPago;
import com.giparking.appgiparking.rest.HelperWs;
import com.giparking.appgiparking.rest.MethodWs;
import com.giparking.appgiparking.util.ContenedorClass;
import com.giparking.appgiparking.util.str_global;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoguinActivity extends AppCompatActivity {

    @BindView(R.id.edt_usuario)
    EditText edt_usuario;

    @BindView(R.id.edt_contrasenia)
    EditText edt_contrasenia;

    @BindView(R.id.edt_llave)
    EditText edt_llave;

    SweetAlertDialog pd;
    String descripcion_respuesta = "";
    String usuario,contrasenia,llave,terminal;

    private str_global a_str_global = str_global.getInstance();
    private ContenedorClass contenedorClass = ContenedorClass.getInstance();
    List<Producto> list_producto = new ArrayList<>();
    List<Convenio> list_convenio = new ArrayList<>();
    List<TipoPago> list_tipopago = new ArrayList<>();
    List<Menu> list_menu = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loguin);
        ButterKnife.bind(this);


    }

    @OnClick(R.id.btn_ingresar)
    public void ingresar() {

         usuario = edt_usuario.getText().toString();
         contrasenia = edt_contrasenia.getText().toString();
         llave = edt_llave.getText().toString();
         terminal = "863558030455649";

        pd = new SweetAlertDialog(LoguinActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pd.getProgressHelper().setBarColor(Color.parseColor("#102670"));
        pd.setContentText("Por favor, espere...");
        pd.setCancelable(false);
        pd.show();


        MethodWs methodWs = HelperWs.getConfiguration().create(MethodWs.class);
        Call<ResponseBody> responseBodyCall = methodWs.accesarLoguin(llave, usuario, contrasenia, terminal);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {

                    ResponseBody informacion = response.body();
                    try {
                        String cadena_respuesta = informacion.string();

                        String[] parts = cadena_respuesta.split("©");
                        String respuesta_validacion = parts[0];

                        String[] parts_validacion = respuesta_validacion.split("¦");
                        String codigo_respuesta = parts_validacion[0];
                        if (!codigo_respuesta.equals("0")) {
                            descripcion_respuesta = parts_validacion[1];
                        }

                        if (codigo_respuesta.equals("0")) {

                            //Loguin Valido

                            //Extraemos valores usuario
                            String valores_usuario = parts[1];
                            String[] parts_valores_usuario = valores_usuario.split("¦");
                            String status_caja = parts_valores_usuario[0];

                            //Poblamos informacion del usuario
                            a_str_global.setCefectivo_cod_estado(parts_valores_usuario[0]);
                            a_str_global.setCod_corpempresa(parts_valores_usuario[1]);
                            a_str_global.setCod_sucursal(parts_valores_usuario[2]);
                            a_str_global.setCod_caja(parts_valores_usuario[3]);
                            a_str_global.setCod_usuario(parts_valores_usuario[4]);
                            a_str_global.setPersona(parts_valores_usuario[5]);
                            a_str_global.setUsuario_loguin(parts_valores_usuario[6]);
                            a_str_global.setCaja_nombre(parts_valores_usuario[7]);
                            a_str_global.setCaja_impresora(parts_valores_usuario[8]);
                            a_str_global.setCaja_cpago_manual(parts_valores_usuario[9]);


                            //Extraemos valores empresa
                            String valores_empresa = parts[2];
                            String[] parts_valores_empresa = valores_empresa.split("¦");

                            //Poblamos informacion de la empresa
                            a_str_global.setSistema_nombre(parts_valores_empresa[0]);
                            a_str_global.setSucursal_nombre(parts_valores_empresa[1]);
                            a_str_global.setEmpresa_nombre(parts_valores_empresa[2]);
                            a_str_global.setMascara_imglogo(parts_valores_empresa[3]);
                            a_str_global.setMascara_colorfondo(parts_valores_empresa[4]);
                            a_str_global.setMascara_colorletra(parts_valores_empresa[5]);

                            //Extraemos valores encabezado
                            String valores_encabezado = parts[3];
                            String[] parts_valores_encabezado = valores_encabezado.split("¦");

                            String cabecera_ticket = parts_valores_encabezado[0];
                            String cabecera_comprobante = parts_valores_encabezado[0];

                            String[] parts_cabecera_ticket = cabecera_ticket.split("|");
                            a_str_global.setVar_cabecera_t_0(parts_cabecera_ticket[0]);
                            a_str_global.setVar_cabecera_t_1(parts_cabecera_ticket[1]);
                            a_str_global.setVar_cabecera_t_2(parts_cabecera_ticket[2]);
                            a_str_global.setVar_cabecera_t_3(parts_cabecera_ticket[3]);

                            String[] parts_cabecera_comprobante = cabecera_comprobante.split("|");
                            a_str_global.setVar_cabecera_c_0(parts_cabecera_comprobante[0]);
                            a_str_global.setVar_cabecera_c_1(parts_cabecera_comprobante[1]);
                            a_str_global.setVar_cabecera_c_2(parts_cabecera_comprobante[2]);
                            a_str_global.setVar_cabecera_c_3(parts_cabecera_comprobante[3]);

                            //Extraemos valores productos
                            String valores_productos = parts[4];
                            String[] parts_valores_productos = valores_productos.split("¬");

                            for (int i=0; i < parts_valores_productos.length;i++){

                                String linea = parts_valores_productos[i];

                                //Extraemos valores de cada producto
                                String[] parts_valores_productos_detalle = linea.split("¦");

                                Producto producto = new Producto();
                                producto.setCod_producto(parts_valores_productos_detalle[0]);
                                producto.setNombre_producto(parts_valores_productos_detalle[1]);
                                producto.setTiene_convenio(parts_valores_productos_detalle[2]);
                                list_producto.add(producto);
                            }
                            contenedorClass.setList_producto(list_producto);

                            //Extraemos valores convenios
                            String valores_convenios = parts[5];
                            String[] parts_valores_convenios = valores_convenios.split("¬");

                            for (int i=0; i < parts_valores_convenios.length;i++){

                                String linea = parts_valores_convenios[i];

                                //Extraemos valores de cada convenio
                                String[] parts_valores_convenio_detalle = linea.split("¦");

                                Convenio convenio = new Convenio();
                                convenio.setCod_producto(parts_valores_convenio_detalle[0]);
                                convenio.setCod_convenio(parts_valores_convenio_detalle[1]);
                                convenio.setEmpresa_nombre(parts_valores_convenio_detalle[2]);
                                list_convenio.add(convenio);
                            }
                            contenedorClass.setList_convenio(list_convenio);

                            //Extraemos valores tipo pago
                            String valores_tipo_pago = parts[6];
                            String[] parts_valores_tipo_pago = valores_tipo_pago.split("¬");

                            for (int i=0; i < parts_valores_tipo_pago.length;i++){

                                String linea = parts_valores_tipo_pago[i];

                                //Extraemos valores de cada tipo de pago
                                String[] parts_valores_tipo_pago_detalle = linea.split("¦");

                                TipoPago tipoPago = new TipoPago();
                                tipoPago.setCod_tpago(parts_valores_tipo_pago_detalle[0]);
                                tipoPago.setDescripcion(parts_valores_tipo_pago_detalle[1]);
                                list_tipopago.add(tipoPago);
                            }
                            contenedorClass.setList_tipopago(list_tipopago);

                            //Caja Aperturada
                            if(status_caja.equals("6")){

                                //Extraemos valor caja aperturada
                                String codigo_caja_aperturada = parts[7];
                                a_str_global.setCod_cefectivo(codigo_caja_aperturada);

                                //Extraemos valores tipo pago
                                String valores_menu = parts[8];
                                String[] parts_valores_menu = valores_menu.split("¬");

                                for (int i=0; i < parts_valores_menu.length;i++){

                                    String linea = parts_valores_menu[i];

                                    //Extraemos valores de cada tipo de pago
                                    String[] parts_valores_menu_detalle = linea.split("¦");

                                    Menu menu = new Menu();
                                    menu.setCod_modulo(parts_valores_menu_detalle[0]);
                                    menu.setCod_menu(parts_valores_menu_detalle[1]);
                                    menu.setCod_taccion(parts_valores_menu_detalle[2]);
                                    menu.setMenu_cod_padre(parts_valores_menu_detalle[3]);
                                    menu.setModulo(parts_valores_menu_detalle[4]);
                                    menu.setMenu(parts_valores_menu_detalle[5]);
                                    menu.setMenu_accion(parts_valores_menu_detalle[6]);
                                    menu.setMenu_parametro(parts_valores_menu_detalle[7]);
                                    list_menu.add(menu);
                                }
                                contenedorClass.setList_menu(list_menu);

                                irMenuPrincipal();

                            //Caja Cerrada - Mostrar Dialogo
                            }else{

                                createLoginDialogo().show();

                            }

                            pd.dismiss();

                        } else {

                            //Loguin Invalido

                            pd.dismiss();
                            pd = new SweetAlertDialog(LoguinActivity.this, SweetAlertDialog.WARNING_TYPE);
                            pd.getProgressHelper().setBarColor(Color.parseColor("#03A9F4"));
                            pd.setContentText(descripcion_respuesta);
                            pd.setCancelable(false);
                            pd.show();
                            return;
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                        pd.dismiss();
                    }
                }
                else{
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

    private void irMenuPrincipal() {

        Intent i = new Intent(LoguinActivity.this, MenuActivity.class);
        startActivity(i);
    }

    public AlertDialog createLoginDialogo() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_apertura_caja, null);

        builder.setView(v);

        Button btn_aperturar_no =  v.findViewById(R.id.btn_aperturar_no);
        Button btn_aperturar_si =  v.findViewById(R.id.btn_aperturar_si);

        btn_aperturar_si.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        pd = new SweetAlertDialog(LoguinActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                        pd.getProgressHelper().setBarColor(Color.parseColor("#102670"));
                        pd.setContentText("Por favor, espere...");
                        pd.setCancelable(false);
                        pd.show();

                        String cod_sucursal = a_str_global.getCod_sucursal().toString();
                        String cod_usuario = a_str_global.getCod_usuario().toString();
                        String cod_caja = a_str_global.getCod_caja().toString();
                        String caja_nombre = a_str_global.getCaja_nombre().toString();

                        MethodWs methodWs = HelperWs.getConfiguration().create(MethodWs.class);
                        Call<ResponseBody> responseBodyCall = methodWs.aperturarCaja(llave, cod_sucursal,cod_usuario,cod_caja,caja_nombre);
                        responseBodyCall.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {


                                    ResponseBody informacion = response.body();
                                    try {
                                        String cadena_respuesta = informacion.string();

                                        String[] parts = cadena_respuesta.split("©");
                                        String respuesta_validacion = parts[0];

                                        String[] parts_validacion = respuesta_validacion.split("¦");
                                        String codigo_respuesta = parts_validacion[0];
                                        if (!codigo_respuesta.equals("0")) {
                                            descripcion_respuesta = parts_validacion[1];
                                        }

                                        if (codigo_respuesta.equals("0")) {

                                            //Extraemos valor caja aperturada
                                            String codigo_caja_aperturada = parts[1];
                                            a_str_global.setCod_cefectivo(codigo_caja_aperturada);

                                            //Extraemos valores tipo pago
                                            String valores_menu = parts[2];
                                            String[] parts_valores_menu = valores_menu.split("¬");

                                            for (int i=0; i < parts_valores_menu.length;i++){

                                                String linea = parts_valores_menu[i];

                                                //Extraemos valores de cada tipo de pago
                                                String[] parts_valores_menu_detalle = linea.split("¦");

                                                Menu menu = new Menu();
                                                menu.setCod_modulo(parts_valores_menu_detalle[0]);
                                                menu.setCod_menu(parts_valores_menu_detalle[1]);
                                                menu.setCod_taccion(parts_valores_menu_detalle[2]);
                                                menu.setMenu_cod_padre(parts_valores_menu_detalle[3]);
                                                menu.setModulo(parts_valores_menu_detalle[4]);
                                                menu.setMenu(parts_valores_menu_detalle[5]);
                                                menu.setMenu_accion(parts_valores_menu_detalle[6]);
                                                menu.setMenu_parametro(parts_valores_menu_detalle[7]);
                                                list_menu.add(menu);
                                            }
                                            contenedorClass.setList_menu(list_menu);

                                            irMenuPrincipal();

                                            pd.dismiss();

                                        }
                                        else {

                                            //Loguin Invalido

                                            pd.dismiss();
                                            pd = new SweetAlertDialog(LoguinActivity.this, SweetAlertDialog.WARNING_TYPE);
                                            pd.getProgressHelper().setBarColor(Color.parseColor("#03A9F4"));
                                            pd.setContentText(descripcion_respuesta);
                                            pd.setCancelable(false);
                                            pd.show();
                                            return;
                                        }

                                    }catch (IOException ex){
                                        ex.printStackTrace();
                                        pd.dismiss();
                                    }

                                }
                                else{
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
                }
        );

        btn_aperturar_no.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                    }
                }

        );

        return builder.create();
    }
}

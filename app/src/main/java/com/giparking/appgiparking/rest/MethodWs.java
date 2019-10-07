package com.giparking.appgiparking.rest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by jledesma on 10/7/19.
 */

public interface MethodWs {

    @GET("USUARIOLoginValida/")
    Call<ResponseBody> accesarLoguin(@Query("COD_CORPEMPRESA") String cod_corpempresa,
                              @Query("USUARIO_LOGIN") String usuario_login,
                              @Query("USUARIO_CLAVE") String usuario_clave,
                              @Query("TERMINAL_ID") String terminal_id);

    @GET("USUARIOLoginAperturaCaja/")
    Call<ResponseBody> aperturarCaja(@Query("COD_CORPEMPRESA") String cod_corpempresa,
                                     @Query("COD_SUCURSAL") String cod_sucursal,
                                     @Query("COD_USUARIO") String cod_usuario,
                                     @Query("COD_CAJA") String cod_caja,
                                     @Query("CAJA_NOMBRE") String caja_nombre);


    @GET("CONTROLAutoIngresoGrabar/")
    Call<ResponseBody> ControlIngresoGrabar(@Query("COD_CORPEMPRESA") String cod_corpempresa,
                                     @Query("COD_SUCURSAL") String cod_sucursal,
                                     @Query("COD_USUARIO") String cod_usuario,
                                     @Query("COD_CEFECTIVO") String cod_cefectivo,
                                     @Query("NRO_PLACA") String nro_placa);




}

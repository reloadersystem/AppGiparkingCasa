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
    Call<ResponseBody> controlIngresoGrabar(@Query("COD_CORPEMPRESA") String cod_corpempresa,
                                     @Query("COD_SUCURSAL") String cod_sucursal,
                                     @Query("COD_USUARIO") String cod_usuario,
                                     @Query("COD_CEFECTIVO") String cod_cefectivo,
                                     @Query("NRO_PLACA") String nro_placa);


    @GET("CONTROLAutoSalidaBuscar/")
    Call<ResponseBody> ControlAutoSalidaBusca(@Query("BusCriterio") String cod_corpempresa,
                                            @Query("COD_SUCURSAL") String cod_sucursal,
                                            @Query("COD_MOVIMIENTO") String cod_usuario,
                                            @Query("NRO_PLACA") String nro_placa);


    @GET("CONTROLAutoSalidaCalcular/")
    Call<ResponseBody> ControlAutoSalidaCalcular(@Query("COD_TVALIDACION") String cod_tvalidacion,
                                              @Query("COD_SUCURSAL") String cod_sucursal,
                                              @Query("COD_PRODUCTO") String cod_producto,
                                              @Query("COD_CONVENIO") String cod_convenio,
                                              @Query("INGRESO_FECHA") String ingreso_fecha,
                                              @Query("INGRESO_HORA") String ingreso_hora,
                                              @Query("CONVE_CODIGO") String conve_codigo,
                                              @Query("CONVE_FECHA") String conve_fecha,
                                              @Query("CONVE_TIPO") String conve_tipo,
                                              @Query("CONVE_SERIE") String conve_serie,
                                              @Query("CONVE_NUMERO") String conve_numero,
                                              @Query("CONVE_MONTO") String conve_monto);

    @GET("COMPROBANTEEmpresaBuscar/")
    Call<ResponseBody> comprobanteEmpresaBuscar(@Query("RUC") String ruc);

    @GET("CONTROLAutoSalidaGrabar/")
    Call<ResponseBody> controlAutoSalidaGrabar(@Query("COD_CORPEMPRESA") String cod_corpempresa,
                                               @Query("COD_SUCURSAL") String cod_sucursal,
                                               @Query("COD_CEFECTIVO") String cod_cefectivo,
                                               @Query("COD_CAJA") String cod_caja,
                                               @Query("COD_USUARIO") String cod_usuario,
                                               @Query("COD_TCOMPROBANTE") String cod_tcomprobante,
                                               @Query("COD_MOVIMIENTO") String cod_movimiento,
                                               @Query("COD_TVALIDACION") String cod_tvalidacion,
                                               @Query("COD_PRODUCTO") String cod_producto,
                                               @Query("COD_CONVENIO") String cod_convenio,
                                               @Query("INGRESO_FECHA") String ingresa_fecha,
                                               @Query("INGRESO_HORA") String ingreso_hora,
                                               @Query("EMP_RUC") String emp_ruc,
                                               @Query("NRO_PLACA") String nro_placa,
                                               @Query("CONVE_CODIGO") String conve_codigo,
                                               @Query("CONVE_FECHA") String conve_fecha,
                                               @Query("CONVE_TIPO") String conve_tipo,
                                               @Query("CONVE_SERIE") String conve_serie,
                                               @Query("CONVE_NUMERO") String conve_numero,
                                               @Query("CONVE_MONTO") String conve_monto);





}

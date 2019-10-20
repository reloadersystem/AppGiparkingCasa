package com.giparking.appgiparking.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.giparking.appgiparking.R;
import com.giparking.appgiparking.entity.Comprobante;
import com.giparking.appgiparking.interfaces.OnPrintListener;

import java.util.ArrayList;

/**
 * Created by jledesma on 10/17/19.
 */

public class ComprobanteAdapter extends RecyclerView.Adapter<ComprobanteAdapter.ComprobanteAdapterViewHolder>{

    Context context;
    ArrayList<Comprobante> list_comprobante;
    private OnPrintListener onPrintListener;

    public  void setOnPrintListener(OnPrintListener onPrintListener)
    {
        this.onPrintListener = onPrintListener;
    }

    public ComprobanteAdapter(Context context, ArrayList<Comprobante> list_comprobante) {

        this.context = context;
        this.list_comprobante = list_comprobante;
    }

    @Override
    public ComprobanteAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comprobante,parent,false);
        return new ComprobanteAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ComprobanteAdapterViewHolder holder, int position) {

        Comprobante comprobante = list_comprobante.get(position);

        holder.tv_c_fecha.setText(comprobante.getFecha().toString());
        holder.tv_c_tipo.setText(comprobante.getTipo().toString());
        holder.tv_c_numero.setText(comprobante.getNumero().toString());
        holder.tv_c_placa.setText(comprobante.getPlaca().toString());
        holder.tv_c_cliente.setText(comprobante.getCliente().toString());
        holder.tv_c_monto.setText(comprobante.getMonto().toString());
    }

    @Override
    public int getItemCount() {
        return list_comprobante.size();
    }

    public class ComprobanteAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView tv_c_fecha,tv_c_numero,tv_c_placa,tv_c_cliente,tv_c_monto,tv_c_tipo;
        ImageView imageView2;

        public ComprobanteAdapterViewHolder(View itemView) {
            super(itemView);

            tv_c_fecha = itemView.findViewById(R.id.tv_c_fecha);
            tv_c_tipo = itemView.findViewById(R.id.tv_c_tipo);
            tv_c_numero = itemView.findViewById(R.id.tv_c_numero);
            tv_c_placa = itemView.findViewById(R.id.tv_c_placa);
            tv_c_cliente = itemView.findViewById(R.id.tv_c_cliente);
            tv_c_monto = itemView.findViewById(R.id.tv_c_monto);

            imageView2 = itemView.findViewById(R.id.imageView2);


            imageView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = getAdapterPosition();

                    if(position != RecyclerView.NO_POSITION)
                    {
                        onPrintListener.onAccionClicked(position);
                    }

                }
            });
        }
    }
}

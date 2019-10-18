package com.giparking.appgiparking.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.giparking.appgiparking.R;
import com.giparking.appgiparking.entity.Comprobante;
import com.giparking.appgiparking.entity.Movimiento;

import java.util.ArrayList;

/**
 * Created by jledesma on 10/17/19.
 */

public class MovimientoAdapter extends RecyclerView.Adapter<MovimientoAdapter.MovimientoAdapterViewHolder>{

    Context context;
    ArrayList<Movimiento> list_movimiento;

    public MovimientoAdapter(Context context, ArrayList<Movimiento> list_movimiento) {

        this.context = context;
        this.list_movimiento = list_movimiento;
    }

    @Override
    public MovimientoAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movimiento,parent,false);
        return new MovimientoAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovimientoAdapterViewHolder holder, int position) {

        Movimiento movimiento = list_movimiento.get(position);

        holder.tv_etiqueta.setText(movimiento.getTitulo().toString());
        holder.tv_valor.setText(movimiento.getValor().toString());


    }

    @Override
    public int getItemCount() {
        return list_movimiento.size();
    }

    public class MovimientoAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView tv_etiqueta,tv_valor;


        public MovimientoAdapterViewHolder(View itemView) {
            super(itemView);

            tv_etiqueta = itemView.findViewById(R.id.tv_etiqueta);
            tv_valor = itemView.findViewById(R.id.tv_valor);

        }
    }
}

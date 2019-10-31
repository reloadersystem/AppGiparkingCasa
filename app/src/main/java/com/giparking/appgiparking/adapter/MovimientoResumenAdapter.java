package com.giparking.appgiparking.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.giparking.appgiparking.R;
import com.giparking.appgiparking.entity.Movimiento;

import java.util.ArrayList;

/**
 * Created by jledesma on 10/31/19.
 */

public class MovimientoResumenAdapter extends RecyclerView.Adapter<MovimientoResumenAdapter.MovimientoResumenAdapterViewHolder>{

    Context context;
    ArrayList<Movimiento> list_movimiento;

    public MovimientoResumenAdapter(Context context, ArrayList<Movimiento> list_movimiento) {

        this.context = context;
        this.list_movimiento = list_movimiento;
    }

    @Override
    public MovimientoResumenAdapter.MovimientoResumenAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movimiento_resumen,parent,false);
        return new MovimientoResumenAdapter.MovimientoResumenAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovimientoResumenAdapter.MovimientoResumenAdapterViewHolder holder, int position) {

        Movimiento movimiento = list_movimiento.get(position);

        holder.tv_etiqueta_resumen.setText(movimiento.getTitulo().toString());



    }

    @Override
    public int getItemCount() {
        return list_movimiento.size();
    }

    public class MovimientoResumenAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView tv_etiqueta_resumen;


        public MovimientoResumenAdapterViewHolder(View itemView) {
            super(itemView);

            tv_etiqueta_resumen = itemView.findViewById(R.id.tv_etiqueta_resumen);

        }
    }
}

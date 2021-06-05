package com.example.historiasclinicas.Controlador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.historiasclinicas.Modelo.Historia;
import com.example.historiasclinicas.R;

import java.util.List;

public class EficienciaAdapter extends RecyclerView.Adapter<EficienciaAdapter.ViewHolder> {

    Context ctx;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtfecha, txtatencion, txtatendido, txtdoctor;

        public ViewHolder(@androidx.annotation.NonNull View itemView) {
            super(itemView);
            txtfecha = (TextView) itemView.findViewById(R.id.txtfecha);
            txtatencion = (TextView) itemView.findViewById(R.id.txtatencion);
            txtatendido = (TextView) itemView.findViewById(R.id.txtatendido);
            txtdoctor = (TextView) itemView.findViewById(R.id.txtdoctor);
        }
    }


    public List<Historia> lista;

    public EficienciaAdapter (List<Historia> comidaLista){
        this.lista = comidaLista;
    }

    @androidx.annotation.NonNull
    @Override
    public ViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_eficiencia,parent,false);

        ctx = parent.getContext();

        ViewHolder viewHolder = new EficienciaAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull ViewHolder holder, int i) {
        holder.txtfecha.setText(lista.get(i).getFecha()+"");
        holder.txtatencion.setText(lista.get(i).getIdAtencion()+"");
        if(lista.get(i).getAtendido()==1){
            holder.txtatendido.setText("SI");
        }else{
            holder.txtatendido.setText("NO");
        }

        holder.txtdoctor.setText(lista.get(i).getDoctor()+"");
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public void actualizar(List<Historia> nuevaLista){
        lista = nuevaLista;
        notifyDataSetChanged();
    }

}
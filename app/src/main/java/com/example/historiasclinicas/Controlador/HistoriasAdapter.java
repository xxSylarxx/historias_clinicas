package com.example.historiasclinicas.Controlador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.historiasclinicas.Modelo.Historia;
import com.example.historiasclinicas.R;

import java.util.List;

public class HistoriasAdapter extends RecyclerView.Adapter<HistoriasAdapter.ViewHolder> {

    private OnItemClickListener mListener;
    Context ctx;
    int dato;

    public interface OnItemClickListener{
        void OnItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtfecha, txtatencion, txtuser,txtcuenta;

        public ViewHolder(@androidx.annotation.NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            txtfecha = (TextView) itemView.findViewById(R.id.txtfecha);
            txtatencion = (TextView) itemView.findViewById(R.id.txtatencion);
            txtuser = (TextView) itemView.findViewById(R.id.txtdoctor);
            txtcuenta = (TextView) itemView.findViewById(R.id.txtcuenta);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.OnItemClick(position);
                        }
                    }
                }
            });
        }
    }


    public List<Historia> lista;

    public HistoriasAdapter (List<Historia> comidaLista, int dato){
        this.lista = comidaLista;
        this.dato = dato;
    }

    @androidx.annotation.NonNull
    @Override
    public HistoriasAdapter.ViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_historia,parent,false);

        ctx = parent.getContext();

        HistoriasAdapter.ViewHolder viewHolder = new HistoriasAdapter.ViewHolder(view,mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull HistoriasAdapter.ViewHolder holder, int i) {
        holder.txtfecha.setText(lista.get(i).getFecha()+"\n"+lista.get(i).getHora());
        holder.txtatencion.setText(lista.get(i).getIdAtencion());
        if(dato==0){
            holder.txtuser.setText(lista.get(i).getDoctor());
        }else{
            holder.txtuser.setText(lista.get(i).getCodPaciente());
        }

        holder.txtcuenta.setText(lista.get(i).getCuenta());
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
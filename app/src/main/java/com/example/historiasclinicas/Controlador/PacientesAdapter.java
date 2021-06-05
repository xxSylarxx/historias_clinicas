package com.example.historiasclinicas.Controlador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.historiasclinicas.Modelo.Paciente;
import com.example.historiasclinicas.R;

import java.util.List;

public class PacientesAdapter extends RecyclerView.Adapter<PacientesAdapter.ViewHolder> {

    private OnItemClickListener mListener;
    Context ctx;

    public interface OnItemClickListener{
        void OnVerClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtnombres, txtapellidos;
        Button btnver;

        public ViewHolder(@androidx.annotation.NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            txtnombres = (TextView) itemView.findViewById(R.id.txtnombres);
            txtapellidos = (TextView) itemView.findViewById(R.id.txtapellidos);
            btnver = (Button) itemView.findViewById(R.id.btnver);

            btnver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.OnVerClick(position);
                        }
                    }
                }
            });
        }
    }


    public List<Paciente> lista;

    public PacientesAdapter(){}

    public PacientesAdapter (List<Paciente> comidaLista){
        this.lista = comidaLista;
    }

    @androidx.annotation.NonNull
    @Override
    public ViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_paciente,parent,false);

        ctx = parent.getContext();

        ViewHolder viewHolder = new ViewHolder(view, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull ViewHolder holder, int i) {
        holder.txtapellidos.setText(lista.get(i).getApellidos());
        holder.txtnombres.setText(lista.get(i).getNombres());
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public void actualizar(List<Paciente> nuevaLista){
        lista = nuevaLista;
        notifyDataSetChanged();
    }

}
package com.example.historiasclinicas.Controlador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.historiasclinicas.Modelo.Historia;
import com.example.historiasclinicas.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ReporteAdapter extends RecyclerView.Adapter<ReporteAdapter.ViewHolder> {

    Context ctx;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtfecha,txtpeso,txttalla,txttemperatura,txtpresion,
                txtdiagnostico,txtrecomendacion,txtlaboratorio,txtradiologia,
                tvradio, tvimagen, txthora,txtdoctor, txthoraD;
        ImageView txtrayosx;
        View separador;

        public ViewHolder(@androidx.annotation.NonNull View itemView) {
            super(itemView);
            txtpeso = (TextView) itemView.findViewById(R.id.txtpeso);
            txtfecha = (TextView) itemView.findViewById(R.id.txtfecha);
            txttalla = (TextView) itemView.findViewById(R.id.txttalla);
            txttemperatura = (TextView) itemView.findViewById(R.id.txttemperatura);
            txtpresion = (TextView) itemView.findViewById(R.id.txtpresion);
            txtdiagnostico = (TextView) itemView.findViewById(R.id.txtdiagnostico);
            txtrecomendacion = (TextView) itemView.findViewById(R.id.txtrecomendaciones);
            txtlaboratorio = (TextView) itemView.findViewById(R.id.txtlaboratorios);
            txtradiologia = (TextView) itemView.findViewById(R.id.txtradiologia);
            txtrayosx = itemView.findViewById(R.id.txtrayos);
            separador = (View) itemView.findViewById(R.id.separador);
            tvradio = itemView.findViewById(R.id.txtradio);
            tvimagen = itemView.findViewById(R.id.textView14);
            txtdoctor = itemView.findViewById(R.id.txtdoctor);
            txthora = itemView.findViewById(R.id.txthora);
            txthoraD = itemView.findViewById(R.id.txthoraD);
        }
    }


    public List<Historia> lista;

    public ReporteAdapter (List<Historia> comidaLista){
        this.lista = comidaLista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_reporte,parent,false);

        ctx = parent.getContext();

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        holder.txtfecha.setText(lista.get(i).getFecha());
        holder.txtdiagnostico.setText(lista.get(i).getDiagnostico());
        holder.txtlaboratorio.setText(lista.get(i).getLaboratorio());
        holder.txtpeso.setText(lista.get(i).getPeso()+"");
        holder.txtpresion.setText(lista.get(i).getPresion()+"");
        if(!lista.get(i).getRadiologia().isEmpty()){
            holder.txtradiologia.setText(lista.get(i).getRadiologia()+"");
        }else{
            holder.tvradio.setVisibility(View.GONE);
            holder.txtradiologia.setVisibility(View.GONE);
        }
        if(lista.get(i).getRayosx()!=null && !lista.get(i).getRayosx().isEmpty()){
            Picasso.with(ctx).load(lista.get(i).getRayosx()).into(holder.txtrayosx);
        }else{
            holder.txtrayosx.setVisibility(View.GONE);
            holder.tvimagen.setVisibility(View.GONE);
        }
        holder.txtrecomendacion.setText(lista.get(i).getRecomendaciones());
        holder.txttalla.setText(lista.get(i).getTalla()+"");
        holder.txttemperatura.setText(lista.get(i).getTemperatura()+"");
        holder.txthora.setText(lista.get(i).getDoctor());
        holder.txthora.setText(lista.get(i).getHora());
        holder.txthoraD.setText(lista.get(i).getHoraDiagnosticada());


        if(i==lista.size()-1){
            holder.separador.setVisibility(View.GONE);
        }
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
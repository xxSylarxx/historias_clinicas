package com.example.historiasclinicas.Vista;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.historiasclinicas.Controlador.EficienciaAdapter;
import com.example.historiasclinicas.Modelo.Historia;
import com.example.historiasclinicas.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EficienciaFragment extends Fragment {

    TextView txtnombreDoc, txtatendidos, txtnoatendidos, txteficiencia;
    Button btnfiltrarFecha;
    RecyclerView recyclerView;
    EficienciaAdapter eficienciaAdapter;
    DatabaseReference databaseReference;
    List<Historia> listaHistorias = new ArrayList<>();
    ProgressDialog dialog;
    double atendidos = 0, noAtendidos = 0;
    double eficiencia = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_eficiencia, container, false);
        txtatendidos = view.findViewById(R.id.txtatendidos);
        txtnoatendidos = view.findViewById(R.id.txtnoatendidos);
        txtnombreDoc = view.findViewById(R.id.txtnombreDoc);
        txteficiencia = view.findViewById(R.id.txteficiencia);
        btnfiltrarFecha = view.findViewById(R.id.btnfiltrarFecha);
        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Cargando");
        dialog.setCancelable(false);
        dialog.show();

        txtnombreDoc.setText(MenuDoctorActivity.nombre);

        eficienciaAdapter = new EficienciaAdapter(new ArrayList<Historia>());
        recyclerView.setAdapter(eficienciaAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        listarFirebase();

        btnfiltrarFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                final int dia = c.get(Calendar.DAY_OF_MONTH);
                final int mes = c.get(Calendar.MONTH)+1;
                final int anio = c.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        String dia = dayOfMonth+"";
                        String mes = (monthOfYear+1)+"";
                        if((monthOfYear+1)<10){
                            mes = "0"+mes;
                        }
                        if(dayOfMonth<10){
                            dia = "0"+dia;
                        }
                        btnfiltrarFecha.setText(dia+"/"+mes+"/"+((int) year%100));


                        String fechaElegida = btnfiltrarFecha.getText().toString();
                        String fechaTrans = fechaElegida.split("/")[2]+""+fechaElegida.split("/")[1]+""+fechaElegida.split("/")[0];

                        int fechaAct = Integer.parseInt(fechaTrans);
                        filtrar(fechaAct);

                    }
                },anio,mes-1,dia);
                datePickerDialog.show();

            }
        });


        return view;

    }

    private void filtrar(int fecha) {
        atendidos = 0;
        noAtendidos = 0;
        dialog.show();

        List<Historia> tmp = new ArrayList<>();
        for(int i=0;i<listaHistorias.size();i++){
            final String[] fechaActual = listaHistorias.get(i).getFecha().split("/");
            final int actual = Integer.parseInt(fechaActual[2]+""+fechaActual[1]+""+fechaActual[0]);
            //Log.e("fecha", fecha+" "+actual);

            if(actual==fecha){
                tmp.add(listaHistorias.get(i));

                if(listaHistorias.get(i).getAtendido()==1){
                    atendidos++;
                }else{
                    noAtendidos++;
                }
            }
        }
        dialog.dismiss();
        eficienciaAdapter.actualizar(tmp);
        if((atendidos+noAtendidos)>0){
            eficiencia = (atendidos/tmp.size())*100;
        }else{
            eficiencia = 0;
        }

        //Log.e("NUMEROS", atendidos+" "+eficiencia+" "+noAtendidos+" "+(atendidos/tmp.size()));

        txtatendidos.setText(quitarCeros(""+atendidos));
        txtnoatendidos.setText(quitarCeros(""+noAtendidos));
        txteficiencia.setText(Math.round(eficiencia)+"%");

    }

    public String quitarCeros(String numero){
        final int i = numero.indexOf(".");
        return (numero.substring(0,i));
    }

    private void listarFirebase() {
        databaseReference.child("Historias").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaHistorias.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Historia historia = snapshot.getValue(Historia.class);
                    listaHistorias.add(historia);
                }
                try{
                    dialog.dismiss();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
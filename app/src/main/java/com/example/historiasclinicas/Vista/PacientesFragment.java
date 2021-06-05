package com.example.historiasclinicas.Vista;

import android.app.DatePickerDialog;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.historiasclinicas.Controlador.HistoriasAdapter;
import com.example.historiasclinicas.Controlador.Metodos;
import com.example.historiasclinicas.Modelo.Historia;
import com.example.historiasclinicas.Modelo.Paciente;
import com.example.historiasclinicas.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PacientesFragment extends Fragment {

    RecyclerView recyclerView;
    HistoriasAdapter adapter;
    List<Historia> lista = new ArrayList<>();
    List<Historia> listaGeneral = new ArrayList<>();
    DatabaseReference databaseReference;
    TextView txtnombres;
    String nombres;

    public PacientesFragment(String nombres) {
        this.nombres = nombres;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pacientes, container, false);
        txtnombres = view.findViewById(R.id.txtnombres);
        recyclerView = view.findViewById(R.id.recyclerView);

        databaseReference = FirebaseDatabase.getInstance().getReference("");

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HistoriasAdapter(lista,1);
        recyclerView.setAdapter(adapter);

        listarFBhistoria();
        txtnombres.setText(nombres);

        adapter.setOnItemClickListener(new HistoriasAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                final Calendar c = Calendar.getInstance();
                int hora = c.get(Calendar.HOUR_OF_DAY)*60;
                int minuto = c.get(Calendar.MINUTE);

                String[] horaArray = lista.get(position).getHora().split(":");
                int horaC = Integer.parseInt(""+horaArray[0])*60;
                int minC = Integer.parseInt(""+horaArray[1]);

                int horaActual = hora+minuto;
                Log.e("xd",(horaC+minC)+" "+horaActual);
                if((horaC+minC) <= horaActual){
                    Paciente paciente = new Paciente();
                    for(Paciente p: MenuDoctorActivity.listaPacientes){
                        if(p.getIdAtencion().equals(lista.get(position).getIdAtencion())){
                            paciente = p;
                            break;
                        }
                    }
                    Metodos.cambiarFragment(getActivity(),new DiagnosticoFragment(lista.get(position),
                            paciente,listaGeneral),"Pacientes",null,true);
                }else{
                    Toast.makeText(getContext(), "Fuera de hora", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void listarFBhistoria() {

        databaseReference.child("Historias").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lista.clear();
                listaGeneral.clear();
                for(DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    Historia h = objSnapshot.getValue(Historia.class);
                    String[] array = h.getFecha().split("/");
                    int fecha = Integer.parseInt(array[2]+""+array[1]+""+array[0]);
                    if(h.getAtendido()==0 &&
                       h.getDoctor().equalsIgnoreCase(MenuDoctorActivity.nombre)
                       && fecha==obtenerFechaActual()
                    ){
                        lista.add(h);
                        Log.e("FECHAS", obtenerFechaActual()+" "+fecha);
                    }
                    listaGeneral.add(h);

                }

                adapter.actualizar(lista);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    int obtenerFechaActual(){
        final Calendar c = Calendar.getInstance();
        String dia = c.get(Calendar.DAY_OF_MONTH)+"";
        String mes= (c.get(Calendar.MONTH)+1)+"";
        if(c.get(Calendar.DAY_OF_MONTH) <10){
            dia = "0"+c.get(Calendar.DAY_OF_MONTH);
        }
        if((c.get(Calendar.MONTH)+1) <10){
            mes = "0"+(c.get(Calendar.MONTH)+1);
        }

        final String fechaActual = ((int) c.get(Calendar.YEAR)%100)+""+mes+""+dia;
        final int actual = Integer.parseInt(fechaActual);
        return actual;
    }

}
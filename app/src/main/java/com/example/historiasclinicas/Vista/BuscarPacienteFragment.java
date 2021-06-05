package com.example.historiasclinicas.Vista;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.historiasclinicas.Controlador.Metodos;
import com.example.historiasclinicas.Modelo.Paciente;
import com.example.historiasclinicas.Controlador.PacientesAdapter;
import com.example.historiasclinicas.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BuscarPacienteFragment extends Fragment {

    LinearLayout linearLayout;
    RecyclerView recyclerView;
    PacientesAdapter adapter;
    List<Paciente> lista = new ArrayList<>();
    List<Paciente> tmplista = new ArrayList<>();
    Button btnbuscar;
    TextView txtdni, txtnombres, txtapellidos;
    DatabaseReference databaseReference;

    public BuscarPacienteFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buscar_paciente, container, false);
        txtdni = view.findViewById(R.id.txtdni);
        txtnombres = view.findViewById(R.id.txtnombres);
        txtapellidos = view.findViewById(R.id.txtapellidos);
        linearLayout = view.findViewById(R.id.linearLayout);
        btnbuscar = view.findViewById(R.id.btnbuscar);
        recyclerView = view.findViewById(R.id.recyclerView);

        databaseReference = FirebaseDatabase.getInstance().getReference("Pacientes");

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PacientesAdapter(lista);
        recyclerView.setAdapter(adapter);

        listarFBPacientes();

        btnbuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tmplista.clear();
                Metodos.closeTeclado(getActivity().getCurrentFocus(),getContext());
                String dni = txtdni.getText().toString().trim();
                String nombres = txtnombres.getText().toString().trim();
                String apellidos = txtapellidos.getText().toString().trim();
                if((!nombres.isEmpty() && !apellidos.isEmpty()) || (!dni.isEmpty() && dni.length() == 8) ){
                    if(!nombres.isEmpty() && !apellidos.isEmpty()){
                        for(int i=0;i<lista.size();i++){
                            if(lista.get(i).getApellidos().equalsIgnoreCase(apellidos) &&
                                    lista.get(i).getNombres().equalsIgnoreCase(nombres)){
                                tmplista.add(lista.get(i));
                                break;
                            }
                        }
                    }else if(!dni.isEmpty()){
                        for(int i=0;i<lista.size();i++){
                            if(lista.get(i).getId().equals(dni)){
                                tmplista.add(lista.get(i));
                                break;
                            }
                        }
                    }

                    if(tmplista.size()>0){
                        adapter.actualizar(tmplista);
                        linearLayout.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }else{
                        linearLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                        dialog.setTitle("Alerta")
                                .setMessage("NO SE ENCONTRO REGISTRO ¿DESEA REGISTRAR NUEVO PACIENTE?")
                                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialoginterface, int i) {
                                        dialoginterface.cancel();
                                    }
                                })
                                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialoginterface, int i) {
                                        AperturaHistoriaFragment fragment = new AperturaHistoriaFragment(txtdni.getText().toString());
                                        Metodos.cambiarFragment(getActivity(),fragment,"BuscarPaciente",null,true);
                                    }
                                }).show();
                    }
                } else{
                    Toast.makeText(getActivity(), "Complete los campos", Toast.LENGTH_SHORT).show();
                    linearLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });

        adapter.setOnItemClickListener(new PacientesAdapter.OnItemClickListener() {
            @Override
            public void OnVerClick(int position) {
                HistoriaClinicaFragment fragment = new HistoriaClinicaFragment(tmplista.get(0));
                Metodos.cambiarFragment(getActivity(),fragment,"BuscarPaciente",null,true);
            }
        });

        return view;
    }

    private void listarFBPacientes() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lista.clear();

                for(DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    Paciente p = objSnapshot.getValue(Paciente.class);
                    lista.add(p);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
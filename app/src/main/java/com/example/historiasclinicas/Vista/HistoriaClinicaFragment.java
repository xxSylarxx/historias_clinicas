package com.example.historiasclinicas.Vista;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.historiasclinicas.Controlador.Metodos;
import com.example.historiasclinicas.Controlador.PdfHistoria;
import com.example.historiasclinicas.Modelo.Historia;
import com.example.historiasclinicas.Controlador.HistoriasAdapter;
import com.example.historiasclinicas.Modelo.Paciente;
import com.example.historiasclinicas.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistoriaClinicaFragment extends Fragment {

    private int RC_PERMISSION_WRITE_EXTERNAL_STORAGE = 24;
    RecyclerView recyclerView;
    HistoriasAdapter adapter;
    List<Historia> lista = new ArrayList<>();
    List<Historia> listaAtendida = new ArrayList<>();
    TextView txtpaciente;
    Button btndescargar, btnagendarcita;
    DatabaseReference databaseReference;
    Paciente paciente;
    String codigoPaciente = "";
    String nombrePaciente = "";
    String apellidoPaciente = "";


    public HistoriaClinicaFragment(Paciente paciente) {
        this.paciente = paciente;
        this.codigoPaciente = paciente.getId();
        this.nombrePaciente = paciente.getNombres();
        this.apellidoPaciente = paciente.getApellidos();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historia_clinica, container, false);
        txtpaciente = view.findViewById(R.id.txtPaciente);
        btndescargar = view.findViewById(R.id.btndescargar);
        btnagendarcita = view.findViewById(R.id.btnagendarcita);
        recyclerView = view.findViewById(R.id.recyclerView);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HistoriasAdapter(lista,0);
        recyclerView.setAdapter(adapter);

        listarFBhistoria();

        txtpaciente.setText("Paciente: "+apellidoPaciente+" "+nombrePaciente);

        btnagendarcita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AgendarCitaFragment registrarHistoriaFragment = new AgendarCitaFragment(paciente);
                Metodos.cambiarFragment(getActivity(),registrarHistoriaFragment,"HistoriaClinica",null,true);
            }
        });

        btndescargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    createPDF();
                }else {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            RC_PERMISSION_WRITE_EXTERNAL_STORAGE);
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
                listaAtendida.clear();

                for(DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    Historia h = objSnapshot.getValue(Historia.class);
                    if(h.getCodPaciente().equals(codigoPaciente)){
                        lista.add(h);
                    }
                    if(h.getCodPaciente().equals(codigoPaciente) && h.getAtendido()==1){
                        listaAtendida.add(h);
                    }
                }

                adapter.actualizar(lista);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createPDF(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<String[]> tmplista = new ArrayList<>();
                for(int i=0;i<listaAtendida.size();i++) {

                    tmplista.add(new String[]{"Fecha:", "Hora"});
                    tmplista.add(new String[]{""+listaAtendida.get(i).getFecha(), ""+listaAtendida.get(i).getHora()});
                    tmplista.add(new String[]{"Doctor Asignado:", "Hora Diagnosticada"});
                    tmplista.add(new String[]{""+listaAtendida.get(i).getDoctor(), ""+listaAtendida.get(i).getHoraDiagnosticada()});
                    tmplista.add(new String[]{"Peso:", "Talla:"});
                    tmplista.add(new String[]{""+listaAtendida.get(i).getPeso(),""+listaAtendida.get(i).getTalla()});
                    tmplista.add(new String[]{"Temperatura:", "Presion:"});
                    tmplista.add(new String[]{""+listaAtendida.get(i).getTemperatura(),""+listaAtendida.get(i).getPresion()});
                    tmplista.add(new String[]{"DX Ingreso:", "Recomendaciones:"});
                    tmplista.add(new String[]{""+listaAtendida.get(i).getDiagnostico(),""+listaAtendida.get(i).getRecomendaciones()});
                    tmplista.add(new String[]{"Laboratorio:","Radiologia"});
                    tmplista.add(new String[]{""+listaAtendida.get(i).getLaboratorio(),""+listaAtendida.get(i).getRadiologia()});


                }

                PdfHistoria pdfHistoria = new PdfHistoria(getActivity(), "Historia_"+paciente.getApellidos()+"_"+paciente.getNombres());
                pdfHistoria.openDocument();
                pdfHistoria.addMetaData("HISTORIAL CLINICO",paciente.getApellidos()+" "+paciente.getNombres());
                pdfHistoria.addTitles("HISTORIAL CLINICO", "Paciente:"+paciente.getApellidos()+" "+paciente.getNombres());
                pdfHistoria.createTable2(2,tmplista);
                pdfHistoria.closeDocument();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "DESCARGADO", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == RC_PERMISSION_WRITE_EXTERNAL_STORAGE){
            if(grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED){
                createPDF();
            }else{
                Toast.makeText(getActivity(), "PERMISO DENEGADO", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
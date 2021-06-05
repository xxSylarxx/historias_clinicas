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
import android.widget.TextView;
import android.widget.Toast;

import com.example.historiasclinicas.Controlador.PdfHistoria;
import com.example.historiasclinicas.Controlador.ReporteAdapter;
import com.example.historiasclinicas.Modelo.Historia;
import com.example.historiasclinicas.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ReporteHistoriaFragment extends Fragment {

    private int RC_PERMISSION_WRITE_EXTERNAL_STORAGE = 24;
    String apellido="", nombre="";
    List<Historia> listaHistoria;
    ReporteAdapter adapter;
    TextView txtedad,txtpaciente;
    RecyclerView recyclerView;
    FloatingActionButton descargar;
    int edad;

    public ReporteHistoriaFragment(String apellido, String nombre, int edad, List<Historia> lista) {
        this.apellido = apellido;
        this.nombre = nombre;
        this.edad = edad;
        this.listaHistoria = lista;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reporte_historia, container, false);
        txtedad = view.findViewById(R.id.txtedad);
        txtpaciente = view.findViewById(R.id.txtpaciente);
        recyclerView = view.findViewById(R.id.recyclerView);
        descargar = view.findViewById(R.id.descargar);

        txtedad.setText(edad+"");
        txtpaciente.setText(apellido+" "+nombre);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReporteAdapter(listaHistoria);
        recyclerView.setAdapter(adapter);

        descargar.setOnClickListener(new View.OnClickListener() {
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

    public void createPDF(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                PdfHistoria pdfHistoria = new PdfHistoria(getActivity(), "Diagnostico_"+apellido+"_"+nombre);
                pdfHistoria.openDocument();
                pdfHistoria.addMetaData("DIAGNOSTICO MEDICO","");
                pdfHistoria.addTitles("DIAGNOSTICO MEDICO", "Paciente: "+apellido+" "+nombre+"\nEdad: "+edad+" años");

                ArrayList<String[]> tmplista = new ArrayList<>();
                for(int i=0;i<listaHistoria.size();i++) {

                    tmplista.add(new String[]{"Fecha:", "Hora Citada"});
                    tmplista.add(new String[]{"" + listaHistoria.get(i).getFecha(), "" + listaHistoria.get(i).getHora()});
                    tmplista.add(new String[]{"Doctor Asignado:", "Hora Diagnosticada"});
                    tmplista.add(new String[]{"" + listaHistoria.get(i).getDoctor(), "" + listaHistoria.get(i).getHoraDiagnosticada()});
                    tmplista.add(new String[]{"Peso:", "Talla:"});
                    tmplista.add(new String[]{"" + listaHistoria.get(i).getPeso(), "" + listaHistoria.get(i).getTalla()});
                    tmplista.add(new String[]{"Temperatura:", "Presion:"});
                    tmplista.add(new String[]{"" + listaHistoria.get(i).getTemperatura(), "" + listaHistoria.get(i).getPresion()});
                    tmplista.add(new String[]{"DX Ingreso:", "Recomendaciones:"});
                    tmplista.add(new String[]{"" + listaHistoria.get(i).getDiagnostico(), "" + listaHistoria.get(i).getRecomendaciones()});
                    tmplista.add(new String[]{"Laboratorio:","Radiologia"});
                    tmplista.add(new String[]{""+listaHistoria.get(i).getLaboratorio(),""+listaHistoria.get(i).getRadiologia()});

                }
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
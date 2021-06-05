package com.example.historiasclinicas.Vista;

import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.historiasclinicas.Controlador.Metodos;
import com.example.historiasclinicas.Controlador.PacientesAdapter;
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

import lecho.lib.hellocharts.formatter.AxisValueFormatter;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class GestionReporteFragment extends Fragment {

    LinearLayout linearLayout;
    RecyclerView recyclerView;
    PacientesAdapter adapter;
    TextView txtdni;
    LineChartView lineChartView;
    Button btnbuscar;
    ArrayList<Historia> listaHistoria = new ArrayList<>();
    ArrayList<Paciente> listaPacientes = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gestion_reporte, container, false);
        linearLayout = view.findViewById(R.id.linearLayout);
        recyclerView = view.findViewById(R.id.recyclerView);
        lineChartView = view.findViewById(R.id.txtnumconsultas);
        txtdni = view.findViewById(R.id.txtdni);
        btnbuscar = view.findViewById(R.id.btnbuscar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PacientesAdapter(listaPacientes);
        recyclerView.setAdapter(adapter);

        listarHistoria();

        btnbuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listaPacientes.clear();
                Metodos.closeTeclado(getActivity().getCurrentFocus(),getContext());
                for(int i=0;i<MenuDoctorActivity.listaPacientes.size();i++){
                    if(
                            MenuDoctorActivity.listaPacientes.get(i).getId().equals(txtdni.getText().toString())
                    ){
                        listaPacientes.add(MenuDoctorActivity.listaPacientes.get(i));
                        break;
                    }
                }

                if(listaPacientes.size()>0)
                {
                    cargarChart();
                    adapter.actualizar(listaPacientes);
                    linearLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    lineChartView.setVisibility(View.VISIBLE);
                }

            }
        });

        adapter.setOnItemClickListener(new PacientesAdapter.OnItemClickListener() {
            @Override
            public void OnVerClick(int position) {
                ArrayList<Historia> tmp = new ArrayList<>();
                for(int i=0;i<listaHistoria.size();i++){
                    if(listaPacientes.get(0).getDni().equals(listaHistoria.get(i).getCodPaciente())){
                        tmp.add(listaHistoria.get(i));
                    }
                }
                Log.e("lista",listaHistoria.size()+"");
                Metodos.cambiarFragment(getActivity(),
                        new ReporteHistoriaFragment(listaPacientes.get(0).getApellidos(),
                                listaPacientes.get(0).getNombres(),
                                listaPacientes.get(0).getEdad(),
                                tmp),"GestionReporte",null,true);
            }
        });

        return view;
    }

    private void listarHistoria() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Historias");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaHistoria.clear();
                for(DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    Historia h = objSnapshot.getValue(Historia.class);
                    if(h.getAtendido()==1
                            //&& MenuDoctorActivity.nombre.equalsIgnoreCase(h.getDoctor())
                     ){
                        listaHistoria.add(h);
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void cargarChart(){
        List yAxisValues = new ArrayList();
        List axisValues = new ArrayList();
        final Calendar c = Calendar.getInstance();
        int anio = (int) c.get(Calendar.YEAR)%100;

        String[] axisData = new String[] {"Ene","Feb","Mar","Abr","May","Jun",
                "Jul","Ago","Sep","Oct","Nov","Dic"};
        int[] yAxisData = new int[]{0,0,0,0,0,0,0,0,0,0,0,0};

        ArrayList<Historia> tmp = new ArrayList<>();
        for(int i=0;i<listaHistoria.size();i++){
            if(listaPacientes.get(0).getDni().equals(listaHistoria.get(i).getCodPaciente())){
                tmp.add(listaHistoria.get(i));
            }
        }

        for(int i=0; i<tmp.size(); i++){
            String[] array = listaHistoria.get(i).getFecha().split("/");
            int mes = Integer.parseInt(array[1]);
            Log.e("mes",mes+"");
            Log.e("anio",array[2]+" "+anio);
            if(Integer.parseInt(array[2]) == anio){
               // int aux = yAxisData[mes-1];
                yAxisData[mes-1] += 1;
            }
        }

        Line line = new Line(yAxisValues).setColor(Color.parseColor("#9C27B0"));

        for (int i = 0; i < axisData.length; i++) {
            axisValues.add(i, new AxisValue(i).setLabel(axisData[i]));
        }

        for (int i = 0; i < yAxisData.length; i++) {
            yAxisValues.add(new PointValue(i, yAxisData[i]));
        }

        int max =0;
        for(int i=0;i<yAxisData.length;i++){
            if(yAxisData[i]>max){
                max=yAxisData[i];
            }
        }

        List lines = new ArrayList();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        Axis axis = new Axis();
        axis.setValues(axisValues);
        axis.setTextSize(12);
        axis.setTextColor(Color.parseColor("#03A9F4"));
        axis.setName("Meses");
        data.setAxisXBottom(axis);

        ArrayList<AxisValue> values = new ArrayList<>();
        for(float i=0;i<=max+2;i++){
            values.add(new AxisValue(i));
        }

        Axis yAxis = new Axis();
        yAxis.setValues(values);
        yAxis.setTextColor(Color.parseColor("#03A9F4"));
        yAxis.setTextSize(14);
        yAxis.setName("Visitas");
        data.setAxisYLeft(yAxis);


        lineChartView.setLineChartData(data);
        Viewport viewport = new Viewport(lineChartView.getMaximumViewport());
        viewport.top = max+2;
        lineChartView.setMaximumViewport(viewport);
        lineChartView.setCurrentViewport(viewport);

        lineChartView.setOnValueTouchListener(new LineChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int i, int i1, PointValue pointValue) {
                Toast.makeText(getContext(),pointValue.getY()+"",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onValueDeselected() {

            }
        });
    }



}
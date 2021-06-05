package com.example.historiasclinicas.Vista;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.historiasclinicas.Controlador.Metodos;
import com.example.historiasclinicas.Modelo.Historia;
import com.example.historiasclinicas.Modelo.Paciente;
import com.example.historiasclinicas.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class AgendarCitaFragment extends Fragment {

    TextView txtfecha,txthora;
    Spinner spndoctor;
    Button btnagendarcita;
    int dia,mes,anio;
    DatabaseReference databaseReference;
    Paciente paciente;

    public AgendarCitaFragment(Paciente paciente) {
        this.paciente = paciente;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agendar_cita, container, false);
        txtfecha = view.findViewById(R.id.txtfecha);
        txthora = view.findViewById(R.id.txthora);
        spndoctor = view.findViewById(R.id.spndoctor);
        btnagendarcita = view.findViewById(R.id.btnagendar);


        databaseReference = FirebaseDatabase.getInstance().getReference();

        final Calendar c = Calendar.getInstance();
        dia = c.get(Calendar.DAY_OF_MONTH);
        mes = c.get(Calendar.MONTH)+1;
        anio = c.get(Calendar.YEAR);

        if(dia<10 && mes<10){
            txtfecha.setText("0"+dia+"/0"+mes+"/"+(int) anio%100);
        }else if(dia<10 && mes>=10){
            txtfecha.setText("0"+dia+"/"+mes+"/"+(int) anio%100);
        }else if(dia>=10 && mes<10){
            txtfecha.setText(dia+"/0"+mes+"/"+(int) anio%100);
        }else{
            txtfecha.setText(dia+"/"+mes+"/"+(int) anio%100);
        }

        final int hora = c.get(Calendar.HOUR_OF_DAY);
        final int minuto = c.get(Calendar.MINUTE);
        if(minuto<10){
            txthora.setText(hora+":0"+minuto);
        }else {
            txthora.setText(hora+":"+minuto);
        }

        txthora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog tm = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        if(i1<10){
                            txthora.setText(i+":0"+i1);
                        }else {
                            txthora.setText(i+":"+i1);
                        }
                    }
                },hora,minuto,true);
                tm.show();
            }
        });

        spndoctor.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, MenuActivity.doctoresList));
        txtfecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                        txtfecha.setText(dia+"/"+mes+"/"+((int) year%100));
                    }
                },anio,mes-1,dia);
                datePickerDialog.show();
            }
        });

        btnagendarcita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!txthora.getText().toString().isEmpty() &&
                    !txtfecha.getText().toString().isEmpty()){
                    double randC = Math.random()*1000;
                    String cuenta = Math.round(randC)+"";
                    String keyFB = databaseReference.child("Historias").push().getKey();
                    Historia historia = new Historia();
                    historia.setKey(keyFB);
                    historia.setIdAtencion(paciente.getIdAtencion());
                    historia.setCuenta(cuenta);
                    historia.setCodPaciente(paciente.getDni());
                    historia.setFecha(txtfecha.getText().toString());
                    historia.setHora(txthora.getText().toString());
                    historia.setDoctor(spndoctor.getSelectedItem().toString());
                    historia.setEdad(paciente.getEdad());
                    historia.setPeso(paciente.getPeso());
                    historia.setTemperatura(paciente.getTemperatura());
                    historia.setPresion(paciente.getPresion());
                    historia.setTalla(paciente.getTalla());
                    databaseReference.child("Historias").child(keyFB).setValue(historia);
                    databaseReference.child("Pacientes").child(historia.getCodPaciente()).child("historias").child(keyFB).setValue(historia);

                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setCancelable(false);
                    dialog.setMessage("CITA REGISTRADA CON EXITO")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialoginterface, int i) {
                                    getActivity().getSupportFragmentManager().popBackStack();
                                }
                            }).show();
                }
            }
        });

        return view;
    }



}
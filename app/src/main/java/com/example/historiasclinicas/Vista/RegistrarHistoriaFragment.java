package com.example.historiasclinicas.Vista;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.historiasclinicas.Controlador.Metodos;
import com.example.historiasclinicas.Modelo.Historia;
import com.example.historiasclinicas.Modelo.Paciente;
import com.example.historiasclinicas.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class RegistrarHistoriaFragment extends Fragment {

    String codigoPaciente = "";
    String nombrePaciente = "";
    String apellidoPaciente = "";
    TextView txtfecha, txthora,txtPaciente, tempAlert, presionAlert;
    EditText txtpeso,txttalla,txtpresion,txttemperatura;
    Spinner spndoctor;
    ConstraintLayout constraintLayout;
    Button btnactualizar;
    DatabaseReference databaseReference;
    Paciente paciente;
    int dia,mes,anio,hora,min;
    boolean nn=false;


    public RegistrarHistoriaFragment(Paciente paciente, boolean nn) {
        // Required empty public constructor
        this.paciente = paciente;
        this.codigoPaciente = paciente.getId();
        this.nombrePaciente = paciente.getNombres();
        this.apellidoPaciente = paciente.getApellidos();
        this.nn = nn;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registrar_historia, container, false);

        constraintLayout = view.findViewById(R.id.constraintLayout);
        txtPaciente = view.findViewById(R.id.txtPaciente);
        txtfecha = view.findViewById(R.id.txtfecha);
        txthora = view.findViewById(R.id.txthora);
        spndoctor = view.findViewById(R.id.spndoctor);
        txtpeso = view.findViewById(R.id.txtpeso);
        txttalla = view.findViewById(R.id.txttalla);
        txtpresion = view.findViewById(R.id.txtpresion);
        txttemperatura = view.findViewById(R.id.txttemperatura);
        btnactualizar = view.findViewById(R.id.btnactualizar);
        tempAlert = view.findViewById(R.id.temperaturaAlert);
        presionAlert = view.findViewById(R.id.presionAlert);

        spndoctor.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, MenuActivity.doctoresList));

        txtPaciente.setText("Paciente: "+apellidoPaciente+" "+nombrePaciente);

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

        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Metodos.closeTeclado(getActivity().getCurrentFocus(),getContext());
            }
        });

        btnactualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Metodos.closeTeclado(getActivity().getCurrentFocus(),getContext());
                if(nn){
                    if(
                        !txtfecha.getText().toString().isEmpty() &&
                        !txtpeso.getText().toString().isEmpty() &&
                        !txtpresion.getText().toString().isEmpty() &&
                        !txttalla.getText().toString().isEmpty() &&
                        !txttemperatura.getText().toString().isEmpty() &&
                        !txthora.getText().toString().isEmpty()
                    ){
                        registrarHistoria();
                    }else{
                        Snackbar snackbar = Snackbar
                                .make(constraintLayout, "Complete los campos", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }else{
                    if(
                        !txtfecha.getText().toString().isEmpty() &&
                        !txtpeso.getText().toString().isEmpty() &&
                        !txtpresion.getText().toString().isEmpty() &&
                        !txttalla.getText().toString().isEmpty() &&
                        !txttemperatura.getText().toString().isEmpty()&&
                        !txthora.getText().toString().isEmpty()
                    ){
                        registrarHistoria();
                    }else{
                        Snackbar snackbar = Snackbar
                                .make(constraintLayout, "Complete los campos", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }

            }
        });

        txttemperatura.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try{
                    if(editable.toString().length()>0){
                        tempAlert.setVisibility(View.VISIBLE);
                        int number = Integer.parseInt(editable.toString());
                        if(number<36 || number>41){
                            tempAlert.setText("*Temperatura fuera de rango");
                        }else{
                            tempAlert.setText("");
                        }
                    }else {
                        tempAlert.setVisibility(View.GONE);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        txtpresion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try{
                    if(editable.toString().length()>0){
                        presionAlert.setVisibility(View.VISIBLE);
                        int number = Integer.parseInt(editable.toString());
                        if(number<90 || number>120){
                            presionAlert.setText("*Presion fuera de rango");
                        }else{
                            presionAlert.setText("");
                        }
                    }else {
                        presionAlert.setVisibility(View.GONE);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        return view;
    }



    private void registrarHistoria(){
        Historia historia = new Historia();

        double rand = Math.random()*100000;
        double randC = Math.random()*1000;
        String key = "A"+Math.round(rand)+"";
        String keyFB = databaseReference.child("Historias").push().getKey();
        String fecha = txtfecha.getText().toString();
        String hora = txthora.getText().toString();
        String cuenta = Math.round(randC)+"";
        double peso = Double.parseDouble(txtpeso.getText().toString());
        double presion = Double.parseDouble(txtpresion.getText().toString());
        double talla = Double.parseDouble(txttalla.getText().toString());
        double temperatura = Double.parseDouble(txttemperatura.getText().toString());
        int edad = 0;
        if(!nn){
            String[] fecCumple = paciente.getFechaNacimiento().split("/");
            String[] fechaActual = fecha.split("/");
            int fecC = Integer.parseInt(fecCumple[2].substring(2)+""+fecCumple[1]+""+fecCumple[0]);
            int fecA = Integer.parseInt(fechaActual[2]+""+fechaActual[1]+""+fechaActual[0]);
            if(fecA>=fecC){
                edad = anio - Integer.parseInt(fecCumple[2]) ;
                historia.setEdad(edad);
            }else{
                edad = anio - Integer.parseInt(fecCumple[2]) -1;
                historia.setEdad(edad);
            }

        }
        historia.setIdAtencion(key);
        historia.setKey(keyFB);
        historia.setFecha(fecha);
        historia.setCuenta(cuenta);
        historia.setCodPaciente(codigoPaciente);
        historia.setDoctor(spndoctor.getSelectedItem().toString());
        historia.setPeso(peso);
        historia.setPresion(presion);
        historia.setTalla(talla);
        historia.setTemperatura(temperatura);
        historia.setAtendido(0);
        historia.setApellidoPaciente(apellidoPaciente);
        historia.setNombrePaciente(nombrePaciente);
        historia.setHora(hora);

        paciente.setIdAtencion(key);

        if(!nn) {
            paciente.setEdad(edad);
        }
        paciente.setPeso(peso);
        paciente.setPresion(presion);
        paciente.setTalla(talla);
        paciente.setTemperatura(temperatura);

            databaseReference.child("Pacientes").child(historia.getCodPaciente()).setValue(paciente);
            databaseReference.child("Historias").child(keyFB).setValue(historia);
            databaseReference.child("Pacientes").child(historia.getCodPaciente()).child("historias").child(keyFB).setValue(historia);

            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setCancelable(false);
            dialog.setMessage("HISTORIA ACTUALIZADA CON EXITO")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            Metodos.limpiarStack(getActivity().getSupportFragmentManager());
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame,new BuscarPacienteFragment()).commit();
                        }
                    }).show();
        //}




    }


}
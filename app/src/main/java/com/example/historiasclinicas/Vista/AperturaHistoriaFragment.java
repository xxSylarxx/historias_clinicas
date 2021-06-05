package com.example.historiasclinicas.Vista;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.historiasclinicas.Controlador.Metodos;
import com.example.historiasclinicas.Modelo.Historia;
import com.example.historiasclinicas.Modelo.Paciente;
import com.example.historiasclinicas.Modelo.User;
import com.example.historiasclinicas.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class AperturaHistoriaFragment extends Fragment {

    EditText txtnombres, txtapellidos, txtcelular, txtdni, txtdireccion, txtpadre, txtmadre, txtfechanacimiento, txtobservaciones;
    ConstraintLayout constraintNN,constraintLayout;
    RadioButton rbhombre, rbmujer;
    Button btnregistrar;
    String sexo,dniPaciente;
    CheckBox cbnn;
    DatabaseReference databaseReference;
    ArrayList<Paciente> listaPacientes = new ArrayList<>();

    public AperturaHistoriaFragment(String dni) {
        this.dniPaciente = dni;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        databaseReference = FirebaseDatabase.getInstance().getReference("Pacientes");
        new Thread(new Runnable() {
            @Override
            public void run() {
                listarFBPacientes();
            }
        }).start();

        txtdni.setText(dniPaciente);

        cbnn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cbnn.isChecked()){
                    constraintNN.setVisibility(View.GONE);
                }else{
                    constraintNN.setVisibility(View.VISIBLE);
                }
            }
        });

        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Metodos.closeTeclado(getActivity().getCurrentFocus(),getContext());
            }
        });

        txtfechanacimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int dia,mes,anio;
                final Calendar c = Calendar.getInstance();
                dia = c.get(Calendar.DAY_OF_MONTH);
                mes = c.get(Calendar.MONTH)+1;
                anio = c.get(Calendar.YEAR);

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
                        txtfechanacimiento.setText(dia+"/"+mes+"/"+year);
                    }
                },anio,mes-1,dia);
                datePickerDialog.show();
            }
        });

        rbhombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sexo = "Masculino";
            }
        });

        rbmujer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sexo = "Femenino";
            }
        });

        btnregistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Metodos.closeTeclado(getActivity().getCurrentFocus(),getContext());
                registrarNuevoUsuario();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_apertura_historia, container, false);
        constraintNN = view.findViewById(R.id.constraintNN);
        constraintLayout = view.findViewById(R.id.constraintLayout);
        cbnn = view.findViewById(R.id.cbnn);
        txtnombres = view.findViewById(R.id.txtnombres);
        txtapellidos = view.findViewById(R.id.txtapellidos);
        txtcelular = view.findViewById(R.id.txtcelular);
        txtobservaciones = view.findViewById(R.id.txtobservaciones);
        txtdni = view.findViewById(R.id.txtdni);
        txtdireccion = view.findViewById(R.id.txtdireccion);
        txtpadre = view.findViewById(R.id.txtpadre);
        txtmadre = view.findViewById(R.id.txtmadre);
        txtfechanacimiento = view.findViewById(R.id.txtfechaNacimiento);
        rbhombre = view.findViewById(R.id.rbhombre);
        rbmujer = view.findViewById(R.id.rbmujer);
        btnregistrar = view.findViewById(R.id.btnregistrar);

        return view;
    }


    private void registrarNuevoUsuario(){

        double randNN = Math.random()*10000000;
        String keyNN = "N"+Math.round(randNN);

        if(
                cbnn.isChecked() &&
                        !sexo.isEmpty() &&
                        !txtobservaciones.getText().toString().isEmpty()
        ){
            final Paciente paciente = new Paciente();
            paciente.setId(keyNN);
            paciente.setNombres("NN");
            paciente.setApellidos("NN");
            paciente.setDni(keyNN);
            paciente.setSexo(sexo);
            paciente.setObservaciones(txtobservaciones.getText().toString());

            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setCancelable(false);
            dialog.setMessage("REGISTRADO NN CON EXITO")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            RegistrarHistoriaFragment registrarHistoriaFragment = new RegistrarHistoriaFragment(paciente,true);
                            Metodos.cambiarFragment(getActivity(),registrarHistoriaFragment,"AperturaHistoria",null,true);
                        }
                    }).show();


        }else if(
                !cbnn.isChecked() &&
                !txtfechanacimiento.getText().toString().isEmpty() &&
                !txtcelular.getText().toString().isEmpty() &&
                !txtdni.getText().toString().isEmpty() &&
                !txtnombres.getText().toString().isEmpty() &&
                !txtdireccion.getText().toString().isEmpty() &&
                !sexo.isEmpty()
                && txtcelular.getText().toString().length() == 9
                && txtdni.getText().toString().length() == 8
        ){
            final Paciente paciente = new Paciente();
            paciente.setId(txtdni.getText().toString());
            paciente.setFechaNacimiento(txtfechanacimiento.getText().toString());
            if(txtcelular.getText().toString().length() == 9){
                paciente.setCelular(txtcelular.getText().toString());
            }
            paciente.setDni(txtdni.getText().toString());
            paciente.setDireccion(txtdireccion.getText().toString());
            paciente.setNombres(txtnombres.getText().toString());
            paciente.setApellidos(txtapellidos.getText().toString());
            paciente.setNombreMadre(txtmadre.getText().toString());
            paciente.setNombrePadre(txtpadre.getText().toString());
            paciente.setSexo(sexo);
            paciente.setObservaciones(txtobservaciones.getText().toString());

            if(existeDNI(txtdni.getText().toString())){
                Toast.makeText(getContext(), "DNI YA EXISTE", Toast.LENGTH_SHORT).show();
            }else {

                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setCancelable(false);
                dialog.setMessage("PACIENTE REGISTRADO, POR FAVOR APERTURAR HISTORIA")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                RegistrarHistoriaFragment registrarHistoriaFragment = new RegistrarHistoriaFragment(paciente,false);
                                Metodos.cambiarFragment(getActivity(),registrarHistoriaFragment,"AperturaHistoria",null,true);
                            }
                        }).show();
            }
        }


    }

    public boolean existeDNI(String dni){
        for (int i=0;i<listaPacientes.size();i++){
            if(dni.equals(listaPacientes.get(i).getDni())){
                //Log.e("dni","dni existe");
                return true;
            }
        }
        //Log.e("dni","dni no existe");
        return false;
    }

    private void listarFBPacientes() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaPacientes.clear();

                for(DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    Paciente p = objSnapshot.getValue(Paciente.class);
                    listaPacientes.add(p);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
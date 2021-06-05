package com.example.historiasclinicas.Vista;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.historiasclinicas.Controlador.Metodos;
import com.example.historiasclinicas.Modelo.Historia;
import com.example.historiasclinicas.Modelo.Paciente;
import com.example.historiasclinicas.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class DiagnosticoFragment extends Fragment {

    Historia historia;
    String nombreCompleto;
    Paciente paciente;
    TextView txtpaciente,txtedad,txtfecha, lab, radio, tempAlert, presionAlert;
    EditText txtpresion,txttalla,txttemperatura,txtpeso;
    EditText txtdiagnostico,txtrecomendaciones;
    Spinner spnlaboratorio, spnelegido, spnradiologia, spnelegidoradio;
    ImageView foto;
    Button btnfinalizar;
    ProgressDialog progressDialog;
    List<Historia> listaExtra;

    private static final int GALLERY_INTENT = 1;
    StorageReference myStorage;
    String downUrl="";
    ProgressDialog pd;

    public DiagnosticoFragment(Historia historia, Paciente paciente, List<Historia> listaExtra) {
        this.historia = historia;
        this.paciente = paciente;
        this.nombreCompleto = paciente.getApellidos()+" "+paciente.getNombres();
        this.listaExtra = listaExtra;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diagnostico, container, false);
        txtpaciente = view.findViewById(R.id.txtpaciente);
        lab = view.findViewById(R.id.lab);
        txtedad = view.findViewById(R.id.txtedad);
        txtfecha = view.findViewById(R.id.txtfecha);
        txtpresion = view.findViewById(R.id.txtpresion);
        txttalla = view.findViewById(R.id.txttalla);
        txtpeso = view.findViewById(R.id.txtpeso);
        txttemperatura = view.findViewById(R.id.txttemperatura);
        txtdiagnostico = view.findViewById(R.id.txtdiagnostico);
        txtrecomendaciones = view.findViewById(R.id.txtrecomendaciones);
        spnlaboratorio = view.findViewById(R.id.spnlaboratorio);
        spnelegido = view.findViewById(R.id.spnelegido);
        spnradiologia = view.findViewById(R.id.spnareRadio);
        spnelegidoradio = view.findViewById(R.id.spnelegidoRadio);
        foto = view.findViewById(R.id.txtrayos);
        btnfinalizar = view.findViewById(R.id.btnfinalizar);
        radio = view.findViewById(R.id.radio);
        tempAlert = view.findViewById(R.id.tempAlert);
        presionAlert = view.findViewById(R.id.presionAlert);

        final ImageView historial = view.findViewById(R.id.historial);
        historial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Historia> tmp = new ArrayList<>();
                for(int i=0;i<listaExtra.size();i++){
                    if(listaExtra.get(i).getAtendido()==1 &&
                        listaExtra.get(i).getIdAtencion().equals(historia.getIdAtencion())){
                        tmp.add(listaExtra.get(i));
                    }
                }
                Metodos.cambiarFragment(getActivity(),
                        new ReporteHistoriaFragment(paciente.getApellidos(),paciente.getNombres(),historia.getEdad(),tmp),"Pacientes",null,true);
            }
        });

        myStorage = FirebaseStorage.getInstance().getReference();
        pd = new ProgressDialog(getContext());
        pd.setMessage("Cargando");
        pd.setCancelable(false);

        Calendar c = Calendar.getInstance();
        int dia = c.get(Calendar.DAY_OF_MONTH);
        int mes = c.get(Calendar.MONTH)+1;
        int anio = c.get(Calendar.YEAR);

        if(dia<10 && mes<10){
            txtfecha.setText("0"+dia+"/0"+mes+"/"+(int) anio%100);
        }else if(dia<10 && mes>=10){
            txtfecha.setText("0"+dia+"/"+mes+"/"+(int) anio%100);
        }else if(dia>=10 && mes<10){
            txtfecha.setText(dia+"/0"+mes+"/"+(int) anio%100);
        }else{
            txtfecha.setText(dia+"/"+mes+"/"+(int) anio%100);
        }

        txtedad.setText(historia.getEdad()+"");
        txtpaciente.setText(nombreCompleto);
        txtpresion.setText(historia.getPresion()+"");
        txtpeso.setText(historia.getPeso()+"");
        txttalla.setText(historia.getTalla()+"");
        txttemperatura.setText(historia.getTemperatura()+"");

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Cargando");
        progressDialog.setCancelable(false);

        spnlaboratorio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    spnelegido.setVisibility(View.GONE);
                    lab.setVisibility(View.GONE);
                }else{
                    progressDialog.show();
                    final int index = i;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final List<String> lista = Metodos.api(index);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(lista.size()>0){
                                        Log.e("size",lista.size()+"");
                                        spnelegido.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,lista));
                                        spnelegido.setVisibility(View.VISIBLE);
                                        lab.setVisibility(View.VISIBLE);
                                    }
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }).start();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        spnradiologia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    spnelegidoradio.setVisibility(View.GONE);
                    radio.setVisibility(View.GONE);
                }else{
                    progressDialog.show();
                    final int index = i;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final List<String> lista = Metodos.apiRadio(index);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("size",lista.size()+"");
                                    if (lista.size()>0){
                                        spnelegidoradio.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,lista));
                                        spnelegidoradio.setVisibility(View.VISIBLE);
                                        radio.setVisibility(View.VISIBLE);
                                    }
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }).start();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_INTENT);
            }
        });

        btnfinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Metodos.closeTeclado(getActivity().getCurrentFocus(),getContext());
                if(
                        !txtrecomendaciones.getText().toString().isEmpty() &&
                        !txtdiagnostico.getText().toString().isEmpty() &&
                        !txtpeso.getText().toString().isEmpty() &&
                        !txtpresion.getText().toString().isEmpty() &&
                        !txttemperatura.getText().toString().isEmpty() &&
                        !txttalla.getText().toString().isEmpty()
                ){
                    historia.setAtendido(1);
                    historia.setPeso(Double.parseDouble(txtpeso.getText().toString()));
                    historia.setPresion(Double.parseDouble(txtpresion.getText().toString()));
                    historia.setTemperatura(Double.parseDouble(txttemperatura.getText().toString()));
                    historia.setTalla(Double.parseDouble(txttalla.getText().toString()));
                    historia.setDiagnostico(txtdiagnostico.getText().toString());
                    historia.setRecomendaciones(txtrecomendaciones.getText().toString());
                    historia.setRayosx(downUrl);

                    if(spnlaboratorio.getSelectedItemPosition()>0){
                        historia.setLaboratorio(spnlaboratorio.getSelectedItem().toString()+"\n"+spnelegido.getSelectedItem().toString());
                    }else{
                        historia.setLaboratorio("Ninguno");
                    }

                    if(spnradiologia.getSelectedItemPosition()>0){
                        historia.setRadiologia(spnradiologia.getSelectedItem().toString()+"\n"+spnelegidoradio.getSelectedItem().toString());
                    }else{
                        historia.setRadiologia("Ninguno");
                    }


                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setCancelable(false);
                    dialog.setTitle("Alert");
                    dialog.setMessage("¿DESEA FINALIZAR LA ATENCION?")
                            .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialoginterface, int i) {
                                    final Calendar c = Calendar.getInstance();
                                    final int hora = c.get(Calendar.HOUR_OF_DAY);
                                    final int minuto = c.get(Calendar.MINUTE);
                                    historia.setHoraDiagnosticada(hora+":"+minuto);

                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("");
                                    databaseReference.child("Historias").child(historia.getKey()).setValue(historia);
                                    databaseReference.child("Pacientes").child(historia.getCodPaciente())
                                            .child("historias").child(historia.getKey()).setValue(historia);
                                    Metodos.popFragment(getActivity());
                                }
                            })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();
                }else{
                    Toast.makeText(getActivity(), "Complete los campos", Toast.LENGTH_SHORT).show();
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
            pd.show();
            final Uri uri = data.getData();

            final StorageReference filePath = myStorage.child(uri.getLastPathSegment());

            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downUrl = uri.toString();
                            pd.dismiss();
                        }
                    });
                }
            });

            Picasso.with(getContext())
                    .load(uri)
                    .into(foto);

        }

    }


}
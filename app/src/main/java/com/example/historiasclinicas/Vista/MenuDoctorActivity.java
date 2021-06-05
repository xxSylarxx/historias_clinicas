package com.example.historiasclinicas.Vista;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.historiasclinicas.Controlador.Metodos;
import com.example.historiasclinicas.Modelo.Paciente;
import com.example.historiasclinicas.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MenuDoctorActivity extends AppCompatActivity {

    ImageView ivmenu;
    TextView miperfil, pacientes, gestionReporte, cerrarsesion, txtnombre, eficiencia;
    DrawerLayout menuLateral;
    String email="";
    public static String nombre="";
    int tipo;
    public static ArrayList<Paciente> listaPacientes = new ArrayList<>();

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_doctor);

        menuLateral = findViewById(R.id.drawer);
        txtnombre = findViewById(R.id.txtcargoNombre);
        ivmenu = findViewById(R.id.ivmenu);
        miperfil = findViewById(R.id.miperfil);
        pacientes = findViewById(R.id.pacientes);
        gestionReporte = findViewById(R.id.gestionReporte);
        cerrarsesion = findViewById(R.id.cerrarsesion);
        eficiencia = findViewById(R.id.eficiencia);

        if(getIntent().hasExtra("email") && getIntent().hasExtra("nombre") && getIntent().hasExtra("tipo") ){
            email = getIntent().getStringExtra("email");
            nombre = getIntent().getStringExtra("nombre");
            tipo = getIntent().getIntExtra("tipo",0);
        }

        if(tipo==1){
            txtnombre.setText(nombre);
        }else{
            txtnombre.setText("Admision: "+nombre);
        }


        getSupportFragmentManager().beginTransaction().replace(R.id.frame, new PerfilFragment(nombre,tipo)).commit();
        listarPacientes();

        ivmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuLateral.openDrawer(Gravity.LEFT);
            }
        });

        miperfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuLateral.closeDrawer(GravityCompat.START);
                Metodos.closeTeclado(getCurrentFocus(),MenuDoctorActivity.this);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Metodos.limpiarStack(getSupportFragmentManager());
                        Metodos.cambiarFragment(MenuDoctorActivity.this, new PerfilFragment(nombre,tipo),null,null,false);
                    }
                }).start();

            }
        });
        pacientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuLateral.closeDrawer(GravityCompat.START);
                Metodos.closeTeclado(getCurrentFocus(),MenuDoctorActivity.this);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Metodos.limpiarStack(getSupportFragmentManager());
                        Metodos.cambiarFragment(MenuDoctorActivity.this, new PacientesFragment(nombre),null,null,false);
                    }
                }).start();

            }
        });

        eficiencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuLateral.closeDrawer(GravityCompat.START);
                Metodos.closeTeclado(getCurrentFocus(),MenuDoctorActivity.this);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Metodos.limpiarStack(getSupportFragmentManager());
                        Metodos.cambiarFragment(MenuDoctorActivity.this, new EficienciaFragment(),null,null,false);
                    }
                }).start();

            }
        });

        gestionReporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuLateral.closeDrawer(GravityCompat.START);
                Metodos.closeTeclado(getCurrentFocus(),MenuDoctorActivity.this);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Metodos.limpiarStack(getSupportFragmentManager());
                        Metodos.cambiarFragment(MenuDoctorActivity.this, new GestionReporteFragment(),null,null,false);
                    }
                }).start();

            }
        });
        cerrarsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MenuDoctorActivity.this);
                dialog.setTitle("CERRAR SESION")
                        .setMessage("¿Esta seguro de terminar su sesión?")
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                dialoginterface.cancel();
                            }
                        })
                        .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                Intent intent = new Intent(MenuDoctorActivity.this,LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }).show();
            }
        });
    }

    public void listarPacientes(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Pacientes");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaPacientes.clear();
                for(DataSnapshot obj: dataSnapshot.getChildren()){
                    listaPacientes.add(obj.getValue(Paciente.class));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        FragmentManager manager = getSupportFragmentManager();
        int backStackSize = manager.getBackStackEntryCount();
        if(backStackSize==0) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MenuDoctorActivity.this);
            dialog.setTitle("CERRAR SESION")
                    .setMessage("¿Esta seguro de terminar su sesión?")
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            dialoginterface.cancel();
                        }
                    })
                    .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            Intent intent = new Intent(MenuDoctorActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }).show();
        }
        else{
            manager.popBackStack();
        }
    }


}
package com.example.historiasclinicas.Vista;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.historiasclinicas.Controlador.Metodos;
import com.example.historiasclinicas.R;
import com.example.historiasclinicas.Modelo.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    ImageView ivmenu;
    TextView miperfil, buscarpaciente, pacientenuevo, cerrarsesion, txtnombre;
    DrawerLayout menuLateral;
    public static List<String> doctoresList = new ArrayList<>();
    String email="";
    String nombre="";
    int tipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mAuth=FirebaseAuth.getInstance();

        menuLateral = findViewById(R.id.drawer);
        txtnombre = findViewById(R.id.txtcargoNombre);
        ivmenu = findViewById(R.id.ivmenu);
        miperfil = findViewById(R.id.miperfil);
        buscarpaciente = findViewById(R.id.pacientes);
        pacientenuevo = findViewById(R.id.gestionReporte);
        cerrarsesion = findViewById(R.id.cerrarsesion);

        databaseReference = FirebaseDatabase.getInstance().getReference();

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

        listarFBDoctores();

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

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Metodos.limpiarStack(getSupportFragmentManager());
                        Metodos.cambiarFragment(MenuActivity.this, new PerfilFragment(nombre,tipo),null,null,false);
                    }
                }).start();

                //getSupportFragmentManager().beginTransaction().replace(R.id.frame, new PerfilFragment()).commit();
            }
        });
        buscarpaciente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuLateral.closeDrawer(GravityCompat.START);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Metodos.limpiarStack(getSupportFragmentManager());
                        Metodos.cambiarFragment(MenuActivity.this, new BuscarPacienteFragment(),null,null,false);
                    }
                }).start();

                //getSupportFragmentManager().beginTransaction().replace(R.id.frame, new BuscarPacienteFragment()).commit();
            }
        });
        pacientenuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuLateral.closeDrawer(GravityCompat.START);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Metodos.limpiarStack(getSupportFragmentManager());
                        Metodos.cambiarFragment(MenuActivity.this, new AperturaHistoriaFragment(""),null,null,false);
                    }
                }).start();

            }
        });
        cerrarsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MenuActivity.this);
                dialog.setTitle("CERRAR SESION")
                    .setMessage("¿Esta seguro de terminar su sesión?")
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            dialoginterface.cancel();
                        }
                    })
                    .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            Intent intent = new Intent(MenuActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }).show();
            }
        });

    }

    private void listarFBDoctores(){
        databaseReference.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                doctoresList.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    User d = snapshot.getValue(User.class);
                    if(d.getTipo()==1){
                        doctoresList.add(d.getNombre());
                    }
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
            AlertDialog.Builder dialog = new AlertDialog.Builder(MenuActivity.this);
            dialog.setTitle("CERRAR SESION")
                    .setMessage("¿Esta seguro de terminar su sesión?")
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            dialoginterface.cancel();
                        }
                    })
                    .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }).show();
        }
        else{
            manager.popBackStack();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }
}
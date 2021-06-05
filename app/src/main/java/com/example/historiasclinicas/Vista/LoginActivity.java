package com.example.historiasclinicas.Vista;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.historiasclinicas.Controlador.Metodos;
import com.example.historiasclinicas.Modelo.User;
import com.example.historiasclinicas.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mfirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    private EditText usuario;
    private EditText password;
    private ConstraintLayout constraintLayout;
    private Button sesion;
    private User usuarioExtra;
    private String email = "";
    private String contra = "";
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    List<User> listaUsuarios = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usuario = (EditText) findViewById(R.id.txtUsuario);
        password = (EditText) findViewById(R.id.txtPassword);
        sesion = (Button) findViewById(R.id.btnInicioSesion);
        constraintLayout = findViewById(R.id.constraintLayout);


        mfirebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("User");
        getInformacion();

        progressDialog = new ProgressDialog(this);

        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Metodos.closeTeclado(LoginActivity.this.getCurrentFocus(), LoginActivity.this);
            }
        });

        sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = usuario.getText().toString().trim();
                contra = password.getText().toString().trim();
                Log.e("SIZE",listaUsuarios.size()+"");
                if (!email.isEmpty() && !contra.isEmpty() && listaUsuarios.size()>0) {
                    progressDialog.setMessage("Cargando");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    LoginUser();
                } else {
                    Snackbar snackbar = Snackbar
                            .make(constraintLayout, "Complete los campos", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });

    }

    public void LoginUser() {
        mfirebaseAuth.signInWithEmailAndPassword(email, contra).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                boolean paso = false;
                if (task.isSuccessful()) {
                    for(int i=0; i<listaUsuarios.size(); i++){
                        if(listaUsuarios.get(i).getEmail().equals(email)){
                            usuarioExtra = listaUsuarios.get(i);
                            paso = true;
                        }
                    }

                    progressDialog.dismiss();
                    if(paso){
                        if(usuarioExtra.getTipo()==1){
                            Intent intent = new Intent(LoginActivity.this, MenuDoctorActivity.class);
                            intent.putExtra("email", usuarioExtra.getEmail());
                            intent.putExtra("tipo", usuarioExtra.getTipo());
                            intent.putExtra("nombre", usuarioExtra.getNombre());
                            startActivity(intent);
                            finish();
                        }else{
                            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                            intent.putExtra("email", usuarioExtra.getEmail());
                            intent.putExtra("tipo", usuarioExtra.getTipo());
                            intent.putExtra("nombre", usuarioExtra.getApellidos()+", "+usuarioExtra.getNombre());
                            startActivity(intent);
                            finish();
                        }
                    }
                } else {
                    progressDialog.dismiss();
                    Snackbar snackbar = Snackbar
                            .make(constraintLayout, "No se puede iniciar Sesion, compruebe los datos", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
    }

    private void getInformacion(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaUsuarios.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    final User user = snapshot.getValue(User.class);
                    listaUsuarios.add(user);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



};

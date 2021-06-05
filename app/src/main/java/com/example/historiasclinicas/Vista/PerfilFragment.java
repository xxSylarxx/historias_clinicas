package com.example.historiasclinicas.Vista;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.historiasclinicas.R;

public class PerfilFragment extends Fragment {

    String nombre;
    int tipo;

    public PerfilFragment(String nombre, int tipo) {
        this.nombre = nombre;
        this.tipo = tipo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        TextView txtnombre = view.findViewById(R.id.txtnombre);
        TextView txtcargo = view.findViewById(R.id.txtcargo);
        Button btncerrarSesion = view.findViewById(R.id.btncerrarSesion);
        txtnombre.setText(nombre);
        if(tipo==1){
            txtcargo.setText("Doctor(a)");
        }

        btncerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("CERRAR SESION")
                        .setMessage("¿Esta seguro de terminar su sesión?")
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                dialoginterface.cancel();
                            }
                        })
                        .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        }).show();
            }
        });

        return view;
    }
}
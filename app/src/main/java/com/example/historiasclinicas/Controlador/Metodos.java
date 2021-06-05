package com.example.historiasclinicas.Controlador;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.historiasclinicas.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Metodos {

    public static void cambiarFragment(FragmentActivity fa, Fragment fragment, String tag, String stack, boolean isStack){
        FragmentManager fm = fa.getSupportFragmentManager();
        if(isStack){
            fm.beginTransaction().replace(R.id.frame,fragment,tag).addToBackStack(stack).commit();
        }else{
            fm.beginTransaction().replace(R.id.frame,fragment).commit();
        }
    }

    public static void popFragment(FragmentActivity fa){
        fa.getSupportFragmentManager().popBackStack();
    }

    public static void limpiarStack(FragmentManager manager){
        int backStackSize = manager.getBackStackEntryCount();
        for (int i= 0; i<backStackSize;i++) {
            manager.popBackStack();
        }
    }

    public static void closeTeclado(View currentFocus, Context context){
        View view = currentFocus;
        if(view != null){
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    public static List<String> api(int opcionLista){
        List<String> lista = new ArrayList<>();
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("http://demo0672626.mockable.io/laboratorio")
                .method("GET", null)
                .build();
        try {
            Response response = client.newCall(request).execute();
            JSONObject json = new JSONObject(response.body().string());
            JSONArray array = json.getJSONArray(opcionLista+"");
            for(int i=0; i<array.length(); i++){
                lista.add(array.getString(i));
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public static List<String> apiRadio(int opcionLista){
        List<String> lista = new ArrayList<>();
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("http://demo0672626.mockable.io/radiografia")
                .method("GET", null)
                .build();
        try {
            Response response = client.newCall(request).execute();
            JSONObject json = new JSONObject(response.body().string());
            JSONArray array = json.getJSONArray(opcionLista+"");
            for(int i=0; i<array.length(); i++){
                lista.add(array.getString(i));
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return lista;
    }


}

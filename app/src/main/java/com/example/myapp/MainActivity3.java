package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity3 extends AppCompatActivity {
    EditText etMsgEdit;
    Button bMod, bCancel;
    SharedPreferences sesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        etMsgEdit = findViewById(R.id.etMsgEdit);
        bMod = findViewById(R.id.bMod);
        bCancel = findViewById(R.id.bCancel);

        sesion = getSharedPreferences("sesion", 0);

        consultar();

        bMod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardar();
            }
        });
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity3.this,MainActivity2.class));
            }
        });
    }

    private void consultar() {
        String id = sesion.getString("idedit","error");
        String url = Uri.parse(config.url + "/msgs.php")
                .buildUpon()
                .appendQueryParameter("id", id)
                .build().toString();
        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //response trae todos los campos de la tabla (idmsg,user,msg,fecha)
                        try {
                            etMsgEdit.setText(response.getString("msg"));
                        }catch (Exception e){}
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity3.this, "Error en la red", Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<>();
                headers.put("Authorization", sesion.getString("token","Error"));
                return headers;
            }
        };
        RequestQueue cola = Volley.newRequestQueue(this);
        cola.add(peticion);
    }

    private void guardar() {
        String id = sesion.getString("idedit","error");
        String url = Uri.parse(config.url + "/msgs.php")
                .buildUpon()
                .appendQueryParameter("id", id)
                .appendQueryParameter("msg", etMsgEdit.getText().toString())
                .build().toString();
        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.PUT, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //response trae modificado:si o modificado:no como un objeto JSON
                        try {
                            if(response.getString("modificado").compareTo("si")==0){
                                Toast.makeText(MainActivity3.this, "Mensaje moodificado", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity3.this, MainActivity2.class));
                            }else{
                                Toast.makeText(MainActivity3.this, "Error no se pudo guardar", Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){}
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity3.this, "Error en la red", Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<>();
                headers.put("Authorization", sesion.getString("token","Error"));
                return headers;
            }
        };
        RequestQueue cola = Volley.newRequestQueue(this);
        cola.add(peticion);
    }
}
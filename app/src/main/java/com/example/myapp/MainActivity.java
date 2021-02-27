package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    EditText txtUsuario, txtPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtUsuario = findViewById(R.id.txtUsuario);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    private void login() {
        String login = Uri.parse(config.url + "/login.php")
                .buildUpon()
                .appendQueryParameter("user", txtUsuario.getText().toString())
                .appendQueryParameter("pass", txtPassword.getText().toString())
                .build().toString();

        JsonObjectRequest peticion = new JsonObjectRequest(
                Request.Method.GET,
                login,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            if(response.getString("entro").compareTo("si") == 0){
                                Toast.makeText(MainActivity.this, "Bienvendio", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(MainActivity.this, MainActivity2.class);
                                startActivity(i);
                            }else{
                                Toast.makeText(MainActivity.this, "Error de usurio/conraseña", Toast.LENGTH_SHORT).show();
                            }
                        }catch(Exception e ){
                            Toast.makeText(MainActivity.this, "No se recibió respuesta", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error en la red", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        RequestQueue cola = Volley.newRequestQueue(this);
        cola.add(peticion);
    }
}
package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity {

    EditText txtMsg;
    Button btnPost, btnRefresh;
    ListView lsMsg;
    SharedPreferences sesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        txtMsg = findViewById(R.id.txtMsg);
        btnPost = findViewById(R.id.btnPost);
        btnRefresh = findViewById(R.id.btnRefresh);
        lsMsg = findViewById(R.id.lsMsg);

        sesion = getSharedPreferences("sesion", 0);

        
        llenar();

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviar();
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llenar();
            }
        });

    }

    private void enviar() {
    }

    private void llenar() {
        String url = Uri.parse(config.url + "/msgs.php")
                .buildUpon()
                .build().toString();
        JsonArrayRequest peticion = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            ArrayList<String> lista = new ArrayList<>();

                            for (int i = 0; i < response.length(); i++) {
                                lista.add(response.getJSONObject(i).getString("idmsg") + ":" +
                                        response.getJSONObject(i).getString("user") + ":" +
                                        response.getJSONObject(i).getString("fecha") + ":" +
                                        response.getJSONObject(i).getString("msg"));
                            }

                            lsMsg.setAdapter(new ArrayAdapter<String>(MainActivity2.this, android.R.layout.simple_list_item_1));

                        }catch (Exception e){
                            Toast.makeText(MainActivity2.this, "No se recibió respuesta", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity2.this, "Error en la red", Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError{
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", sesion.getString("token", "Error"));
                return headers;
            }
        };

        RequestQueue cola = Volley.newRequestQueue(this);
        cola.add(peticion);

    }
}
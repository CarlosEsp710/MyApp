package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.KeyEventDispatcher;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity {
    EditText etMsg;
    Button bPost, bRefresh;
    ListView lvMsg;
    SharedPreferences sesion;

    ArrayList<String> lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        etMsg = findViewById(R.id.etMsg);
        bPost = findViewById(R.id.bPost);
        bRefresh = findViewById(R.id.bRefresh);
        lvMsg = findViewById(R.id.lvMsg);

        sesion = getSharedPreferences("sesion",0);

        llenar();

        bPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviar();
            }
        });

        View.OnClickListener x = new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                enviar();
            }
        };

        bRefresh.setOnClickListener(x);

        lvMsg.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String tmp[] = lista.get(i).split("::");
                borrar(tmp[0]);
                return false;
            }
        });
        lvMsg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String tmp[] = lista.get(i).split("::");
                SharedPreferences.Editor editor = sesion.edit();
                editor.putString("idedit",tmp[0]);
                editor.commit();
                startActivity(new Intent(MainActivity2.this,MainActivity3.class));
            }
        });
    }

    private void borrar(String id) {
        String url = Uri.parse(config.url + "/msgs.php")
                .buildUpon()
                .appendQueryParameter("id",id)
                .build().toString();
        StringRequest peticion = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(MainActivity2.this, response, Toast.LENGTH_SHORT).show();
                        llenar();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity2.this, "Error de servidor", Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError{
                Map<String,String> headers = new HashMap<>();
                headers.put("Authorization", sesion.getString("token","Error"));
                return headers;
            }
        };
        RequestQueue cola = Volley.newRequestQueue(this);
        cola.add(peticion);

    }

    private void enviar() {
        String url = Uri.parse(config.url + "/msgs.php")
                .buildUpon()
                .build().toString();
        StringRequest peticion = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //response trae agregado=si o agregado=no  como una cadena
                        Toast.makeText(MainActivity2.this, response, Toast.LENGTH_SHORT).show();
                        llenar();
                        etMsg.setText("");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity2.this, "Error de servidor", Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError{
                Map<String,String> headers = new HashMap<>();
                headers.put("Authorization", sesion.getString("token","Error"));
                return headers;
            }

            @Override
            //Enviar datos de post
            public Map<String,String> getParams(){
                Map<String,String> datos = new HashMap<>();
                datos.put("msg", etMsg.getText().toString());
                return datos;
            }
        };
        RequestQueue cola = Volley.newRequestQueue(this);
        cola.add(peticion);
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
                            lista = new ArrayList<>();

                            for (int i = 0; i < response.length(); i++) {
                                lista.add(response.getJSONObject(i).getString("idmsg") + "::" +
                                        response.getJSONObject(i).getString("user") + "::" +
                                        response.getJSONObject(i).getString("fecha") + "::" +
                                        response.getJSONObject(i).getString("msg"));
                            }
                            lvMsg.setAdapter(new ArrayAdapter<>(MainActivity2.this, android.R.layout.simple_list_item_1,lista));
                        }catch(Exception e){
                            Toast.makeText(MainActivity2.this, "No se recibio datos validos", Toast.LENGTH_SHORT).show();
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
            public Map<String,String> getHeaders() throws AuthFailureError{
                Map<String,String> headers = new HashMap<>();
                headers.put("Authorization", sesion.getString("token","Error"));
                return headers;
            }
        };
        RequestQueue cola = Volley.newRequestQueue(this);
        cola.add(peticion);
    }
}
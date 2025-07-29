package com.example.proyectosemestral;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

public class Registrarse extends AppCompatActivity {

    EditText editTextNombre, editTextCorreo, editTextContraseña, editTextConfirmarContraseña,editTextApellido;
    Button btnRegistrarse;
    TextView linkTextVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrarse);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referencias a los campos
        editTextNombre = findViewById(R.id.editTextName);
        editTextCorreo = findViewById(R.id.editTextCorreo);
        editTextContraseña = findViewById(R.id.editTextContraseña);
        editTextConfirmarContraseña = findViewById(R.id.editTextConfirmar);
        btnRegistrarse = findViewById(R.id.btnRegistrarse);
        linkTextVolver = findViewById(R.id.linkText2);
        editTextApellido = findViewById(R.id.editTextApellido);

        // Acción del botón Registrar
        btnRegistrarse.setOnClickListener(view -> {
            String nombre = editTextNombre.getText().toString().trim();
            String apellido = editTextApellido.getText().toString().trim();
            String correo = editTextCorreo.getText().toString().trim().toLowerCase();
            String contraseña = editTextContraseña.getText().toString().trim();
            String confirmarContraseña = editTextConfirmarContraseña.getText().toString().trim();

            if (nombre.isEmpty() || correo.isEmpty() || contraseña.isEmpty() || confirmarContraseña.isEmpty() || apellido.isEmpty()) {
                Toast.makeText(Registrarse.this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!contraseña.equals(confirmarContraseña)) {
                Toast.makeText(Registrarse.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            // Aquí luego se puede hacer la petición al API para registrar al admin
            String url = "https://camino-cruces-backend-production.up.railway.app/api/registro/";

            RequestQueue queue = Volley.newRequestQueue(Registrarse.this);

            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("email", correo);
                jsonBody.put("nombre", nombre);
                jsonBody.put("apellido", apellido);
                jsonBody.put("contraseña", contraseña);
                jsonBody.put("rol", "user"); // Puedes omitirlo si ya tienes valor por defecto
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                    response -> {
                        Toast.makeText(Registrarse.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Registrarse.this, IniciarSesion.class);
                        startActivity(intent);
                        finish();
                    },
                    error -> {
                        String msg = "Error al registrar";
                        if (error.networkResponse != null && error.networkResponse.statusCode == 400) {
                            msg = "Correo ya registrado o datos inválidos";
                        }
                        Toast.makeText(Registrarse.this, msg, Toast.LENGTH_LONG).show();
                    }
            );
            queue.add(request);
        });

        // Acción del link “Ya tengo una cuenta”
        linkTextVolver.setOnClickListener(view -> {
            Intent intent = new Intent(Registrarse.this, IniciarSesion.class);
            startActivity(intent);
            finish();
        });
    }
}

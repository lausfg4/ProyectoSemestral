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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class IniciarSesion extends AppCompatActivity {

    Button btnIngresar;
    TextView linkText3;
    EditText etCorreo, etContrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_iniciar_sesion);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referencias a los elementos
        btnIngresar = findViewById(R.id.btnRegistrarse2);
        linkText3 = findViewById(R.id.linkText3);
        etCorreo = findViewById(R.id.editTextCorreo2);         // Asegúrate que estos IDs existan en tu XML
        etContrasena = findViewById(R.id.editTextContraseña2); // Asegúrate que estos IDs existan en tu XML

        // Acción al pulsar "Ingresar"
        btnIngresar.setOnClickListener(view -> {
            String correo = etCorreo.getText().toString().trim();
            String contrasena = etContrasena.getText().toString().trim();

            if (correo.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            String url = "https://camino-cruces-backend-production.up.railway.app/api/login/";

            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("email", correo);
                jsonBody.put("contraseña", contrasena);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al preparar datos", Toast.LENGTH_SHORT).show();
                return;
            }

            RequestQueue queue = Volley.newRequestQueue(this);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                    response -> {
                        try {
                            String token = response.getString("token");
                            String nombre = response.getString("nombre");
                            String apellido = response.getString("apellido");
                            String rol = response.getString("rol");
                            int id = response.getInt("id");

                            Toast.makeText(this, "Bienvenido " + nombre, Toast.LENGTH_SHORT).show();

                            // Redirigir a Dashboard (Admin o User según rol)
                            Intent intent;
                            if (rol.equalsIgnoreCase("ADMIN")) {
                                intent = new Intent(this, DashboardActivity.class);
                            } else {
                                intent = new Intent(this, Inicio.class);
                            }

                            intent.putExtra("token", token);
                            intent.putExtra("usuario_id", id);
                            intent.putExtra("rol", rol);
                            startActivity(intent);
                            finish();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Error al procesar respuesta", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        String msg = "Error al iniciar sesión";
                        if (error.networkResponse != null && error.networkResponse.statusCode == 400) {
                            msg = "Correo o contraseña incorrecta";
                        }
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                    });

            queue.add(request);
        });

        // Acción al pulsar "Crea una"
        linkText3.setOnClickListener(view -> {
            Intent intent = new Intent(this, Registrarse.class);
            startActivity(intent);
        });
    }
}

package com.example.proyectosemestral;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SenderoComentarios extends AppCompatActivity {

    private static final String TAG = "SenderoComentarios";

    private int senderoId;
    private String senderoNombre;
    private String usuarioId;

    private TextView nombreSenderoView;
    private RatingBar ratingBar;
    private EditText editComentario;
    private Button btnComentar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sendero_comentarios);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Obtener datos del Intent
        senderoId = getIntent().getIntExtra("sendero_id", -1);
        senderoNombre = getIntent().getStringExtra("sendero_nombre");

        // Obtener usuario_id del Intent
        usuarioId = getIntent().getStringExtra("usuario_id");

        // Si no viene en el Intent, obtener de SharedPreferences
        if (usuarioId == null || usuarioId.isEmpty()) {
            SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
            usuarioId = prefs.getString("usuario_id", null);
        }

        // Fallback si no se encuentra usuario_id
        if (usuarioId == null || usuarioId.isEmpty()) {
            usuarioId = "1";
            Log.w(TAG, "⚠️ No se encontró usuario_id, usando fallback: " + usuarioId);
        }

        Log.d(TAG, "Usuario ID obtenido: " + usuarioId);
        Log.d(TAG, "Sendero ID: " + senderoId + ", Nombre: " + senderoNombre);

        // Inicializar vistas
        nombreSenderoView = findViewById(R.id.nombre_sendero);
        ratingBar = findViewById(R.id.ratingBar);
        editComentario = findViewById(R.id.edit_comentario);
        btnComentar = findViewById(R.id.btn_comentar);

        nombreSenderoView.setText(senderoNombre);

        // Validar que tenemos los datos necesarios
        if (senderoId == -1) {
            Toast.makeText(this, "❌ Error: ID de sendero no válido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (usuarioId == null || usuarioId.isEmpty()) {
            Toast.makeText(this, "❌ Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnComentar.setOnClickListener(v -> enviarComentario());
    }

    private void enviarComentario() {
        float valoracion = ratingBar.getRating();
        String comentario = editComentario.getText().toString().trim();

        // Validaciones
        if (comentario.isEmpty()) {
            Toast.makeText(this, "⚠️ Escribe un comentario", Toast.LENGTH_SHORT).show();
            return;
        }

        if (valoracion == 0) {
            Toast.makeText(this, "⚠️ Califica el sendero (1-5 estrellas)", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Enviando comentario:");
        Log.d(TAG, "Usuario ID: " + usuarioId);
        Log.d(TAG, "Sendero ID: " + senderoId);
        Log.d(TAG, "Valoración: " + valoracion);
        Log.d(TAG, "Comentario: " + comentario);

        String url = "https://camino-cruces-backend-production.up.railway.app/api/comentarios/agregar/";

        // Crear JSON para el comentario
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("usuario_id", usuarioId);
            jsonBody.put("sendero", senderoId);
            jsonBody.put("comentario", comentario);
            jsonBody.put("valoracion", valoracion);

            Log.d(TAG, "JSON enviado: " + jsonBody.toString());
        } catch (Exception e) {
            Log.e(TAG, "Error creando JSON: " + e.getMessage());
            Toast.makeText(this, "❌ Error al preparar datos", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                response -> {
                    Log.d(TAG, "✅ Comentario enviado exitosamente: " + response.toString());
                    Toast.makeText(this, "✅ Comentario enviado", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> {
                    String mensaje = "❌ Error al enviar comentario";
                    Log.e(TAG, "Error en request: " + error.toString());

                    if (error.networkResponse != null) {
                        Log.e(TAG, "Status Code: " + error.networkResponse.statusCode);
                        if (error.networkResponse.data != null) {
                            String errorBody = new String(error.networkResponse.data);
                            Log.e(TAG, "Error Body: " + errorBody);
                            mensaje += "\nServidor: " + errorBody;
                        }
                    }
                    Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        queue.add(request);
    }


}
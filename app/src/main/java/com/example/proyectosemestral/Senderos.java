package com.example.proyectosemestral;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class Senderos extends AppCompatActivity {

    private LinearLayout senderosContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_senderos);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        senderosContainer = findViewById(R.id.senderos_container);

        cargarSenderosDesdeAPI();

        ImageButton btnSalir = findViewById(R.id.btn_salir);
        btnSalir.setOnClickListener(v -> mostrarDialogoSalir());

        ImageView InicioBtn = findViewById(R.id.btn_incio);
        InicioBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Senderos.this, Inicio.class);
            startActivity(intent);
        });
    }

    private void cargarSenderosDesdeAPI() {
        String url = "https://camino-cruces-backend-production.up.railway.app/api/senderos/";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                this::mostrarSenderos,
                error -> Toast.makeText(this, "❌ Error al cargar senderos", Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }

    private void mostrarSenderos(JSONArray response) {
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject sendero = response.getJSONObject(i);
                int id = sendero.getInt("id");
                String nombre = sendero.getString("nombre_sendero");
                String distancia = sendero.getString("distancia");
                String dificultad = sendero.getString("dificultad");
                String descripcion = "Distancia: " + distancia + " km\nDificultad: " + dificultad;


                View card = inflater.inflate(R.layout.sendero_card, senderosContainer, false);

                ImageView imageView = card.findViewById(R.id.sendero_image);
                TextView nombreView = card.findViewById(R.id.sendero_nombre);
                TextView detallesView = card.findViewById(R.id.sendero_detalles);
                RatingBar ratingBar = card.findViewById(R.id.sendero_rating);
                TextView resenasView = card.findViewById(R.id.sendero_resenas);
                TextView verMasView = card.findViewById(R.id.sendero_ver_mas);

                // Asignar imagen local según nombre
                int imagenRes = obtenerImagenPorNombre(nombre);
                imageView.setImageResource(imagenRes);

                nombreView.setText(nombre);
                detallesView.setText(descripcion);
                ratingBar.setRating(0f);
                resenasView.setText(""); // opcional

                verMasView.setOnClickListener(v -> {
                    Intent intent = null;
                    String nombreLower = nombre.toLowerCase();

                    if (nombreLower.contains("camarón") || nombreLower.contains("camaron")) {
                        intent = new Intent(Senderos.this, senderocamaron.class);
                    } else if (nombreLower.contains("cruces")) {
                        intent = new Intent(Senderos.this, senderoCaminoDeCruces.class);
                    } else if (nombreLower.contains("pescador")) {
                        intent = new Intent(Senderos.this, senderoElPescador.class);
                    } else if (nombreLower.contains("buho") || nombreLower.contains("búho")) {
                        intent = new Intent(Senderos.this, senderoBuhoDeAnteojos.class);
                    } else if (nombreLower.contains("ciclovia") || nombreLower.contains("ciclovía")) {
                        intent = new Intent(Senderos.this, senderoCiclovia.class);
                    }

                    if (intent != null) {
                        intent.putExtra("sendero_id", id);
                        intent.putExtra("sendero_nombre", nombre);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "No se pudo encontrar la pantalla del sendero", Toast.LENGTH_SHORT).show();
                    }
                });



                senderosContainer.addView(card);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Devuelve imagen local según el nombre del sendero
    private int obtenerImagenPorNombre(String nombre) {
        nombre = nombre.toLowerCase();
        if (nombre.contains("camaron")) return R.drawable.senderoscamaron;
        if (nombre.contains("cruces")) return R.drawable.senderoscaminocruces;
        if (nombre.contains("ciclovia")) return R.drawable.senderosciclovia;
        if (nombre.contains("pescador")) return R.drawable.senderoselpescador;
        if (nombre.contains("buho")) return R.drawable.senderosbuhodeanteojos;
        return R.drawable.logo; // imagen por defecto
    }

    private void mostrarDialogoSalir() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_salir, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        ImageView btnCerrar = dialogView.findViewById(R.id.btn_cerrar);
        btnCerrar.setOnClickListener(view -> alertDialog.dismiss());

        Button btnConfirmarSalir = dialogView.findViewById(R.id.btn_salir);
        btnConfirmarSalir.setOnClickListener(view -> {
            alertDialog.dismiss();
            finishAffinity();
        });
    }
}

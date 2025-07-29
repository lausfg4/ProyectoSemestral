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

import java.util.Locale;

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

                // Inicializar con valores por defecto mientras se cargan las valoraciones
                ratingBar.setRating(0f);
                resenasView.setText("Cargando...");

                // CARGAR VALORACIONES DEL SENDERO
                cargarValoracionesSendero(id, ratingBar, resenasView);

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

    // NUEVO MÉTODO: Cargar valoraciones de un sendero específico
    private void cargarValoracionesSendero(int senderoId, RatingBar ratingBar, TextView resenasView) {
        String url = "https://camino-cruces-backend-production.up.railway.app/api/comentarios/sendero/" + senderoId + "/";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        double sumaValoracion = 0;
                        int cantidadResenas = response.length();

                        // Calcular promedio de valoraciones
                        for (int i = 0; i < cantidadResenas; i++) {
                            JSONObject comentario = response.getJSONObject(i);
                            double valoracion = comentario.getDouble("valoracion");
                            sumaValoracion += valoracion;
                        }

                        if (cantidadResenas > 0) {
                            double promedioValoracion = sumaValoracion / cantidadResenas;

                            // Actualizar RatingBar con el promedio
                            ratingBar.setRating((float) promedioValoracion);

                            // Actualizar texto de reseñas
                            resenasView.setText(String.format(Locale.US, "%.1f (%d reseñas)",
                                    promedioValoracion, cantidadResenas));
                        } else {
                            // Sin reseñas
                            ratingBar.setRating(0f);
                            resenasView.setText("Sin reseñas");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        // En caso de error, mostrar valores por defecto
                        ratingBar.setRating(0f);
                        resenasView.setText("Error al cargar");
                    }
                },
                error -> {
                    // En caso de error de red
                    ratingBar.setRating(0f);
                    resenasView.setText("Sin conexión");
                }
        );

        queue.add(request);
    }

    // Devuelve imagen local según el nombre del sendero
    private int obtenerImagenPorNombre(String nombre) {
        String nombreLower = nombre.toLowerCase();

        // Debug: Ver qué nombre está recibiendo
        android.util.Log.d("Senderos", "Buscando imagen para: '" + nombre + "' (lower: '" + nombreLower + "')");

        // Búsquedas más específicas y con más variaciones
        if (nombreLower.contains("camarón") || nombreLower.contains("camaron")) {
            android.util.Log.d("Senderos", "Usando imagen: senderoscamaron");
            return R.drawable.senderoscamaron;
        }

        if (nombreLower.contains("cruces") || nombreLower.contains("camino de cruces")) {
            android.util.Log.d("Senderos", "Usando imagen: senderoscaminocruces");
            return R.drawable.senderoscaminocruces;
        }

        if (nombreLower.contains("ciclovía") || nombreLower.contains("ciclovia")) {
            android.util.Log.d("Senderos", "Usando imagen: senderosciclovia");
            return R.drawable.senderosciclovia;
        }

        if (nombreLower.contains("pescador") || nombreLower.contains("el pescador")) {
            android.util.Log.d("Senderos", "Usando imagen: senderoselpescador");
            return R.drawable.senderoselpescador;
        }

        if (nombreLower.contains("búho") || nombreLower.contains("buho") ||
                nombreLower.contains("anteojos") || nombreLower.contains("búho de anteojos")) {
            android.util.Log.d("Senderos", "Usando imagen: senderosbuhodeanteojos");
            return R.drawable.senderosbuhodeanteojos;
        }

        // Si no encuentra ninguna coincidencia
        android.util.Log.w("Senderos", "No se encontró imagen específica para: " + nombre + " - usando logo por defecto");
        return R.drawable.logo;
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
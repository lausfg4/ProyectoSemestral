package com.example.proyectosemestral;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;

public class senderocamaron extends AppCompatActivity {

    private Button btnComentar;
    private int usuarioId; // ✅ Se inicializa después con SharedPreferences


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senderocamaron);

        // ✅ Obtener el ID del usuario desde sesión
        SharedPreferences preferences = getSharedPreferences("sesion_usuario", MODE_PRIVATE);
        usuarioId = preferences.getInt("usuario_id", -1);

        if (usuarioId == -1) {
            Toast.makeText(this, "❌ Sesión no iniciada. Vuelve a iniciar sesión.", Toast.LENGTH_LONG).show();
            finish(); // Cierra si no hay usuario
            return;
        }

        btnComentar = findViewById(R.id.btn_comentar);
        btnComentar.setOnClickListener(v -> {
            Intent intent = new Intent(this, SenderoComentarios.class);
            intent.putExtra("sendero_id", 1); // ID del sendero Camarón
            intent.putExtra("sendero_nombre", "Sendero Camarón");
            intent.putExtra("usuario_id", usuarioId); // ✅ Usuario real (NO uses RESULT_OK)
            startActivity(intent);
        });

        obtenerResenas();
    }

    private void obtenerResenas() {
        int senderoId = 1; // O usa getIntent si llega por otro Intent
        String url = "https://camino-cruces-backend-production.up.railway.app/api/comentarios/sendero/" + senderoId + "/";

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    LinearLayout contenedor = findViewById(R.id.contenedor_resenas);
                    contenedor.removeAllViews();

                    double sumaValoracion = 0;
                    int cantidadResenas = response.length();

                    for (int i = 0; i < cantidadResenas; i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            String comentario = obj.getString("comentario");
                            double valoracion = obj.getDouble("valoracion");
                            String fotoUrl = obj.optString("foto_comentario", "");
                            String nombreUsuario = obj.optString("usuario", "Anónimo");

                            View tarjeta = getLayoutInflater().inflate(R.layout.item_resena, null);

                            TextView txtComentario = tarjeta.findViewById(R.id.txt_comentario);
                            TextView txtNombreUsuario = tarjeta.findViewById(R.id.txt_nombre_usuario);
                            RatingBar rating = tarjeta.findViewById(R.id.rating_usuario);
                            ImageView imgFoto = tarjeta.findViewById(R.id.img_foto_resena);

                            txtComentario.setText(comentario);
                            txtNombreUsuario.setText(nombreUsuario);
                            rating.setRating((float) valoracion);

                            if (!fotoUrl.isEmpty()) {
                                Picasso.get().load(fotoUrl).into(imgFoto);
                            }

                            contenedor.addView(tarjeta);

                            sumaValoracion += valoracion;

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    // Mostrar promedio y cantidad
                    TextView promedio = findViewById(R.id.txt_valoracion_general);
                    if (cantidadResenas > 0) {
                        double promedioValoracion = sumaValoracion / cantidadResenas;
                        promedio.setText(String.format(Locale.US, "%.1f (%d reseñas)", promedioValoracion, cantidadResenas));
                    } else {
                        promedio.setText("Sin reseñas");
                    }

                },
                error -> Toast.makeText(this, "❌ Error al cargar reseñas", Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }
}

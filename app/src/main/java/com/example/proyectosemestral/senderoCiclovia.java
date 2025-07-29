package com.example.proyectosemestral;

import android.content.Intent;
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

public class senderoCiclovia extends AppCompatActivity {

    private Button btnComentar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendero_ciclovia);


        btnComentar = findViewById(R.id.btn_comentar);
        btnComentar.setOnClickListener(v -> {
            Intent intent = new Intent(this, SenderoComentarios.class);
            intent.putExtra("sendero_id", 5); //
            intent.putExtra("sendero_nombre", "Sendero Ciclovía");
            startActivity(intent);

        });


        obtenerResenas();
    }
    @Override
    protected void onResume() {
        super.onResume();
        obtenerResenas(); // Recarga comentarios cada vez que regresas
    }
    private void obtenerResenas() {
        int senderoId = getIntent().getIntExtra("sendero_id", -1);
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

                            // AGREGAR ESTA LÍNEA - Sumar la valoración al total
                            sumaValoracion += valoracion;

                            View tarjeta = getLayoutInflater().inflate(R.layout.item_resena, null);

                            TextView txtComentario = tarjeta.findViewById(R.id.txt_comentario);
                            TextView txtNombreUsuario = tarjeta.findViewById(R.id.txt_nombre_usuario);
                            RatingBar rating = tarjeta.findViewById(R.id.rating_usuario);


                            txtComentario.setText(comentario);
                            txtNombreUsuario.setText(nombreUsuario);
                            rating.setRating((float) valoracion);



                            contenedor.addView(tarjeta);

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

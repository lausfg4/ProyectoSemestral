package com.example.proyectosemestral;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Esta actividad representa la pantalla de la encuesta.
 * Contiene un scroll automático de imágenes y un botón para regresar.
 */
public class EncuestaActivity extends AppCompatActivity {

    private HorizontalScrollView scrollImagenes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encuesta2);

        Button btncomenzar = findViewById(R.id.btnComenzar);
        btncomenzar.setOnClickListener(v -> {
            EditText etIdentificacion = findViewById(R.id.etIdentificacion);
            String identificacion = etIdentificacion.getText().toString().trim();

            if (identificacion.isEmpty()) {
                mostrarAlerta("¡Ingrese su identificación!", R.drawable.exclamation, "Volver", null);
                return;
            }

            String url = "https://camino-cruces-backend-production.up.railway.app/api/registrar-visita/";

            org.json.JSONObject jsonBody = new org.json.JSONObject();
            try {
                jsonBody.put("cedula_pasaporte", identificacion);
                jsonBody.put("razon_visita", "Encuesta");
                jsonBody.put("sendero_visitado", "No especificado");
            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta("¡Error al preparar los datos!", R.drawable.exclamation, "Volver", null);
                return;
            }

            com.android.volley.RequestQueue queue = com.android.volley.toolbox.Volley.newRequestQueue(this);

            com.android.volley.toolbox.JsonObjectRequest request = new com.android.volley.toolbox.JsonObjectRequest(
                    com.android.volley.Request.Method.POST,
                    url,
                    jsonBody,
                    response -> {
                        try {
                            int idVisita = response.getInt("id_visita"); // Extraer ID de la visita

                            mostrarAlerta("¡Visita registrada con éxito!", R.drawable.ic_check, "Continuar", () -> {
                                Intent intent = new Intent(this, Encuesta.class);
                                intent.putExtra("id_visita", idVisita); // Pasar ID al siguiente Activity
                                startActivity(intent);
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            mostrarAlerta("Error al procesar la respuesta del servidor", R.drawable.exclamation, "Cerrar", null);
                        }
                    },

                    error -> {
                        String msg = "Error al registrar visita";
                        if (error.networkResponse != null && error.networkResponse.statusCode == 400) {
                            msg = "Cédula no registrada o datos inválidos";
                        }
                        mostrarAlerta(msg, R.drawable.exclamation, "Volver", null);
                    }
            );

            queue.add(request);
        });


        // Botón de regresar
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> startActivity(new Intent(this, DashboardActivity.class)));

        // Scroll de imágenes horizontal automático
        scrollImagenes = findViewById(R.id.scrollImagenes);
        iniciarScrollAutomatico();

        // *** Opción futura: recibir número de identificación si se necesita ***
        String identificacion = getIntent().getStringExtra("identificacion");
        if (identificacion != null) {
            // Puedes usarla más adelante, por ahora solo la dejamos recibida
            // Ejemplo: Log.d("Encuesta", "ID recibida: " + identificacion);
        }
    }

    /**
     * Método para crear un desplazamiento automático lateral
     * en el contenedor de imágenes.
     */
    private void iniciarScrollAutomatico() {
        final Handler handler = new Handler();
        final int step = 5;   // Pixeles por paso
        final int delay = 20; // Milisegundos entre pasos

        Runnable runnable = new Runnable() {
            int posicion = 0;

            @Override
            public void run() {
                scrollImagenes.smoothScrollBy(step, 0);
                posicion += step;

                int maxScroll = scrollImagenes.getChildAt(0).getWidth() - scrollImagenes.getWidth();
                if (posicion >= maxScroll) {
                    scrollImagenes.smoothScrollTo(0, 0);
                    posicion = 0;
                }

                handler.postDelayed(this, delay);
            }
        };

        handler.postDelayed(runnable, delay);
    }

    private void mostrarAlerta(String mensaje, int icono, String textoBoton, Runnable onClick) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.alert_custom, null);

        TextView alertMessage = view.findViewById(R.id.alert_message);
        ImageView alertIcon = view.findViewById(R.id.alert_icon);
        Button alertButton = view.findViewById(R.id.alert_button);

        alertMessage.setText(mensaje);
        alertIcon.setImageResource(icono);
        alertButton.setText(textoBoton);

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

        alertButton.setOnClickListener(v -> {
            dialog.dismiss();
            if (onClick != null) onClick.run();
        });


    }

}
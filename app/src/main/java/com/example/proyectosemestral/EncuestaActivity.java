package com.example.proyectosemestral;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.android.volley.AuthFailureError;

import org.json.JSONObject;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Esta actividad representa la pantalla de la encuesta.
 * Contiene un scroll automático de imágenes y un botón para regresar.
 */
public class EncuestaActivity extends AppCompatActivity {

    private static final String TAG = "EncuestaActivity";
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

            Log.d(TAG, "Buscando visitante con cédula: " + identificacion);

            // Verificar que el visitante existe y obtener sus datos completos
            String urlConsultaVisitante = "https://camino-cruces-backend-production.up.railway.app/api/visitante/cedula/" + identificacion + "/";

            RequestQueue queue = Volley.newRequestQueue(this);

            JsonObjectRequest consultaVisitanteRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    urlConsultaVisitante,
                    null,
                    response -> {
                        try {
                            Log.d(TAG, "Visitante encontrado: " + response.toString());

                            // Validar que la respuesta contiene los campos necesarios
                            if (!response.has("id") || !response.has("cedula_pasaporte") || !response.has("nombre_visitante")) {
                                Log.e(TAG, "La respuesta no contiene campos requeridos");
                                mostrarAlerta("Error: datos del visitante incompletos", R.drawable.exclamation, "Cerrar", null);
                                return;
                            }

                            int idVisitante = response.getInt("id");
                            String cedulaPasaporte = response.getString("cedula_pasaporte");
                            String nombreVisitante = response.getString("nombre_visitante");
                            String nacionalidad = response.optString("nacionalidad", "No especificada");
                            String adultoNino = response.optString("adulto_nino", "No especificado");
                            String telefono = response.optString("telefono", "No especificado");
                            String genero = response.optString("genero", "No especificado");

                            Log.d(TAG, "Datos del visitante - ID: " + idVisitante + ", Nombre: " + nombreVisitante);

                            // Validar que el ID sea válido
                            if (idVisitante <= 0) {
                                Log.e(TAG, "ID de visitante inválido: " + idVisitante);
                                mostrarAlerta("Error: ID de visitante inválido", R.drawable.exclamation, "Cerrar", null);
                                return;
                            }

                            // Ir directamente a la encuesta con los datos del visitante
                            mostrarAlerta("¡Bienvenido " + nombreVisitante + "! Puedes comenzar la encuesta",
                                    R.drawable.ic_check, "Comenzar Encuesta", () -> {
                                        irAEncuesta(idVisitante, idVisitante, cedulaPasaporte, nombreVisitante,
                                                nacionalidad, adultoNino, telefono, genero);
                                    });

                        } catch (Exception e) {
                            Log.e(TAG, "Error procesando respuesta del visitante: " + e.getMessage());
                            e.printStackTrace();
                            mostrarAlerta("Error procesando datos del visitante", R.drawable.exclamation, "Cerrar", null);
                        }
                    },
                    error -> {
                        Log.e(TAG, "Error consultando visitante: " + error.toString());

                        String mensajeError = "¡No estás registrado!";

                        if (error.networkResponse != null) {
                            int statusCode = error.networkResponse.statusCode;
                            Log.e(TAG, "Código de error HTTP: " + statusCode);

                            switch (statusCode) {
                                case 404:
                                    mensajeError = "Visitante no encontrado. Verifica tu cédula.";
                                    break;
                                case 500:
                                    mensajeError = "Error del servidor. Intenta más tarde.";
                                    break;
                                case 400:
                                    mensajeError = "Cédula inválida. Verifica el formato.";
                                    break;
                                default:
                                    mensajeError = "Error " + statusCode + ". Contacta al administrador.";
                            }

                            if (error.networkResponse.data != null) {
                                String errorBody = new String(error.networkResponse.data);
                                Log.e(TAG, "Cuerpo del error: " + errorBody);
                            }
                        } else {
                            Log.e(TAG, "Error de red o timeout");
                            mensajeError = "Error de conexión. Verifica tu internet.";
                        }

                        mostrarAlerta(mensajeError, R.drawable.exclamation, "Volver", null);
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("Accept", "application/json");
                    return headers;
                }
            };

            // Configurar timeout
            consultaVisitanteRequest.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                    15000, // 15 segundos timeout
                    1, // 1 retry
                    com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(consultaVisitanteRequest);
        });

        // Botón de regresar
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Log.d(TAG, "Regresando al Dashboard");
            startActivity(new Intent(this, DashboardActivity.class));
        });

        // Scroll de imágenes horizontal automático
        scrollImagenes = findViewById(R.id.scrollImagenes);
        iniciarScrollAutomatico();

        // Recibir identificación si viene de otra actividad
        String identificacion = getIntent().getStringExtra("identificacion");
        if (identificacion != null) {
            Log.d(TAG, "Identificación recibida desde actividad anterior: " + identificacion);
            EditText etIdentificacion = findViewById(R.id.etIdentificacion);
            if (etIdentificacion != null) {
                etIdentificacion.setText(identificacion);
            }
        }
    }

    /**
     * Navega directamente a la encuesta con los datos del visitante
     */
    private void irAEncuesta(int idVisita, int idVisitante, String cedulaPasaporte, String nombreVisitante,
                             String nacionalidad, String adultoNino, String telefono, String genero) {

        Intent intent = new Intent(this, Encuesta.class);

        // Pasar todos los datos del visitante a la encuesta
        intent.putExtra("id_visita", idVisita);
        intent.putExtra("id_visitante", idVisitante);
        intent.putExtra("cedula_pasaporte", cedulaPasaporte);
        intent.putExtra("nombre_visitante", nombreVisitante);
        intent.putExtra("nacionalidad", nacionalidad);
        intent.putExtra("adulto_nino", adultoNino);
        intent.putExtra("telefono", telefono);
        intent.putExtra("genero", genero);
        intent.putExtra("identificacion", cedulaPasaporte); // Para compatibilidad

        Log.d(TAG, "Navegando a Encuesta - ID Visita: " + idVisita +
                ", ID Visitante: " + idVisitante + ", Nombre: " + nombreVisitante);

        startActivity(intent);
    }

    /**
     * Obtener fecha actual en formato yyyy-MM-dd
     */
    private String obtenerFechaActual() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    /**
     * Obtener hora actual en formato HH:mm:ss
     */
    private String obtenerHoraActual() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    /**
     * Método para crear un desplazamiento automático lateral
     * en el contenedor de imágenes.
     */
    private void iniciarScrollAutomatico() {
        if (scrollImagenes == null) {
            Log.w(TAG, "scrollImagenes es null, no se puede iniciar scroll automático");
            return;
        }

        final Handler handler = new Handler();
        final int step = 5;   // Pixeles por paso
        final int delay = 20; // Milisegundos entre pasos

        Runnable runnable = new Runnable() {
            int posicion = 0;

            @Override
            public void run() {
                if (scrollImagenes != null && scrollImagenes.getChildCount() > 0) {
                    scrollImagenes.smoothScrollBy(step, 0);
                    posicion += step;

                    int maxScroll = scrollImagenes.getChildAt(0).getWidth() - scrollImagenes.getWidth();
                    if (posicion >= maxScroll) {
                        scrollImagenes.smoothScrollTo(0, 0);
                        posicion = 0;
                    }

                    handler.postDelayed(this, delay);
                } else {
                    Log.w(TAG, "scrollImagenes o sus hijos son null, deteniendo scroll automático");
                }
            }
        };

        handler.postDelayed(runnable, delay);
    }

    private void mostrarAlerta(String mensaje, int icono, String textoBoton, Runnable onClick) {
        try {
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

            // Prevenir que se cierre tocando fuera
            dialog.setCancelable(false);
            dialog.show();

            alertButton.setOnClickListener(v -> {
                dialog.dismiss();
                if (onClick != null) {
                    onClick.run();
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error mostrando alerta: " + e.getMessage());
            // Fallback con Toast si falla el AlertDialog
            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "EncuestaActivity destruida");
    }
}
package com.example.proyectosemestral;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.AuthFailureError;

import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Esta actividad muestra el formulario para completar los datos
 * de una visita al parque, incluyendo la selección de sendero,
 * motivo de visita y una bienvenida personalizada.
 */
public class RegistroVisitaActivity2 extends AppCompatActivity {

    private static final String TAG = "RegistroVisitaActivity2";

    // Declaración de vistas
    private Spinner spinnerSenderos;
    private Spinner spinnerMotivos;
    private Button btnIngresar;
    private TextView bienvenida;
    private HorizontalScrollView scrollImagenes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_visita2);

        // Vinculación de vistas con el layout
        spinnerSenderos = findViewById(R.id.spinnerSenderos);
        spinnerMotivos = findViewById(R.id.spinnerMotivos);
        btnIngresar = findViewById(R.id.btnIngresar);
        bienvenida = findViewById(R.id.txtBienvenida); // Asegúrate que en tu XML tenga este ID
        scrollImagenes = findViewById(R.id.scrollImagenes);
        ImageButton btnBack = findViewById(R.id.btnBack);

        // Obtener datos del Intent
        String nombreUsuario = getIntent().getStringExtra("nombreUsuario");
        String cedula = getIntent().getStringExtra("cedula_pasaporte");
        int idVisitante = getIntent().getIntExtra("id_visitante", -1);
        String nacionalidad = getIntent().getStringExtra("nacionalidad");
        String adultoNino = getIntent().getStringExtra("adulto_nino");
        String telefono = getIntent().getStringExtra("telefono");
        String genero = getIntent().getStringExtra("genero");

        Log.d(TAG, "Datos recibidos - Nombre: " + nombreUsuario + ", Cédula: " + cedula + ", ID Visitante: " + idVisitante);

        // Mostrar bienvenida personalizada
        if (nombreUsuario != null && !nombreUsuario.isEmpty()) {
            bienvenida.setText("¡Bienvenid@! \n" + nombreUsuario);
        } else {
            bienvenida.setText("¡Bienvenid@!");
        }

        // Configurar adaptadores para los Spinners
        configurarSpinners();

        // Acción para botón de regreso
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });

        // Acción para botón "INGRESAR"
        btnIngresar.setOnClickListener(v -> {
            String sendero = spinnerSenderos.getSelectedItem().toString();
            String motivo = spinnerMotivos.getSelectedItem().toString();

            // Validar que no se seleccionen las opciones por defecto
            if (sendero.equals("Selecciona un sendero")) {
                mostrarAlerta("Por favor selecciona un sendero", R.drawable.exclamation, "OK", null);
                return;
            }

            if (motivo.equals("Selecciona el motivo")) {
                mostrarAlerta("Por favor selecciona un motivo de visita", R.drawable.exclamation, "OK", null);
                return;
            }

            if (cedula == null || cedula.isEmpty()) {
                mostrarAlerta("¡Cédula no disponible!", R.drawable.exclamation, "Volver", null);
                return;
            }

            registrarVisita(cedula, motivo, sendero, idVisitante, nombreUsuario, nacionalidad, adultoNino, telefono, genero);
        });

        // Iniciar scroll lateral automático de imágenes
        iniciarScrollAutomatico();
    }

    /**
     * Método que configura los adaptadores para los Spinners de senderos y motivos.
     */
    private void configurarSpinners() {
        // 🥾 SENDEROS DISPONIBLES
        String[] senderos = {
                "Selecione un Sendero","Sendero Camarón",
                "Sendero Camino de cruces",
                "Sendero el Pescador",
                "Sendero Búho de Anteojos",
                "Sendero Ciclovía"
        };

        // 🎯 MOTIVOS DE VISITA
        String[] motivos = {
                "Selecciona el motivo",
                "Turismo",
                "Recreación",
                "Deporte",
                "Educación",
                "Investigación",
                "Fotografía",
                "Observación de vida silvestre",
                "Senderismo",
                "Ciclismo",
                "Visita familiar",
                "Actividad grupal",
                "Conservación",
                "Voluntariado",
                "Trabajo",
                "Otro"
        };

        // Configurar adapter para senderos
        ArrayAdapter<String> adapterSendero = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, senderos);
        adapterSendero.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSenderos.setAdapter(adapterSendero);

        // Configurar adapter para motivos
        ArrayAdapter<String> adapterMotivo = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, motivos);
        adapterMotivo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMotivos.setAdapter(adapterMotivo);

        Log.d(TAG, "Spinners configurados con " + senderos.length + " senderos y " + motivos.length + " motivos");
    }

    /**
     * Registra la visita en el backend
     */
    private void registrarVisita(String cedula, String motivo, String sendero, int idVisitante,
                                 String nombreUsuario, String nacionalidad, String adultoNino,
                                 String telefono, String genero) {
        String url = "https://camino-cruces-backend-production.up.railway.app/api/registro-visita/";

        Log.d(TAG, "Registrando visita - Cédula: " + cedula + ", Motivo: " + motivo + ", Sendero: " + sendero);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("cedula_pasaporte", cedula);
            jsonBody.put("razon_visita", motivo);
            jsonBody.put("sendero_visitado", sendero);

            Log.d(TAG, "JSON a enviar: " + jsonBody.toString());
        } catch (Exception e) {
            Log.e(TAG, "Error creando JSON: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("¡Error al preparar los datos!", R.drawable.exclamation, "Volver", null);
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                response -> {
                    Log.d(TAG, "Visita registrada exitosamente: " + response.toString());
                    mostrarAlerta(
                            "¡Visita registrada con éxito!\n\nSendero: " + sendero + "\nMotivo: " + motivo + "\n\n¡Disfruta tu recorrido!",
                            R.drawable.ic_check,
                            "Ir al Dashboard",
                            () -> {
                                Intent intent = new Intent(this, DashboardActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                    );
                },
                error -> {
                    Log.e(TAG, "Error registrando visita: " + error.toString());

                    String msg = "Error al registrar visita";

                    if (error.networkResponse != null) {
                        int statusCode = error.networkResponse.statusCode;
                        Log.e(TAG, "Código de error: " + statusCode);

                        if (error.networkResponse.data != null) {
                            String errorBody = new String(error.networkResponse.data);
                            Log.e(TAG, "Cuerpo del error: " + errorBody);
                        }

                        switch (statusCode) {
                            case 400:
                                msg = "Datos inválidos o visitante no encontrado";
                                break;
                            case 404:
                                msg = "Endpoint no encontrado. Contacta al administrador";
                                break;
                            case 500:
                                msg = "Error del servidor. Intenta más tarde";
                                break;
                            default:
                                msg = "Error " + statusCode + ". Contacta al administrador";
                        }
                    } else {
                        Log.e(TAG, "Error de red o timeout");
                        msg = "Error de conexión. Verifica tu internet";
                    }

                    // Ofrecer ir a la encuesta sin registrar visita
                    mostrarAlerta(msg + "\n\n¿Continuar con la encuesta sin registrar visita?",
                            R.drawable.exclamation, "Continuar", () -> {
                                Intent intent = new Intent(this, Encuesta.class);
                                intent.putExtra("id_visita", idVisitante); // Usar ID visitante como fallback
                                intent.putExtra("id_visitante", idVisitante);
                                intent.putExtra("cedula_pasaporte", cedula);
                                intent.putExtra("nombre_visitante", nombreUsuario);
                                intent.putExtra("nacionalidad", nacionalidad);
                                intent.putExtra("adulto_nino", adultoNino);
                                intent.putExtra("telefono", telefono);
                                intent.putExtra("genero", genero);
                                intent.putExtra("identificacion", cedula);

                                Log.d(TAG, "Navegando a encuesta sin registro de visita");
                                startActivity(intent);
                                finish();
                            });
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
        request.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                15000, // 15 segundos timeout
                1, // 1 retry
                com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);
    }

    /**
     * Método que crea un scroll automático y suave en el HorizontalScrollView de imágenes.
     */
    private void iniciarScrollAutomatico() {
        if (scrollImagenes == null) {
            Log.w(TAG, "scrollImagenes es null, no se puede iniciar scroll automático");
            return;
        }

        final Handler handler = new Handler();
        final int paso = 5;    // Pixeles por paso
        final int retardo = 20; // Milisegundos entre pasos

        Runnable runnable = new Runnable() {
            int posicion = 0;

            @Override
            public void run() {
                if (scrollImagenes != null && scrollImagenes.getChildCount() > 0) {
                    scrollImagenes.smoothScrollBy(paso, 0);
                    posicion += paso;

                    int maxScroll = scrollImagenes.getChildAt(0).getWidth() - scrollImagenes.getWidth();
                    if (posicion >= maxScroll) {
                        scrollImagenes.smoothScrollTo(0, 0);
                        posicion = 0;
                    }

                    handler.postDelayed(this, retardo);
                } else {
                    Log.w(TAG, "scrollImagenes o sus hijos son null, deteniendo scroll automático");
                }
            }
        };

        handler.postDelayed(runnable, retardo);
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
        Log.d(TAG, "RegistroVisitaActivity2 destruida");
    }
}
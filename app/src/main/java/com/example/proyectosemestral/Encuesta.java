package com.example.proyectosemestral;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ArrayAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.AuthFailureError;

import org.json.JSONObject;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Encuesta extends AppCompatActivity {

    private static final String TAG = "Encuesta";

    EditText editTextSexo, editTextOcupacion, editTextEstudios, editTextPorque,
            editTextGusto, editTextNoGusto, editTextRecomendar, editTextComentarios;
    Spinner spinnerActividades, spinnerExAps;
    CheckBox checkBoxSolo, checkBoxFamiliares, checkBoxAmigos, checkBoxPlaneaSi, checkBoxPlaneaNo;
    Button btnEnviar;

    // Variables para almacenar datos del visitante
    private int idVisita = -1;
    private int idVisitante = -1;
    private String nombreVisitante = "";
    private String cedulaPasaporte = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_encuesta);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Obtener datos del Intent
        obtenerDatosDelIntent();

        // Inicializar EditTexts
        editTextSexo = findViewById(R.id.EditTextSexo);
        editTextOcupacion = findViewById(R.id.EditTextOcupación);
        editTextEstudios = findViewById(R.id.EditTextEstudios);
        editTextPorque = findViewById(R.id.porque);
        editTextGusto = findViewById(R.id.EditTextGusto);
        editTextNoGusto = findViewById(R.id.EditTextNoGusto);
        editTextRecomendar = findViewById(R.id.EditTextRecomendar);
        editTextComentarios = findViewById(R.id.EditTextComentarios);

        // Pre-llenar algunos campos si tenemos los datos del visitante
        if (!nombreVisitante.isEmpty()) {
            Log.d(TAG, "Pre-llenando datos para: " + nombreVisitante);
            String genero = getIntent().getStringExtra("genero");
            if (genero != null && !genero.equals("No especificado")) {
                editTextSexo.setText(genero);
            }
        }

        ImageView btnregresar = findViewById(R.id.regresar);
        btnregresar.setOnClickListener(v -> {
            Intent intent = new Intent(this, EncuestaActivity.class);
            if (!cedulaPasaporte.isEmpty()) {
                intent.putExtra("identificacion", cedulaPasaporte);
            }
            startActivity(intent);
        });

        // Inicializar Spinners
        spinnerActividades = findViewById(R.id.SpinnerAps);
        spinnerExAps = findViewById(R.id.SpinnerExAps);

        // Configurar opciones para Spinner de actividades experimentadas
        String[] actividades = {
                "Selecione una opción",
                "Redes sociales",
                "Recomendación de amigos",
                "Publicidad",
                "Otra fuente"
        };
        ArrayAdapter<String> actividadesAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                actividades
        );
        actividadesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActividades.setAdapter(actividadesAdapter);

        // Configurar opciones para Spinner de cómo se enteró
        String[] exAps = {
                "Selecione una opción",
                "Buceo y Esnorqueling",
                "Uso Público de Playas",
                "Senderismo",
                "Observación de desove de tortugas",
                "Observación de Cetáceos",
                "Contacto con Culturas Locales",
                "Visitación a sitios de interés histórico"
        };
        ArrayAdapter<String> exApsAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                exAps
        );
        exApsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExAps.setAdapter(exApsAdapter);

        // Inicializar CheckBoxes
        checkBoxSolo = findViewById(R.id.CheckBoxSolo);
        checkBoxFamiliares = findViewById(R.id.CheckBoxFamiliares);
        checkBoxAmigos = findViewById(R.id.CheckBoxAmigos);
        checkBoxPlaneaSi = findViewById(R.id.CheckBoxPlaneaSi);
        checkBoxPlaneaNo = findViewById(R.id.CheckBoxPlaneaNo);

        // Solo seleccionar un checkbox
        checkBoxPlaneaSi.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) checkBoxPlaneaNo.setChecked(false);
        });
        checkBoxPlaneaNo.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) checkBoxPlaneaSi.setChecked(false);
        });

        // Inicializar Button
        btnEnviar = findViewById(R.id.BtnEnviar);

        btnEnviar.setOnClickListener(v -> {
            if (camposEstanVacios()) {
                mostrarAlerta("¡Campos vacíos por llenar!", R.drawable.exclamation, "Volver", null);
                return;
            }

            // Validar que al menos una opción de "planea volver" esté seleccionada
            if (!checkBoxPlaneaSi.isChecked() && !checkBoxPlaneaNo.isChecked()) {
                mostrarAlerta("Por favor selecciona si planeas volver o no", R.drawable.exclamation, "Volver", null);
                return;
            }

            // Validar selección en los Spinners
            if (spinnerSeleccionInvalida(spinnerActividades) || spinnerSeleccionInvalida(spinnerExAps)) {
                mostrarAlerta("Por favor seleccione una opción válida en los campos de actividades", R.drawable.exclamation, "Volver", null);
                return;
            }


            // Validar que tenemos ID válido
            if (idVisita == -1) {
                String debugInfo = "Debug Info:\n" +
                        "ID Visita: " + idVisita + "\n" +
                        "ID Visitante: " + idVisitante + "\n" +
                        "Nombre: " + nombreVisitante + "\n" +
                        "Cédula: " + cedulaPasaporte;

                Log.e(TAG, "❌ " + debugInfo);

                mostrarAlerta("Error: No se recibió ID de la visita válido\n\n" + debugInfo,
                        R.drawable.exclamation, "Cerrar", null);
                return;
            }

            enviarEncuesta();
        });
    }

    private boolean spinnerSeleccionInvalida(Spinner spinner) {
        String seleccion = spinner.getSelectedItem().toString();
        return seleccion.equals("Selecione una opción");
    }

    private void obtenerDatosDelIntent() {
        Intent intent = getIntent();


        // Obtener ID de visita
        idVisita = intent.getIntExtra("id_visita", -1);
        idVisitante = intent.getIntExtra("id_visitante", -1);

        // Obtener datos del visitante
        nombreVisitante = intent.getStringExtra("nombre_visitante");
        cedulaPasaporte = intent.getStringExtra("cedula_pasaporte");

        if (nombreVisitante == null) nombreVisitante = "";
        if (cedulaPasaporte == null) cedulaPasaporte = "";

        // Para compatibilidad con versiones anteriores
        if (cedulaPasaporte.isEmpty()) {
            cedulaPasaporte = intent.getStringExtra("identificacion");
            if (cedulaPasaporte == null) cedulaPasaporte = "";
        }

        // LOG FINAL DE DATOS OBTENIDOS
        Log.d(TAG, "========== DATOS FINALES OBTENIDOS ==========");
        Log.d(TAG, "ID Visita: " + idVisita);
        Log.d(TAG, "ID Visitante: " + idVisitante);
        Log.d(TAG, "Nombre Visitante: '" + nombreVisitante + "'");
        Log.d(TAG, "Cédula/Pasaporte: '" + cedulaPasaporte + "'");
        Log.d(TAG, "===========================================");

        // Si no tenemos id_visita, intentar usar id_visitante
        if (idVisita == -1 && idVisitante != -1) {
            Log.w(TAG, "⚠️ No se recibió id_visita, usando id_visitante como fallback");
            idVisita = idVisitante;
        }
    }

    private void enviarEncuesta() {
        // Recolectar datos
        String sexo = editTextSexo.getText().toString().trim();
        String ocupacion = editTextOcupacion.getText().toString().trim();
        String estudios = editTextEstudios.getText().toString().trim();
        String visitaRealiza = getVisitaRealizaSeleccionada();
        String actividad = spinnerActividades.getSelectedItem().toString();
        String planeaVolver = checkBoxPlaneaSi.isChecked() ? "Sí" : "No";
        String porque = editTextPorque.getText().toString().trim();
        String comoSeEntero = spinnerExAps.getSelectedItem().toString();
        String meGusto = editTextGusto.getText().toString().trim();
        String noMeGusto = editTextNoGusto.getText().toString().trim();
        String recomendar = editTextRecomendar.getText().toString().trim();
        String sugerencias = editTextComentarios.getText().toString().trim();

        Log.d(TAG, "🚀 Enviando encuesta con ID Visita: " + idVisita);

        // Crear JSON para formulario
        JSONObject formulario = new JSONObject();
        try {
            formulario.put("sexo", sexo);
            formulario.put("ocupacion", ocupacion);
            formulario.put("estudios", estudios);
            formulario.put("visita_realiza", visitaRealiza);
            formulario.put("actividad_experimentada", actividad);
            formulario.put("planea_volver", planeaVolver);
            formulario.put("porque", porque);
            formulario.put("como_se_entero", comoSeEntero);
            formulario.put("me_gusto", meGusto);
            formulario.put("no_me_gusto", noMeGusto);
            formulario.put("recomendaria", recomendar);
            formulario.put("sugerencias", sugerencias);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error al crear JSON formulario: " + e.getMessage());
            mostrarAlerta("Error al preparar datos", R.drawable.exclamation, "Cerrar", null);
            return;
        }

        // Crear JSON principal
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id_visita", idVisita);
            jsonBody.put("formulario", formulario);

            Log.d(TAG, "📋 JSON a enviar: " + jsonBody.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error al crear JSON principal: " + e.getMessage());
            mostrarAlerta("Error al preparar datos", R.drawable.exclamation, "Cerrar", null);
            return;
        }

        String url = "https://camino-cruces-backend-production.up.railway.app/api/encuestas/registrar/";

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                response -> {
                    Log.d(TAG, "✅ Respuesta exitosa: " + response.toString());

                    mostrarAlerta(
                            "¡Gracias " + (!nombreVisitante.isEmpty() ? nombreVisitante : "") + " por su tiempo!\nEsto nos ayuda a mejorar",
                            R.drawable.ic_check,
                            "Cerrar",
                            () -> {
                                Intent intent = new Intent(this, DashboardActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                    );
                },
                error -> {
                    String msg = "Error al registrar encuesta";
                    Log.e(TAG, "❌ Error en request: " + error.toString());

                    if (error.networkResponse != null) {
                        Log.e(TAG, "Código de respuesta: " + error.networkResponse.statusCode);
                        if (error.networkResponse.data != null) {
                            String errorBody = new String(error.networkResponse.data);
                            Log.e(TAG, "Cuerpo del error: " + errorBody);
                            msg = "Error " + error.networkResponse.statusCode + ": " + errorBody;
                        }
                    } else {
                        Log.e(TAG, "Error de red o timeout");
                        msg = "Error de conexión. Verifica tu internet.";
                    }

                    mostrarAlerta(msg, R.drawable.exclamation, "Cerrar", null);
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

        // Configurar timeout (30 segundos)
        request.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                30000,
                com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);
    }

    private String getVisitaRealizaSeleccionada() {
        List<String> opciones = new ArrayList<>();
        if (checkBoxSolo.isChecked()) opciones.add("solo");
        if (checkBoxFamiliares.isChecked()) opciones.add("con familiares");
        if (checkBoxAmigos.isChecked()) opciones.add("con amigos");

        String resultado = opciones.isEmpty() ? "no especificado" : String.join(", ", opciones);
        Log.d(TAG, "Visita realizada seleccionada: " + resultado);
        return resultado;
    }

    private boolean camposEstanVacios() {
        boolean vacio = editTextSexo.getText().toString().trim().isEmpty() ||
                editTextOcupacion.getText().toString().trim().isEmpty() ||
                editTextEstudios.getText().toString().trim().isEmpty() ||
                editTextPorque.getText().toString().trim().isEmpty() ||
                editTextGusto.getText().toString().trim().isEmpty() ||
                editTextNoGusto.getText().toString().trim().isEmpty() ||
                editTextRecomendar.getText().toString().trim().isEmpty() ||
                editTextComentarios.getText().toString().trim().isEmpty();

        Log.d(TAG, "Campos vacíos: " + vacio);
        return vacio;
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
                if (onClick != null) onClick.run();
            });
        } catch (Exception e) {
            Log.e(TAG, "Error mostrando alerta: " + e.getMessage());
            // Fallback con Toast si falla el AlertDialog
            android.widget.Toast.makeText(this, mensaje, android.widget.Toast.LENGTH_LONG).show();
        }
    }
}
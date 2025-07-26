package com.example.proyectosemestral;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

public class RegistroActivity extends AppCompatActivity {

    private AlertDialog dialog; // para acceder desde el listener si hace falta

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Inputs del formulario
        EditText etNombre = findViewById(R.id.etNombre);
        EditText etIdentificacion = findViewById(R.id.etIdentificacion);
        EditText etEdad = findViewById(R.id.etEdad);
        EditText etTelefono = findViewById(R.id.etTelefono);

        // Spinners
        Spinner spinnerPais = findViewById(R.id.spinnerPais);
        Spinner spinnerSenderos = findViewById(R.id.spinnerSenderos);
        Spinner spinnerMotivos = findViewById(R.id.spinnerMotivos);

        // Botón registrar
        Button btnRegistrar = findViewById(R.id.btnRegistrar);

        // Llenar spinner de países automáticamente en español
        List<String> paisesList = new ArrayList<>();
        paisesList.add("Selecciona un país");

        for (String codigo : Locale.getISOCountries()) {
            Locale locale = new Locale("", codigo);
            String nombrePais = locale.getDisplayCountry(new Locale("es"));
            if (!nombrePais.isEmpty()) {
                paisesList.add(nombrePais);
            }
        }

        // Ordenar alfabéticamente excepto el primero
        Collections.sort(paisesList.subList(1, paisesList.size()));

        ArrayAdapter<String> adapterPaises = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, paisesList);
        spinnerPais.setAdapter(adapterPaises);

        // Datos fijos para senderos y motivos
        String[] senderos = {"Selecione un Sendero","Sendero Camarón",
                "Sendero Camino de cruces",
                "Sendero el Pescador",
                "Sendero Búho de Anteojos",
                "Sendero Ciclovía"};
        String[] motivos = {
                "Seleccione un motivo",
                "Turismo",
                "Educación ambiental",
                "Investigación científica",
                "Fotografía",
                "Recreación familiar",
                "Deporte",
                "Observación de flora y fauna",
                "Otro"
        };

        Spinner spinnerEdadTipo = findViewById(R.id.spinnerEdadTipo);
        Spinner spinnerGenero = findViewById(R.id.spinnerGenero);

        String[] opcionesEdad = {"Seleccione", "Adulto", "Niño"};
        String[] opcionesGenero = {"Seleccione", "Femenino", "Masculino", "Otro"};

        spinnerEdadTipo.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, opcionesEdad));
        spinnerGenero.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, opcionesGenero));
        spinnerSenderos.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, senderos));
        spinnerMotivos.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, motivos));

        // Acción del botón REGISTRAR
        btnRegistrar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String identificacion = etIdentificacion.getText().toString().trim();
            String edad = etEdad.getText().toString().trim();
            String telefono = etTelefono.getText().toString().trim();

            String pais = spinnerPais.getSelectedItem().toString();
            String sendero = spinnerSenderos.getSelectedItem().toString();
            String motivo = spinnerMotivos.getSelectedItem().toString();

            String edadTipo = spinnerEdadTipo.getSelectedItem().toString();
            String genero = spinnerGenero.getSelectedItem().toString();

            if (edadTipo.equals("Seleccione") || genero.equals("Seleccione")) {
                mostrarAlerta("Selecciona el tipo de edad y género", R.drawable.exclamation, "Volver", null);
                return;
            }

            // 🟡 Validación de campos vacíos
            if (nombre.isEmpty() || identificacion.isEmpty() || edad.isEmpty() || telefono.isEmpty()) {
                mostrarAlerta("¡Campos vacíos por llenar!", R.drawable.exclamation, "Volver", null);
                return;
            }

            // 🟠 Validación de selección en país y motivo
            if (pais.equals("Selecciona un país") || motivo.equals("Seleccione un motivo")) {
                mostrarAlerta("Por favor selecciona un país y un motivo válidos", R.drawable.exclamation, "Volver", null);
                return;
            }

            // 🟢 Aquí empieza la preparación y envío de la petición
            String url = "https://camino-cruces-backend-production.up.railway.app/api/registrar_visitante_y_visita/";

            RequestQueue queue = Volley.newRequestQueue(RegistroActivity.this);

            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("cedula_pasaporte", identificacion);
                jsonBody.put("nombre_visitante", nombre);
                jsonBody.put("nacionalidad", pais);
                jsonBody.put("adulto_nino", edadTipo);
                jsonBody.put("telefono", telefono);
                jsonBody.put("genero", genero);
                jsonBody.put("razon_visita", motivo);
                jsonBody.put("sendero_visitado", sendero);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al preparar datos", Toast.LENGTH_SHORT).show();
                return;
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                    response -> {
                        mostrarAlerta("¡Se ha registrado un nuevo visitante!", R.drawable.ic_check, "Cerrar", () -> {
                            Intent intent = new Intent(RegistroActivity.this, DashboardActivity.class);
                            startActivity(intent);
                            finish();
                        });
                    },
                    error -> {
                        String msg = "Error al registrar visitante";
                        if (error.networkResponse != null && error.networkResponse.statusCode == 400) {
                            msg = "Datos inválidos o visitante ya registrado";
                        }
                        Toast.makeText(RegistroActivity.this, msg, Toast.LENGTH_LONG).show();
                    });

            queue.add(request);
        });


        // Navegación inferior
        LinearLayout btnRegistro = findViewById(R.id.btnRegistro);
        LinearLayout btnEncuesta = findViewById(R.id.btnEncuesta);
        LinearLayout btnDashboard = findViewById(R.id.btnDashboard);

        btnRegistro.setOnClickListener(v -> {});
        btnEncuesta.setOnClickListener(v -> startActivity(new Intent(this, EncuestaActivity.class)));
        btnDashboard.setOnClickListener(v -> startActivity(new Intent(this, DashboardActivity.class)));

        TextView txtNoPrimeraVez = findViewById(R.id.txtNoPrimeraVez);
        txtNoPrimeraVez.setOnClickListener(v -> startActivity(new Intent(this, RegistroVisitaActivity.class)));
    }

    // Método alerta mejorado con callback opcional
    private void mostrarAlerta(String mensaje, int icono, String textoBoton, Runnable onClose) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.alert_custom, null);

        TextView alertMessage = view.findViewById(R.id.alert_message);
        ImageView alertIcon = view.findViewById(R.id.alert_icon);
        Button alertButton = view.findViewById(R.id.alert_button);

        alertMessage.setText(mensaje);
        alertIcon.setImageResource(icono);
        alertButton.setText(textoBoton);

        builder.setView(view);
        dialog = builder.create();
        dialog.show();

        alertButton.setOnClickListener(v -> {
            dialog.dismiss();
            if (onClose != null) onClose.run();
        });
    }
}

package com.example.proyectosemestral;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class Encuesta extends AppCompatActivity {

    EditText editTextSexo, editTextOcupacion, editTextEstudios, editTextOtros,
            editTextGusto, editTextNoGusto, editTextRecomendar, editTextComentarios;
    Spinner spinnerActividades, spinnerExAps;
    CheckBox checkBoxSolo, checkBoxFamiliares, checkBoxAmigos, checkBoxPlaneaSi, checkBoxPlaneaNo;
    Button btnEnviar;

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

        // Inicializar EditTexts
        editTextSexo = findViewById(R.id.EditTextSexo);
        editTextOcupacion = findViewById(R.id.EditTextOcupación);
        editTextEstudios = findViewById(R.id.EditTextEstudios);
        editTextOtros = findViewById(R.id.EditTextOtros);
        editTextGusto = findViewById(R.id.EditTextGusto);
        editTextNoGusto = findViewById(R.id.EditTextNoGusto);
        editTextRecomendar = findViewById(R.id.EditTextRecomendar);
        editTextComentarios = findViewById(R.id.EditTextComentarios);

        ImageView btnregresar = findViewById(R.id.regresar);
        btnregresar.setOnClickListener(v -> startActivity(new Intent(this, EncuestaActivity.class)));

        // Inicializar Spinners
        spinnerActividades = findViewById(R.id.SpinnerAps); // Ajusta si hay otro Spinner
        spinnerExAps = findViewById(R.id.SpinnerExAps);
        // Inicializar CheckBoxes
        checkBoxSolo = findViewById(R.id.CheckBoxSolo);
        checkBoxFamiliares = findViewById(R.id.CheckBoxFamiliares);
        checkBoxAmigos = findViewById(R.id.CheckBoxAmigos);
        checkBoxPlaneaSi = findViewById(R.id.CheckBoxPlaneaSi);
        checkBoxPlaneaNo = findViewById(R.id.CheckBoxPlaneaNo);
        checkBoxSolo.setOnCheckedChangeListener((buttonView, isChecked) -> actualizarEstadoEditTextOtros());
        checkBoxFamiliares.setOnCheckedChangeListener((buttonView, isChecked) -> actualizarEstadoEditTextOtros());
        checkBoxAmigos.setOnCheckedChangeListener((buttonView, isChecked) -> actualizarEstadoEditTextOtros());

        //solo seleccionar un checkbox
        checkBoxPlaneaSi.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBoxPlaneaNo.setChecked(false);
            }
        });

        checkBoxPlaneaNo.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBoxPlaneaSi.setChecked(false);
            }
        });

        // Inicializar Button
        btnEnviar = findViewById(R.id.BtnEnviar);



        btnEnviar.setOnClickListener(v -> {
            if (camposEstanVacios()) {
                mostrarAlerta("¡Campos vacíos por llenar!", R.drawable.exclamation, "Volver");
                return;
            }

            // Recolectar datos
            String sexo = editTextSexo.getText().toString().trim();
            String ocupacion = editTextOcupacion.getText().toString().trim();
            String estudios = editTextEstudios.getText().toString().trim();
            String visitaRealiza = getVisitaRealizaSeleccionada();
            String actividad = spinnerActividades.getSelectedItem().toString();
            String planeaVolver = checkBoxPlaneaSi.isChecked() ? "Sí" : "No";
            String porque = editTextOtros.getText().toString().trim();
            String comoSeEntero = spinnerExAps.getSelectedItem().toString();
            String meGusto = editTextGusto.getText().toString().trim();
            String noMeGusto = editTextNoGusto.getText().toString().trim();
            String recomendar = editTextRecomendar.getText().toString().trim();
            String sugerencias = editTextComentarios.getText().toString().trim();

            // Obtener ID de visita
            int idVisita = getIntent().getIntExtra("id_visita", -1);
            if (idVisita == -1) {
                mostrarAlerta("No se recibió ID de la visita", R.drawable.exclamation, "Cerrar");
                return;
            }

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
                mostrarAlerta("Error al preparar datos", R.drawable.exclamation, "Cerrar");
                return;
            }

            // Crear JSON principal
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("id_visita", idVisita);
                jsonBody.put("formulario", formulario);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            String url = "https://camino-cruces-backend-production.up.railway.app/api/encuestas/registrar/";

            RequestQueue queue = com.android.volley.toolbox.Volley.newRequestQueue(Encuesta.this);

            com.android.volley.toolbox.JsonObjectRequest request = new com.android.volley.toolbox.JsonObjectRequest(
                    com.android.volley.Request.Method.POST,
                    url,
                    jsonBody,
                    response -> mostrarAlerta("¡Gracias por su tiempo!\nEsto nos ayuda a mejorar", R.drawable.ic_check, "Cerrar"),
                    error -> {
                        String msg = "Error al registrar encuesta";
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            msg = new String(error.networkResponse.data);
                        }
                        mostrarAlerta(msg, R.drawable.exclamation, "Cerrar");
                    });

            queue.add(request);
        });

    }
    private void actualizarEstadoEditTextOtros() {
        if (checkBoxSolo.isChecked() || checkBoxFamiliares.isChecked() || checkBoxAmigos.isChecked()) {
            editTextOtros.setEnabled(false); // Desactiva el campo
            editTextOtros.setText(""); // Limpia el texto si estaba escrito
        } else {
            editTextOtros.setEnabled(true); // Activa el campo
        }
    }
    private String getVisitaRealizaSeleccionada() {
        List<String> opciones = new ArrayList<>();
        if (checkBoxSolo.isChecked()) opciones.add("solo");
        if (checkBoxFamiliares.isChecked()) opciones.add("con familiares");
        if (checkBoxAmigos.isChecked()) opciones.add("con amigos");
        return opciones.isEmpty() ? "no especificado" : String.join(", ", opciones);
    }

    private boolean camposEstanVacios() {
        return editTextSexo.getText().toString().trim().isEmpty() ||
                editTextOcupacion.getText().toString().trim().isEmpty() ||
                editTextEstudios.getText().toString().trim().isEmpty() ||
                editTextOtros.getText().toString().trim().isEmpty() ||
                editTextGusto.getText().toString().trim().isEmpty() ||
                editTextNoGusto.getText().toString().trim().isEmpty() ||
                editTextRecomendar.getText().toString().trim().isEmpty() ||
                editTextComentarios.getText().toString().trim().isEmpty()
                // Puedes agregar validación de Spinner si es necesario
                // || spinnerActividades.getSelectedItemPosition() == 0
                ;
    }

    private void mostrarAlerta(String mensaje, int icono, String textoBoton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Encuesta.this);
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

        alertButton.setOnClickListener(v -> dialog.dismiss());
    }
}

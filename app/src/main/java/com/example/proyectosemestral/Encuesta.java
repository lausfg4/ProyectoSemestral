package com.example.proyectosemestral;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
            } else {
                mostrarAlerta("¡Gracias por su tiempo!\nEsto nos ayuda a mejorar", R.drawable.ic_check, "Cerrar");
            }
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

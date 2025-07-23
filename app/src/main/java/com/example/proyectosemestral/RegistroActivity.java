package com.example.proyectosemestral;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegistroActivity extends AppCompatActivity {

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

        // Datos de ejemplo para los spinners
        String[] paises = {"Panamá"};
        String[] senderos = {"Sendero El Charco"};
        String[] motivos = {"Turismo"};

        // Adaptadores para spinners
        spinnerPais.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, paises));
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

            // Validación básica
            if (nombre.isEmpty() || identificacion.isEmpty() || edad.isEmpty() || telefono.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                // Muestra datos por ahora
                Toast.makeText(this, "¡Registro capturado correctamente!", Toast.LENGTH_SHORT).show();

                // Aquí podrías enviar los datos a una base, Firebase, API, etc.
                System.out.println("Nombre: " + nombre);
                System.out.println("Identificación: " + identificacion);
                System.out.println("Edad: " + edad);
                System.out.println("Teléfono: " + telefono);
                System.out.println("País: " + pais);
                System.out.println("Sendero: " + sendero);
                System.out.println("Motivo: " + motivo);
            }
        });

        // Botones de navegación inferior
        LinearLayout btnRegistro = findViewById(R.id.btnRegistro);
        LinearLayout btnEncuesta = findViewById(R.id.btnEncuesta);
        LinearLayout btnDashboard = findViewById(R.id.btnDashboard);

        btnRegistro.setOnClickListener(v -> {
            // Ya estás en esta actividad
        });

        btnEncuesta.setOnClickListener(v -> {
            Intent intent = new Intent(RegistroActivity.this, EncuestaActivity.class);
            startActivity(intent);
        });

        btnDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(RegistroActivity.this, com.example.proyectosemestral.DashboardActivity.class);
            startActivity(intent);
        });

        // Texto "¿No es tu primera vez?"
        TextView txtNoPrimeraVez = findViewById(R.id.txtNoPrimeraVez);
        txtNoPrimeraVez.setOnClickListener(v -> {
            Intent intent = new Intent(RegistroActivity.this, RegistroVisitaActivity.class);
            startActivity(intent);
        });
    }
}



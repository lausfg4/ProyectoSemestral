package com.example.proyectosemestral;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class RegistroVisitaActivity extends AppCompatActivity {

    private EditText etIdentificacion;
    private Button btnIngresar;

    // Simulación de base de datos con cédula y nombre
    private HashMap<String, String> baseUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_visita);

        // Referencias
        etIdentificacion = findViewById(R.id.etIdentificacion);
        btnIngresar = findViewById(R.id.btnIngresar);

        // Botón de regreso
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        // Inicializar base simulada
        inicializarUsuarios();

        // Acción al presionar "Ingresar"
        btnIngresar.setOnClickListener(v -> {
            String identificacion = etIdentificacion.getText().toString().trim();

            if (identificacion.isEmpty()) {
                Toast.makeText(this, "Ingrese una cédula válida", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verifica si existe en la base
            if (baseUsuarios.containsKey(identificacion)) {
                String nombreUsuario = baseUsuarios.get(identificacion);

                // Enviar nombre al siguiente Activity
                Intent intent = new Intent(RegistroVisitaActivity.this, RegistroVisitaActivity2.class);
                intent.putExtra("nombreUsuario", nombreUsuario);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Cédula no registrada", Toast.LENGTH_SHORT).show();
            }
        });

        // Scroll automático de imágenes
        HorizontalScrollView scrollImagenes = findViewById(R.id.scrollImagenes);
        final Handler handler = new Handler();
        final int step = 5;
        final int delay = 20;

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

    /**
     * Inicializa una base de datos simulada de usuarios con cédula → nombre.
     */
    private void inicializarUsuarios() {
        baseUsuarios = new HashMap<>();
        baseUsuarios.put("8-123-456", "Juan Pérez");
        baseUsuarios.put("4-789-012", "Ana Gómez");
        baseUsuarios.put("3-456-789", "Carlos Ruiz");
        // Puedes agregar más aquí
    }
}


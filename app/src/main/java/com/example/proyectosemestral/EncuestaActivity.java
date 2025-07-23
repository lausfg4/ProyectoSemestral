package com.example.proyectosemestral;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;

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
        btncomenzar.setOnClickListener(v -> startActivity(new Intent(this, Encuesta.class)));

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
}
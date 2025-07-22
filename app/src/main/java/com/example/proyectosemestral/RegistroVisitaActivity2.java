package com.example.proyectosemestral;


import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Esta actividad muestra el formulario para completar los datos
 * de una visita al parque, incluyendo la selección de sendero,
 * motivo de visita y una bienvenida personalizada.
 */
public class RegistroVisitaActivity2 extends AppCompatActivity {

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

        // Obtener el nombre del usuario desde el Intent
        String nombreUsuario = getIntent().getStringExtra("nombreUsuario");

        // Mostrar bienvenida personalizada
        if (nombreUsuario != null && !nombreUsuario.isEmpty()) {
            bienvenida.setText("¡Bienvenid@! \n" + nombreUsuario);
        } else {
            bienvenida.setText("¡Bienvenid@!");
        }

        // Configurar adaptadores para los Spinners
        configurarSpinners();

        // Acción para botón de regreso
        btnBack.setOnClickListener(v -> onBackPressed());

        // Acción para botón "ENVIAR"
        btnIngresar.setOnClickListener(v ->
                Toast.makeText(RegistroVisitaActivity2.this,
                        "Datos enviados", Toast.LENGTH_SHORT).show()
        );

        // Iniciar scroll lateral automático de imágenes
        iniciarScrollAutomatico();
    }

    /**
     * Método que configura los adaptadores para los Spinners de senderos y motivos.
     */
    private void configurarSpinners() {
        String[] senderos = {"Sendero El Charco"};
        String[] motivos = {"Turismo"};

        ArrayAdapter<String> adapterSendero = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, senderos);
        adapterSendero.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSenderos.setAdapter(adapterSendero);

        ArrayAdapter<String> adapterMotivo = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, motivos);
        adapterMotivo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMotivos.setAdapter(adapterMotivo);
    }

    /**
     * Método que crea un scroll automático y suave en el HorizontalScrollView de imágenes.
     */
    private void iniciarScrollAutomatico() {
        final Handler handler = new Handler();
        final int paso = 5;    // Pixeles por paso
        final int retardo = 20; // Milisegundos entre pasos

        Runnable runnable = new Runnable() {
            int posicion = 0;

            @Override
            public void run() {
                scrollImagenes.smoothScrollBy(paso, 0);
                posicion += paso;

                int maxScroll = scrollImagenes.getChildAt(0).getWidth() - scrollImagenes.getWidth();
                if (posicion >= maxScroll) {
                    scrollImagenes.smoothScrollTo(0, 0);
                    posicion = 0;
                }

                handler.postDelayed(this, retardo);
            }
        };

        handler.postDelayed(runnable, retardo);
    }
}




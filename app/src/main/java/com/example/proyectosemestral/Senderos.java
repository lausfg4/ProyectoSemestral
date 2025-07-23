package com.example.proyectosemestral;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Senderos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_senderos);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 🔥 Contenedor de senderos
        LinearLayout senderosContainer = findViewById(R.id.senderos_container);

        // 📦 Datos (de prueba - luego vendrán del backend)
       // int[] imagenes = {
                //R.drawable.sendero_camaron,
                //R.drawable.sendero_cruces,
               //R.drawable.sendero_pescador,
                //R.drawable.sendero_quetzal,
                //R.drawable.sendero_historia
       // };

        String[] nombres = {
                "Sendero Camarón",
                "Sendero Camino de Cruces",
                "Sendero El Pescador",
                "Sendero Quetzal",
                "Sendero Histórico"
        };

        String[] detalles = {
                "Dificultad: Media\nRecorrido: 3km\nActividades: Senderismo, Aves",
                "Dificultad: Alta\nRecorrido: 5km\nActividades: Ciclismo, Historia",
                "Dificultad: Baja\nRecorrido: 2km\nActividades: Fauna y flora",
                "Dificultad: Media\nRecorrido: 4km\nActividades: Senderismo",
                "Dificultad: Alta\nRecorrido: 6km\nActividades: Historia y cultura"
        };

        float[] estrellas = {4.5f, 4.0f, 5.0f, 3.5f, 3.0f}; // Valoraciones promedio
        int[] resenas = {23, 19, 15, 12, 9}; // Cantidad de reseñas

        // 💥 Inflar y llenar cada card
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < nombres.length; i++) {
            View card = inflater.inflate(R.layout.sendero_card, senderosContainer, false);

            ImageView imageView = card.findViewById(R.id.sendero_image);
            TextView nombreView = card.findViewById(R.id.sendero_nombre);
            TextView detallesView = card.findViewById(R.id.sendero_detalles);
            RatingBar ratingBar = card.findViewById(R.id.sendero_rating);
            TextView resenasView = card.findViewById(R.id.sendero_resenas);

          //  imageView.setImageResource(imagenes[i]);
            nombreView.setText(nombres[i]);
            detallesView.setText(detalles[i]);
            ratingBar.setRating(estrellas[i]); // Valor dinámico
            resenasView.setText("(" + resenas[i] + " reseñas)");

            senderosContainer.addView(card);
        }

        // ✅ Botón salir (footer)
        ImageButton btnSalir = findViewById(R.id.btn_salir);
        btnSalir.setOnClickListener(v -> mostrarDialogoSalir());

        ImageView InicioBtn = findViewById(R.id.btn_incio);
        InicioBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Senderos.this, Inicio.class);
            startActivity(intent);
        });
    }

    // 🔥 Método para mostrar el diálogo personalizado
    private void mostrarDialogoSalir() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_salir, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // Botón cerrar (icono X)
        ImageView btnCerrar = dialogView.findViewById(R.id.btn_cerrar);
        btnCerrar.setOnClickListener(view -> alertDialog.dismiss());

        // Botón "Salir" en el popup
        Button btnConfirmarSalir = dialogView.findViewById(R.id.btn_salir);
        btnConfirmarSalir.setOnClickListener(view -> {
            alertDialog.dismiss();
            finishAffinity(); // Cierra la app
        });
    }
}

package com.example.proyectosemestral;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Inicio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Botón de los pinos → clase Senderos
        ImageView senderosBtn = findViewById(R.id.rpx4inz2zboq);
        senderosBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Inicio.this, Senderos.class);
            startActivity(intent);
        });

        // Botón salir → muestra diálogo
        ImageView salirBtn = findViewById(R.id.salir);
        salirBtn.setOnClickListener(v -> mostrarDialogoSalir());
    }

    // 🔥 Método para mostrar el diálogo personalizado de salida
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
            finishAffinity(); // Cierra completamente la app
        });
    }
}

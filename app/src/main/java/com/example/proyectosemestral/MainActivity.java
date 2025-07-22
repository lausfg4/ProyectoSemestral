package com.example.proyectosemestral;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    Button btnExplorar;
    TextView linkText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Ajustes de UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referencias a los elementos
        btnExplorar = findViewById(R.id.btnExplorar);
        linkText = findViewById(R.id.linkText);

        // Acción al pulsar EXPLORAR
        btnExplorar.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Inicio.class);
            startActivity(intent);
        });

        // Acción al pulsar "Inicie sesión aquí"
        linkText.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, IniciarSesion.class);
            startActivity(intent);
        });
    }
}

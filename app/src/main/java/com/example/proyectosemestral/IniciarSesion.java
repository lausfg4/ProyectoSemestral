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

public class IniciarSesion extends AppCompatActivity {

    Button btnIngresar;
    TextView linkText3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_iniciar_sesion);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referencias a los elementos
        btnIngresar = findViewById(R.id.btnRegistrarse2);
        linkText3 = findViewById(R.id.linkText3);

        // Acción al pulsar "Ingresar"
        btnIngresar.setOnClickListener(view -> {
            // Aquí irá la lógica de autenticación
            // Por ahora solo redirigimos al DashboardAdmin
           // Intent intent = new Intent(IniciarSesion.this, DashboardAdmin.class);
            //startActivity(intent);
        });

        // Acción al pulsar "Crea una"
        linkText3.setOnClickListener(view -> {
            Intent intent = new Intent(IniciarSesion.this, Registrarse.class);
            startActivity(intent);
        });
    }
}

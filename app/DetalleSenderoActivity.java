package com.example.proyectosemestral;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

public class DetalleSenderoActivity extends AppCompatActivity {

    private int senderoId;
    private String senderoNombre, senderoDescripcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalle_sendero);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Recibir datos
        senderoId = getIntent().getIntExtra("sendero_id", -1);
        senderoNombre = getIntent().getStringExtra("sendero_nombre");
        senderoDescripcion = getIntent().getStringExtra("sendero_descripcion");

        // Referencias a las vistas
        TextView nombreView = findViewById(R.id.detalle_nombre);
        TextView descripcionView = findViewById(R.id.detalle_descripcion);
        ImageView imagenView = findViewById(R.id.detalle_imagen);
        Button btnComentar = findViewById(R.id.btn_comentar);

        nombreView.setText(senderoNombre);
        descripcionView.setText(senderoDescripcion);
        imagenView.setImageResource(obtenerImagenPorNombre(senderoNombre));

        btnComentar.setOnClickListener(v -> {
            Intent intent = new Intent(DetalleSenderoActivity.this, SenderoComentarios.class);
            intent.putExtra("sendero_id", senderoId);
            intent.putExtra("sendero_nombre", senderoNombre);
            startActivity(intent);
        });
    }

    private int obtenerImagenPorNombre(String nombre) {
        nombre = nombre.toLowerCase();
        if (nombre.contains("camaron")) return R.drawable.senderoscamaron;
        if (nombre.contains("cruces")) return R.drawable.senderoscaminocruces;
        if (nombre.contains("ciclovia")) return R.drawable.senderosciclovia;
        if (nombre.contains("pescador")) return R.drawable.senderoselpescador;
        if (nombre.contains("buho")) return R.drawable.senderosbuhodeanteojos;
        return R.drawable.logo;
    }
}

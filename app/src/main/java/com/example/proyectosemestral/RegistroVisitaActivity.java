package com.example.proyectosemestral;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class RegistroVisitaActivity extends AppCompatActivity {

    private EditText etIdentificacion;
    private Button btnIngresar;

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

        // Acción al presionar "Ingresar"
        btnIngresar.setOnClickListener(v -> {
            String identificacion = etIdentificacion.getText().toString().trim();

            if (identificacion.isEmpty()) {
                mostrarAlerta("¡Ingrese su identificación!", R.drawable.exclamation, "Volver", null);
                return;
            }

            String url = "https://camino-cruces-backend-production.up.railway.app/api/visitante/cedula/" + identificacion + "/";

            RequestQueue queue = Volley.newRequestQueue(RegistroVisitaActivity.this);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        // Si la cédula existe, extraemos el nombre y la cédula
                        String nombreUsuario = response.optString("nombre_visitante", "Visitante");
                        String cedulaPasaporte = response.optString("cedula_pasaporte", identificacion);

                        // Enviar datos del visitante a RegistroVisitaActivity2 para registrar nueva visita
                        Intent intent = new Intent(RegistroVisitaActivity.this, RegistroVisitaActivity2.class);
                        intent.putExtra("nombreUsuario", nombreUsuario);
                        intent.putExtra("cedula_pasaporte", cedulaPasaporte);
                        intent.putExtra("visitante_id", response.optInt("id")); // Por si se necesita
                        startActivity(intent);
                    },
                    error -> {
                        // Si no está registrado, enviarlo a registrarse primero
                        mostrarAlerta("¡No estás registrado!", R.drawable.exclamation, "Registrarse", () -> {
                            Intent intent = new Intent(RegistroVisitaActivity.this, RegistroActivity.class);
                            startActivity(intent);
                        });
                    });

            queue.add(request);
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

    private void mostrarAlerta(String mensaje, int icono, String textoBoton, Runnable onClick) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

        alertButton.setOnClickListener(v -> {
            dialog.dismiss();
            if (onClick != null) onClick.run();
        });
    }
}
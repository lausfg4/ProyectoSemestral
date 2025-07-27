package com.example.proyectosemestral;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SenderoComentarios extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private int senderoId;
    private String senderoNombre;

    private TextView nombreSenderoView;
    private RatingBar ratingBar;
    private EditText editComentario;
    private Button btnImportarFoto;
    private Button btnComentar;
    private ImageView imageViewPreview;

    private Bitmap imagenSeleccionada = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sendero_comentarios);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        senderoId = getIntent().getIntExtra("sendero_id", -1);
        senderoNombre = getIntent().getStringExtra("sendero_nombre");

        nombreSenderoView = findViewById(R.id.nombre_sendero);
        ratingBar = findViewById(R.id.ratingBar);
        editComentario = findViewById(R.id.edit_comentario);
        btnImportarFoto = findViewById(R.id.btn_importar_foto);
        btnComentar = findViewById(R.id.btn_comentar);
        imageViewPreview = findViewById(R.id.image_preview); // Asegúrate de agregarlo en tu XML

        nombreSenderoView.setText(senderoNombre);

        btnImportarFoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        btnComentar.setOnClickListener(v -> enviarComentario());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                imagenSeleccionada = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageViewPreview.setImageBitmap(imagenSeleccionada); // Mostrar imagen en pantalla
            } catch (IOException e) {
                Toast.makeText(this, "❌ Error al cargar imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void enviarComentario() {
        float valoracion = ratingBar.getRating();
        String comentario = editComentario.getText().toString();

        if (comentario.isEmpty() || valoracion == 0) {
            Toast.makeText(this, "⚠️ Escribe un comentario y califica el sendero", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "https://camino-cruces-backend-production.up.railway.app/api/comentarios/agregar/";
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(
                Request.Method.POST,
                url,
                response -> {
                    Toast.makeText(this, "✅ Comentario enviado", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> {
                    String mensaje = "❌ Error al enviar comentario";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        mensaje += "\nServidor: " + new String(error.networkResponse.data);
                    }
                    Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usuario_id", "3");
                params.put("sendero", String.valueOf(senderoId));
                params.put("comentario", comentario);
                params.put("valoracion", String.valueOf(valoracion));
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> imagen = new HashMap<>();
                if (imagenSeleccionada != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagenSeleccionada.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                    imagen.put("foto_comentario", new DataPart("comentario.jpg", baos.toByteArray(), "image/jpeg"));
                }
                return imagen;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(multipartRequest);
    }
}

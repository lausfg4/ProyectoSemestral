package com.example.proyectosemestral;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.android.volley.DefaultRetryPolicy;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SenderoComentarios extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private int senderoId;
    private String senderoNombre;
    private int usuarioId;

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

        FirebaseApp.initializeApp(this);

        senderoId = getIntent().getIntExtra("sendero_id", -1);
        senderoNombre = getIntent().getStringExtra("sendero_nombre");
        usuarioId = getIntent().getIntExtra("usuario_id", -1);

        if (usuarioId == -1 || senderoId == -1) {
            Toast.makeText(this, "❌ Faltan datos del usuario o sendero", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        nombreSenderoView = findViewById(R.id.nombre_sendero);
        ratingBar = findViewById(R.id.ratingBar);
        editComentario = findViewById(R.id.edit_comentario);
        btnImportarFoto = findViewById(R.id.btn_importar_foto);
        btnComentar = findViewById(R.id.btn_comentar);
        imageViewPreview = findViewById(R.id.image_preview);

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
                imageViewPreview.setImageBitmap(imagenSeleccionada);
            } catch (IOException e) {
                Toast.makeText(this, "❌ Error al cargar imagen: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("Imagen", "Error al cargar imagen", e);
            }
        }
    }

    private void enviarComentario() {
        float valoracion = ratingBar.getRating();
        String comentario = editComentario.getText().toString();

        if (comentario.isEmpty() || valoracion == 0) {
            Toast.makeText(this, "⚠ Escribe un comentario y califica el sendero", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imagenSeleccionada != null) {
            subirImagenAFirebase(imagenSeleccionada, urlImagen ->
                    enviarComentarioConUrl(comentario, valoracion, urlImagen));
        } else {
            enviarComentarioConUrl(comentario, valoracion, "");
        }
    }

    private void subirImagenAFirebase(Bitmap bitmap, OnImageUploadListener listener) {
        try {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();

            String timestamp = String.valueOf(System.currentTimeMillis());
            String nombreImagen = "comentarios/img_" + timestamp + ".jpg";
            StorageReference imagenRef = storageRef.child(nombreImagen);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] datos = baos.toByteArray();

            UploadTask uploadTask = imagenRef.putBytes(datos);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                imagenRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    listener.onSuccess(uri.toString());
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al obtener URL, continuando sin imagen", Toast.LENGTH_SHORT).show();
                    listener.onSuccess("");
                });

            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Error al subir imagen, continuando sin foto", Toast.LENGTH_SHORT).show();
                listener.onSuccess("");
            });

        } catch (Exception e) {
            Toast.makeText(this, "Error de configuración, continuando sin imagen", Toast.LENGTH_SHORT).show();
            listener.onSuccess("");
        }
    }

    private void enviarComentarioConUrl(String comentario, float valoracion, String urlImagen) {
        String url = "https://camino-cruces-backend-production.up.railway.app/api/comentarios/agregar/";

        // Crear JSON manual para evitar escape automático de barras
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");
        jsonBuilder.append("\"usuario_id\":").append(usuarioId).append(",");
        jsonBuilder.append("\"sendero\":").append(senderoId).append(",");
        jsonBuilder.append("\"comentario\":\"").append(comentario.replace("\"", "\\\"")).append("\",");
        jsonBuilder.append("\"valoracion\":").append((int)valoracion);

        if (urlImagen != null && !urlImagen.isEmpty()) {
            // Decodificar caracteres URL antes de enviar
            String urlDecodificada = urlImagen
                    .replace("%2F", "/")
                    .replace("%20", " ")
                    .replace("%3A", ":")
                    .replace("%3F", "?")
                    .replace("%3D", "=")
                    .replace("%26", "&");

            jsonBuilder.append(",\"foto_comentario\":\"").append(urlDecodificada).append("\"");
            Log.d("JSONDebug", "URL original: " + urlImagen);
            Log.d("JSONDebug", "URL decodificada: " + urlDecodificada);
        }

        jsonBuilder.append("}");
        String jsonString = jsonBuilder.toString();

        Log.d("JSONDebug", "JSON manual: " + jsonString);

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {
                    Toast.makeText(this, "✅ Comentario enviado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> {
                    if (error.networkResponse != null) {
                        int statusCode = error.networkResponse.statusCode;
                        if (error.networkResponse.data != null) {
                            try {
                                String errorBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                                Toast.makeText(this, "Error " + statusCode + ": " + errorBody, Toast.LENGTH_LONG).show();
                            } catch (Exception ex) {
                                Toast.makeText(this, "Error " + statusCode, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Error " + statusCode, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public byte[] getBody() {
                return jsonString.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
    // ✅ Interfaz funcional para obtener la URL al subir la imagen
    public interface OnImageUploadListener {
        void onSuccess(String imageUrl);
    }
}

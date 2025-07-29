package com.example.proyectosemestral;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request.Builder; // Se usa explícitamente con okhttp3.Request.Builder()
import okhttp3.Response;


public class DashboardActivity extends AppCompatActivity {

    // Botones de navegación
    LinearLayout btnRegistro, btnEncuesta, btnDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ImageView salirBtn = findViewById(R.id.btn_salir);
        salirBtn.setOnClickListener(v -> mostrarDialogoSalir());

        // Referencias a botones inferiores
        btnRegistro = findViewById(R.id.btnRegistro);
        btnEncuesta = findViewById(R.id.btnEncuesta);
        btnDashboard = findViewById(R.id.btnDashboard);

        // Navegación de botones
        btnRegistro.setOnClickListener(v -> startActivity(new Intent(this, RegistroActivity.class)));
        btnEncuesta.setOnClickListener(v -> startActivity(new Intent(this, EncuestaActivity.class)));
        btnDashboard.setOnClickListener(v -> {
            // Ya estamos en Dashboard
        });

        // Cargar datos del dashboard
        cargarDatosDashboard();

        Button btnGenerarReporte = findViewById(R.id.btnReporte);
        btnGenerarReporte.setOnClickListener(view -> {
            descargarReporte();
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refrescar datos cuando se regresa al dashboard
        cargarDatosDashboard();
    }




    private void descargarReporte() {
        String url = "https://camino-cruces-backend-production.up.railway.app/api/reporte-excel/";

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle("Reporte Completo");
        request.setDescription("Descargando reporte Excel...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.US)
                .format(new java.util.Date());
        String fileName = "reporte_completo_" + timestamp + ".xlsx";

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        request.setMimeType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);

        Toast.makeText(this, "📥 Descarga iniciada: " + fileName, Toast.LENGTH_SHORT).show();
    }



    private void cargarDatosDashboard() {
        TextView txtVisitantesHoy = findViewById(R.id.txtVisitantesHoy);
        TextView txtEncuestaCompletadas = findViewById(R.id.txtEncuestaCompletadas);

        cargarEncuestasHoy(txtEncuestaCompletadas);
        cargarVisitantesHoy(txtVisitantesHoy);
        cargarVisitantesPorPais();
        cargarVisitantesPorSendero();
        cargarCalificacionPromedio();
        cargarVisitasRecientes();
    }

    private void cargarEncuestasHoy(TextView txtEncuestaCompletadas) {
        String urlEncuestas = "https://camino-cruces-backend-production.up.railway.app/api/dashboard/encuestas-hoy/";
        RequestQueue queueEncuestas = Volley.newRequestQueue(this);

        Log.d("DashboardActivity", "🔍 Cargando encuestas hoy desde: " + urlEncuestas);

        JsonObjectRequest requestEncuestas = new JsonObjectRequest(
                Request.Method.GET, urlEncuestas, null,
                response -> {
                    try {
                        Log.d("DashboardActivity", "✅ Respuesta encuestas hoy: " + response.toString());
                        int cantidad = response.getInt("encuestas_hoy");
                        Log.d("DashboardActivity", "📊 Encuestas hoy: " + cantidad);
                        txtEncuestaCompletadas.setText(String.valueOf(cantidad));
                    } catch (JSONException e) {
                        Log.e("DashboardActivity", "❌ Error parseando encuestas hoy: " + e.getMessage());
                        txtEncuestaCompletadas.setText("Error");
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e("DashboardActivity", "❌ Error cargando encuestas hoy: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e("DashboardActivity", "Código error encuestas: " + error.networkResponse.statusCode);
                        if (error.networkResponse.data != null) {
                            Log.e("DashboardActivity", "Error body encuestas: " + new String(error.networkResponse.data));
                        }
                    }
                    txtEncuestaCompletadas.setText("Error");
                    error.printStackTrace();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        // Configurar timeout
        requestEncuestas.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                15000, // 15 segundos timeout
                1, // 1 retry
                com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queueEncuestas.add(requestEncuestas);
    }

    private void cargarVisitantesHoy(TextView txtVisitantesHoy) {
        String urlVisitantesHoy = "https://camino-cruces-backend-production.up.railway.app/api/dashboard/visitantes-hoy/";
        RequestQueue queueVisitantes = Volley.newRequestQueue(this);

        JsonObjectRequest requestVisitantes = new JsonObjectRequest(
                Request.Method.GET, urlVisitantesHoy, null,
                response -> {
                    try {
                        // Verificar que la respuesta contenga el campo esperado
                        if (!response.has("visitantes_hoy")) {
                            txtVisitantesHoy.setText("Error");
                            return;
                        }

                        int cantidad = response.getInt("visitantes_hoy");
                        txtVisitantesHoy.setText(String.valueOf(cantidad));

                    } catch (JSONException e) {
                        txtVisitantesHoy.setText("Error");
                        e.printStackTrace();
                    }
                },
                error -> {
                    if (error.networkResponse != null) {
                        int statusCode = error.networkResponse.statusCode;
                        // Mostrar código de error en el UI para debugging
                        txtVisitantesHoy.setText("E" + statusCode);
                    } else {
                        txtVisitantesHoy.setText("Red");
                    }
                    error.printStackTrace();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        // Configurar timeout más largo
        requestVisitantes.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                20000, // 20 segundos timeout
                2, // 2 retries
                com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queueVisitantes.add(requestVisitantes);
    }

    private void cargarVisitantesPorPais() {
        LinearLayout layoutPaises = findViewById(R.id.layoutPaises);
        TextView verTodoPais = findViewById(R.id.verTodoPais);
        int cantidadVisible = 5;

        String urlPaises = "https://camino-cruces-backend-production.up.railway.app/api/dashboard/visitantes-por-pais/";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest requestPaises = new JsonArrayRequest(
                Request.Method.GET, urlPaises, null,
                response -> {
                    try {
                        // Limpiar layout antes de agregar nuevos datos
                        layoutPaises.removeAllViews();

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            String paisNombre = obj.getString("pais");
                            int cantidad = obj.getInt("cantidad");

                            LinearLayout fila = new LinearLayout(this);
                            fila.setOrientation(LinearLayout.HORIZONTAL);
                            fila.setLayoutParams(new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT));
                            fila.setPadding(0, 8, 0, 8);
                            fila.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);

                            TextView pais = new TextView(this);
                            pais.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                            pais.setText(paisNombre);
                            pais.setTextSize(14);
                            pais.setTextColor(getResources().getColor(android.R.color.black));

                            TextView cantidadTv = new TextView(this);
                            cantidadTv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            cantidadTv.setText(String.valueOf(cantidad));
                            cantidadTv.setTextSize(14);
                            cantidadTv.setTextColor(getResources().getColor(android.R.color.black));

                            fila.addView(pais);
                            fila.addView(cantidadTv);

                            if (i >= cantidadVisible) fila.setVisibility(View.GONE);
                            layoutPaises.addView(fila);
                        }

                        if (response.length() > cantidadVisible) {
                            verTodoPais.setVisibility(View.VISIBLE);
                            verTodoPais.setOnClickListener(v -> {
                                for (int i = cantidadVisible; i < layoutPaises.getChildCount(); i++) {
                                    layoutPaises.getChildAt(i).setVisibility(View.VISIBLE);
                                }
                                verTodoPais.setVisibility(View.GONE);
                            });
                        } else {
                            verTodoPais.setVisibility(View.GONE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        queue.add(requestPaises);
    }

    private void cargarVisitantesPorSendero() {
        LinearLayout layoutSenderos = findViewById(R.id.layoutSenderos);
        String urlSenderos = "https://camino-cruces-backend-production.up.railway.app/api/dashboard/visitantes-por-sendero/";

        RequestQueue queueSenderos = Volley.newRequestQueue(this);

        JsonArrayRequest requestSenderos = new JsonArrayRequest(
                Request.Method.GET, urlSenderos, null,
                response -> {
                    try {
                        // Limpiar layout antes de agregar nuevos datos
                        layoutSenderos.removeAllViews();

                        if (response.length() == 0) {
                            TextView noData = new TextView(this);
                            noData.setText("No hay datos de senderos");
                            noData.setTextColor(Color.GRAY);
                            layoutSenderos.addView(noData);
                            return;
                        }

                        int maxCantidad = 1;
                        for (int i = 0; i < response.length(); i++) {
                            int cantidad = response.getJSONObject(i).getInt("cantidad");
                            if (cantidad > maxCantidad) maxCantidad = cantidad;
                        }

                        String[] colores = {"#0B3D2E", "#145C47", "#6B8E89", "#A0BFB9", "#D3DAD8", "#84B59F", "#CCE3DC"};

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            String sendero = obj.getString("sendero");
                            int cantidad = obj.getInt("cantidad");

                            LinearLayout fila = new LinearLayout(this);
                            fila.setOrientation(LinearLayout.HORIZONTAL);
                            fila.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                            fila.setPadding(0, 8, 0, 8);
                            fila.setGravity(Gravity.CENTER_VERTICAL);

                            TextView nombreSendero = new TextView(this);
                            nombreSendero.setText(sendero);
                            nombreSendero.setLayoutParams(new LinearLayout.LayoutParams(
                                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                            nombreSendero.setTextColor(Color.parseColor("#1C3B2D"));
                            nombreSendero.setTextSize(14);

                            LinearLayout barraContenedor = new LinearLayout(this);
                            barraContenedor.setOrientation(LinearLayout.HORIZONTAL);
                            barraContenedor.setGravity(Gravity.CENTER_VERTICAL);
                            barraContenedor.setLayoutParams(new LinearLayout.LayoutParams(
                                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 2f));

                            View barra = new View(this);
                            int ancho = (int) (200 * ((float) cantidad / maxCantidad));
                            LinearLayout.LayoutParams barraParams = new LinearLayout.LayoutParams(ancho, 20);
                            barraParams.setMargins(0, 0, 12, 0);
                            barra.setLayoutParams(barraParams);
                            barra.setBackgroundColor(Color.parseColor(colores[i % colores.length]));

                            TextView numero = new TextView(this);
                            numero.setText(String.valueOf(cantidad));
                            numero.setTextColor(Color.parseColor("#1C3B2D"));
                            numero.setTextSize(14);

                            barraContenedor.addView(barra);
                            barraContenedor.addView(numero);

                            fila.addView(nombreSendero);
                            fila.addView(barraContenedor);
                            layoutSenderos.addView(fila);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        queueSenderos.add(requestSenderos);
    }

    private void cargarCalificacionPromedio() {
        TextView txtCalificacionPromedio = findViewById(R.id.txtCalificacionPromedio);

        // Mapeo: Nombres de senderos a IDs (ajusta según tu base de datos)
        Map<String, Integer> senderoMap = new HashMap<>();
        senderoMap.put("Sendero Camarón", 1);
        senderoMap.put("Sendero Camino de Cruces", 2);

        String urlSenderosVal = "https://camino-cruces-backend-production.up.railway.app/api/dashboard/visitantes-por-sendero/";
        RequestQueue queueVal = Volley.newRequestQueue(this);

        JsonArrayRequest requestSenderosVal = new JsonArrayRequest(
                Request.Method.GET, urlSenderosVal, null,
                response -> {
                    try {
                        int totalSenderos = response.length();
                        if (totalSenderos == 0) {
                            txtCalificacionPromedio.setText("N/A");
                            return;
                        }

                        final double[] sumaValoraciones = {0.0};
                        final int[] totalComentarios = {0};
                        final int[] senderosProcessed = {0};

                        for (int i = 0; i < totalSenderos; i++) {
                            JSONObject obj = response.getJSONObject(i);
                            String nombreSendero = obj.getString("sendero");

                            Integer senderoId = senderoMap.get(nombreSendero);

                            if (senderoId == null) {
                                senderosProcessed[0]++;
                                if (senderosProcessed[0] == totalSenderos) {
                                    finalizarCalculoComentarios(txtCalificacionPromedio, sumaValoraciones[0], totalComentarios[0]);
                                }
                                continue;
                            }

                            String urlComentarios = "https://camino-cruces-backend-production.up.railway.app/api/comentarios/sendero/" + senderoId + "/";

                            JsonArrayRequest requestComentarios = new JsonArrayRequest(
                                    Request.Method.GET, urlComentarios, null,
                                    comentariosResponse -> {
                                        try {
                                            double sumaThisSendero = 0;
                                            int countThisSendero = 0;

                                            for (int j = 0; j < comentariosResponse.length(); j++) {
                                                JSONObject comentario = comentariosResponse.getJSONObject(j);
                                                if (comentario.has("valoracion")) {
                                                    int valoracion = comentario.getInt("valoracion");
                                                    if (valoracion > 0) {
                                                        sumaThisSendero += valoracion;
                                                        countThisSendero++;
                                                    }
                                                }
                                            }

                                            if (countThisSendero > 0) {
                                                sumaValoraciones[0] += sumaThisSendero;
                                                totalComentarios[0] += countThisSendero;
                                            }

                                            senderosProcessed[0]++;

                                            if (senderosProcessed[0] == totalSenderos) {
                                                finalizarCalculoComentarios(txtCalificacionPromedio, sumaValoraciones[0], totalComentarios[0]);
                                            }

                                        } catch (JSONException e) {
                                            senderosProcessed[0]++;
                                            if (senderosProcessed[0] == totalSenderos) {
                                                finalizarCalculoComentarios(txtCalificacionPromedio, sumaValoraciones[0], totalComentarios[0]);
                                            }
                                        }
                                    },
                                    error -> {
                                        senderosProcessed[0]++;
                                        if (senderosProcessed[0] == totalSenderos) {
                                            finalizarCalculoComentarios(txtCalificacionPromedio, sumaValoraciones[0], totalComentarios[0]);
                                        }
                                    }
                            );

                            queueVal.add(requestComentarios);
                        }

                    } catch (JSONException e) {
                        txtCalificacionPromedio.setText("Error");
                    }
                },
                error -> txtCalificacionPromedio.setText("Error")
        );

        queueVal.add(requestSenderosVal);
    }

    private void finalizarCalculoComentarios(TextView txtCalificacionPromedio, double sumaTotal, int totalValoraciones) {
        if (totalValoraciones > 0) {
            double promedioGeneral = sumaTotal / totalValoraciones;
            txtCalificacionPromedio.setText(String.format(Locale.US, "%.1f/5", promedioGeneral));
        } else {
            txtCalificacionPromedio.setText("N/A");
        }
    }

    private void cargarVisitasRecientes() {
        String url = "https://camino-cruces-backend-production.up.railway.app/api/dashboard/visitas-recientes/";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        // Limpiar tabla antes de agregar nuevos datos
                        TableLayout tableVisitantes = findViewById(R.id.tableVisitantes);
                        // Mantener solo el header, remover las filas de datos
                        if (tableVisitantes.getChildCount() > 1) {
                            tableVisitantes.removeViews(1, tableVisitantes.getChildCount() - 1);
                        }

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject visita = response.getJSONObject(i);

                            String fecha = visita.optString("fecha", "N/A");
                            String nombre = visita.optString("nombre", "N/A");
                            int adulto = visita.optInt("adulto", 0);
                            int nino = visita.optInt("nino", 0);
                            String nacionalidad = visita.optString("nacionalidad", "N/A");
                            String motivo = visita.optString("motivo_visita", "N/A");
                            String sendero = visita.optString("sendero", "N/A");
                            String hora = visita.optString("hora_entrada", "N/A");
                            String telefono = visita.optString("telefono", "N/A");

                            agregarVisitanteATabla(fecha, nombre, adulto, nino, nacionalidad, motivo, sendero, hora, telefono);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        queue.add(request);
    }

    private void agregarVisitanteATabla(String fecha, String nombre, int adultos, int ninos,
                                        String nacionalidad, String motivo, String sendero,
                                        String hora, String telefono) {
        TableLayout tableVisitantes = findViewById(R.id.tableVisitantes);

        TableRow fila = new TableRow(this);
        fila.setPadding(8, 8, 8, 8);

        fila.addView(crearCelda(fecha));
        fila.addView(crearCelda(nombre));
        fila.addView(crearCelda(String.valueOf(adultos)));
        fila.addView(crearCelda(String.valueOf(ninos)));
        fila.addView(crearCelda(nacionalidad));
        fila.addView(crearCelda(motivo));
        fila.addView(crearCelda(sendero));
        fila.addView(crearCelda(hora));
        fila.addView(crearCelda(telefono));

        tableVisitantes.addView(fila);
    }

    private TextView crearCelda(String texto) {
        TextView tv = new TextView(this);
        tv.setText(texto);
        tv.setPadding(8, 0, 8, 0);
        tv.setTextSize(12);
        return tv;
    }

    private void mostrarDialogoSalir() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_salir, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        ImageView btnCerrar = dialogView.findViewById(R.id.btn_cerrar);
        btnCerrar.setOnClickListener(view -> alertDialog.dismiss());

        Button btnConfirmarSalir = dialogView.findViewById(R.id.btn_salir);
        btnConfirmarSalir.setOnClickListener(view -> {
            alertDialog.dismiss();
            finishAffinity();
        });
    }

}
package com.example.proyectosemestral;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

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

        // Estadísticas superiores
        TextView txtVisitantesHoy = findViewById(R.id.txtVisitantesHoy);
        TextView txtEncuestaCompletadas = findViewById(R.id.txtEncuestaCompletadas);

        // Navegación de botones
        btnRegistro.setOnClickListener(v -> startActivity(new Intent(this, RegistroActivity.class)));
        btnEncuesta.setOnClickListener(v -> startActivity(new Intent(this, EncuestaActivity.class)));
        btnDashboard.setOnClickListener(v -> {
            // Ya estamos en Dashboard
        });

        // -------------------------
        LinearLayout layoutPaises = findViewById(R.id.layoutPaises);
        TextView verTodoPais = findViewById(R.id.verTodoPais);
        int cantidadVisible = 5;

        String urlPaises = "https://camino-cruces-backend-production.up.railway.app/api/dashboard/visitantes-por-pais/";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest requestPaises = new JsonArrayRequest(
                Request.Method.GET, urlPaises, null,
                response -> {
                    try {
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

                        verTodoPais.setOnClickListener(v -> {
                            for (int i = cantidadVisible; i < layoutPaises.getChildCount(); i++) {
                                layoutPaises.getChildAt(i).setVisibility(View.VISIBLE);
                            }
                            verTodoPais.setVisibility(View.GONE);
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                }
        );

        queue.add(requestPaises);

        // -------------------------
        // Gráfico: Visitantes por sendero
        // -------------------------
        LinearLayout layoutSenderos = findViewById(R.id.layoutSenderos);
        String urlSenderos = "https://camino-cruces-backend-production.up.railway.app/api/dashboard/visitantes-por-sendero/";

        RequestQueue queueSenderos = Volley.newRequestQueue(this);

        JsonArrayRequest requestSenderos = new JsonArrayRequest(
                Request.Method.GET, urlSenderos, null,
                response -> {
                    try {
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

        // -------------------------
        // Calificación Promedio General
        // -------------------------
        TextView txtCalificacionPromedio = findViewById(R.id.txtCalificacionPromedio); // Asegúrate de tener este TextView en el XML

        String urlSenderosVal = "https://camino-cruces-backend-production.up.railway.app/api/dashboard/visitantes-por-sendero/";
        RequestQueue queueVal = Volley.newRequestQueue(this);

        JsonArrayRequest requestSenderosVal = new JsonArrayRequest(
                Request.Method.GET, urlSenderosVal, null,
                response -> {
                    try {
                        int totalSenderos = response.length();
                        final double[] sumaValoraciones = {0.0};
                        final int[] conteo = {0};

                        for (int i = 0; i < totalSenderos; i++) {
                            JSONObject obj = response.getJSONObject(i);
                            int senderoId = obj.getInt("id"); // IMPORTANTE: asegúrate que el backend devuelve 'id'

                            String urlValoracion = "https://camino-cruces-backend-production.up.railway.app/api/valoracion-promedio/" + senderoId + "/";
                            JsonObjectRequest requestValoracion = new JsonObjectRequest(
                                    Request.Method.GET, urlValoracion, null,
                                    valoracionResponse -> {
                                        try {
                                            double promedio = valoracionResponse.getDouble("valoracion_promedio");
                                            sumaValoraciones[0] += promedio;
                                            conteo[0]++;

                                            if (conteo[0] == totalSenderos) {
                                                double promedioGeneral = sumaValoraciones[0] / conteo[0];
                                                txtCalificacionPromedio.setText(String.format(Locale.US, "%.1f/5", promedioGeneral));
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    },
                                    error -> error.printStackTrace()
                            );

                            queueVal.add(requestValoracion);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        queueVal.add(requestSenderosVal);


        cargarVisitasRecientes();
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

    private void cargarVisitasRecientes() {
        String url = "https://camino-cruces-backend-production.up.railway.app/dashboard/visitas-recientes/";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject visita = response.getJSONObject(i);

                            String fecha = visita.getString("fecha");
                            String nombre = visita.getString("nombre");
                            int adulto = visita.getInt("adulto");
                            int nino = visita.getInt("nino");
                            String nacionalidad = visita.getString("nacionalidad");
                            String motivo = visita.getString("motivo_visita");
                            String sendero = visita.getString("sendero");
                            String hora = visita.getString("hora_entrada");
                            String telefono = visita.getString("telefono");

                            agregarVisitanteATabla(fecha, nombre, adulto, nino, nacionalidad, motivo, sendero, hora, telefono);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                }
        );

        queue.add(request);
    }
}
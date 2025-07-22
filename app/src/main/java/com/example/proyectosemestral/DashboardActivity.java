package com.example.proyectosemestral;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    // Botones de navegación
    LinearLayout btnRegistro, btnEncuesta, btnDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Referencias a botones inferiores
        btnRegistro = findViewById(R.id.btnRegistro);
        btnEncuesta = findViewById(R.id.btnEncuesta);
        btnDashboard = findViewById(R.id.btnDashboard);

        // Estadísticas superiores
        TextView txtVisitantesHoy = findViewById(R.id.txtVisitantesHoy);
        TextView txtEncuestaPromedio = findViewById(R.id.txtEncuestaPromedio);
        TextView txtEncuestaCompletadas = findViewById(R.id.txtEncuestaCompletadas);

        txtVisitantesHoy.setText(String.valueOf(25));
        txtEncuestaPromedio.setText(String.valueOf(4.8));
        txtEncuestaCompletadas.setText(String.valueOf(20));

        // Navegación de botones
        btnRegistro.setOnClickListener(v -> startActivity(new Intent(this, RegistroActivity.class)));
        btnEncuesta.setOnClickListener(v -> startActivity(new Intent(this, EncuestaActivity.class)));
        btnDashboard.setOnClickListener(v -> {
            // Ya estamos en Dashboard
        });

        // Tabla de visitantes recientes
        agregarVisitanteATabla("2025-07-22", "Juan Pérez", 2, 1, "Panamá", "Turismo", "Sendero A", "10:00", "12345678");
        agregarVisitanteATabla("2025-07-21", "Ana Gómez", 1, 0, "Colombia", "Investigación", "Sendero B", "11:30", "87654321");
        agregarVisitanteATabla("2025-07-20", "Luis Rodríguez", 3, 2, "USA", "Educación", "Sendero C", "09:15", "11223344");

        // -------------------------
        // Gráfico: Visitantes por país (Top 5)
        // -------------------------

        String[] paises = {"Panamá", "USA", "Colombia", "Costa Rica", "Perú", "Chile", "Argentina", "México"};
        int[] visitantes = {40, 35, 30, 25, 20, 18, 15, 12};
        int cantidadVisible = 5;

        LinearLayout layoutPaises = findViewById(R.id.layoutPaises);
        TextView verTodoPais = findViewById(R.id.verTodoPais);

        for (int i = 0; i < paises.length; i++) {
            LinearLayout fila = new LinearLayout(this);
            fila.setOrientation(LinearLayout.HORIZONTAL);
            fila.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            fila.setPadding(0, 8, 0, 8);
            fila.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);

            // Nombre del país
            TextView pais = new TextView(this);
            pais.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            pais.setText(paises[i]);
            pais.setTextSize(14);
            pais.setTextColor(getResources().getColor(android.R.color.black));

            // Cantidad de visitantes
            TextView cantidad = new TextView(this);
            cantidad.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            cantidad.setText(String.valueOf(visitantes[i]));
            cantidad.setTextSize(14);
            cantidad.setTextColor(getResources().getColor(android.R.color.black));

            fila.addView(pais);
            fila.addView(cantidad);

            // Ocultar inicialmente los que superan el top 5
            if (i >= cantidadVisible) fila.setVisibility(View.GONE);

            layoutPaises.addView(fila);
        }

        // Mostrar todos los países al hacer clic
        verTodoPais.setOnClickListener(v -> {
            for (int i = cantidadVisible; i < layoutPaises.getChildCount(); i++) {
                layoutPaises.getChildAt(i).setVisibility(View.VISIBLE);
            }
            verTodoPais.setVisibility(View.GONE);
        });

        // -------------------------
        // Gráfico: Visitantes por sendero
        // -------------------------

        String[] senderos = {"El camarón", "El pescador", "Cruces", "El Búho", "Ciclovía"};
        int[] cantidades = {15, 10, 8, 6, 4};
        int maxCantidad = 15;
        String[] colores = {"#0B3D2E", "#145C47", "#6B8E89", "#A0BFB9", "#D3DAD8"};

        LinearLayout layoutSenderos = findViewById(R.id.layoutSenderos);

        for (int i = 0; i < senderos.length; i++) {
            LinearLayout fila = new LinearLayout(this);
            fila.setOrientation(LinearLayout.HORIZONTAL);
            fila.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            fila.setPadding(0, 8, 0, 8);
            fila.setGravity(Gravity.CENTER_VERTICAL);

            // Nombre del sendero
            TextView nombreSendero = new TextView(this);
            nombreSendero.setText(senderos[i]);
            nombreSendero.setLayoutParams(new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            nombreSendero.setTextColor(Color.parseColor("#1C3B2D"));
            nombreSendero.setTextSize(14);

            // Barra visual proporcional
            LinearLayout barraContenedor = new LinearLayout(this);
            barraContenedor.setOrientation(LinearLayout.HORIZONTAL);
            barraContenedor.setGravity(Gravity.CENTER_VERTICAL);
            barraContenedor.setLayoutParams(new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 2f));

            View barra = new View(this);
            int ancho = (int) (200 * ((float) cantidades[i] / maxCantidad));
            LinearLayout.LayoutParams barraParams = new LinearLayout.LayoutParams(ancho, 20);
            barraParams.setMargins(0, 0, 12, 0);
            barra.setLayoutParams(barraParams);
            barra.setBackgroundColor(Color.parseColor(colores[i]));

            // Cantidad de visitantes al lado
            TextView numero = new TextView(this);
            numero.setText(String.valueOf(cantidades[i]));
            numero.setTextColor(Color.parseColor("#1C3B2D"));
            numero.setTextSize(14);

            barraContenedor.addView(barra);
            barraContenedor.addView(numero);

            fila.addView(nombreSendero);
            fila.addView(barraContenedor);
            layoutSenderos.addView(fila);
        }
    }

    /**
     * Agrega un visitante a la tabla de visitantes recientes.
     */
    private void agregarVisitanteATabla(String fecha, String nombre, int adultos, int ninos,
                                        String nacionalidad, String motivo, String sendero,
                                        String hora, String telefono) {
        TableLayout tableVisitantes = findViewById(R.id.tableVisitantes);

        TableRow fila = new TableRow(this);
        fila.setPadding(8, 8, 8, 8);

        // Celdas
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

    /**
     * Crea un TextView configurado para usar como celda de tabla.
     */
    private TextView crearCelda(String texto) {
        TextView tv = new TextView(this);
        tv.setText(texto);
        tv.setPadding(8, 0, 8, 0);
        return tv;
    }
}


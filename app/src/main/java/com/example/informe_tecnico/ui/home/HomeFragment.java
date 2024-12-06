package com.example.informe_tecnico.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.informe_tecnico.databinding.FragmentHomeBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private BarChart barChart;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Referenciar el BarChart desde el XML
        barChart = binding.barChart;

        // Llenar los datos del gráfico
        loadChartData();

        return root;
    }

    private void loadChartData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Nombres de las colecciones en Firestore
        String[] collections = {"Area", "Sede", "Falla", "OtrasActividades", "Tipo_Equipo"};
        Map<String, Integer> collectionCounts = new HashMap<>();

        // Inicializar contador para cada colección
        for (String collection : collections) {
            collectionCounts.put(collection, 0);
        }

        // Leer la cantidad de documentos de cada colección
        for (String collection : collections) {
            db.collection(collection).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    int count = 0;
                    for (QueryDocumentSnapshot ignored : task.getResult()) {
                        count++;
                    }

                    // Actualizar el conteo para la colección
                    collectionCounts.put(collection, count);

                    // Actualizar el gráfico una vez se hayan procesado todas las colecciones
                    if (collectionCounts.size() == collections.length) {
                        updateChart(collectionCounts);
                    }
                } else {
                    // Manejar errores si es necesario
                    System.err.println("Error leyendo colección " + collection);
                }
            });
        }
    }

    private void updateChart(Map<String, Integer> collectionCounts) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        // Definir colores para cada barra
        int[] barColors = {
                Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA
        };

        int index = 0;
        for (Map.Entry<String, Integer> entry : collectionCounts.entrySet()) {
            entries.add(new BarEntry(index, entry.getValue(), entry.getKey())); // El tercer parámetro almacena el nombre (tooltip)
            labels.add(entry.getKey());
            colors.add(barColors[index % barColors.length]); // Ciclar colores si hay más colecciones
            index++;
        }

        // Crear el conjunto de datos para el gráfico
        BarDataSet barDataSet = new BarDataSet(entries, "");
        barDataSet.setColors(colors);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(12f);

        // Formatear los valores como enteros
        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value); // Convertir a entero
            }
        });

        // Configurar el gráfico con los datos
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        // Personalizar el eje X (mostrar etiquetas personalizadas)
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f); // Espaciado de 1 unidad
        xAxis.setGranularityEnabled(true);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value >= 0 && value < labels.size()) {
                    return labels.get((int) value); // Etiqueta de la colección
                }
                return "";
            }
        });
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Colocar etiquetas abajo
        xAxis.setDrawGridLines(false); // Eliminar líneas de cuadrícula

        // Personalizar el eje Y
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(0f); // Valores a partir de 0
        barChart.getAxisRight().setEnabled(false); // Ocultar eje derecho

        // Personalizar la leyenda para cada barra
        Legend legend = barChart.getLegend();
        legend.setEnabled(false); // Desactivar leyenda global

        // Configuración final del gráfico
        barChart.getDescription().setText(""); // Sin descripción global
        barChart.setExtraBottomOffset(10f); // Espaciado adicional en la parte inferior
        barChart.animateY(1000); // Animación
        barChart.invalidate(); // Refrescar el gráfico
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
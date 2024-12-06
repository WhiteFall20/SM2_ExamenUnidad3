package com.example.informe_tecnico.ui.o_actividades;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.informe_tecnico.R;
import com.example.informe_tecnico.databinding.FragmentOtrasActividadesBinding;
import com.example.informe_tecnico.ui.o_actividades.OtrasActividades;
import com.example.informe_tecnico.ui.o_actividades.OtrasActividadesAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OtrasActividadesFragment extends Fragment {

    private FragmentOtrasActividadesBinding binding;
    private EditText editTextNombreOtrasActividades, editTextDescripcionOtrasActividades;
    private Button buttonAdd, buttonEliminate;
    private RecyclerView recyclerViewOtrasActividades;
    private OtrasActividadesAdapter otrasActividadesAdapter;
    private List<OtrasActividades> otrasActividadesList;
    private FirebaseFirestore db;
    private OtrasActividades actividadSeleccionada;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOtrasActividadesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Inicializar Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Inicializar las vistas
        editTextNombreOtrasActividades = binding.editTextNombreOtrasActividades;
        editTextDescripcionOtrasActividades = binding.editTextDescripcionOtrasActividades;
        buttonAdd = binding.buttonAdd;
        buttonEliminate = binding.buttonEliminate;
        recyclerViewOtrasActividades = binding.recyclerViewOtrasActividades;

        // Inicializar el RecyclerView
        recyclerViewOtrasActividades.setLayoutManager(new LinearLayoutManager(getContext()));
        otrasActividadesList = new ArrayList<>();
        otrasActividadesAdapter = new OtrasActividadesAdapter(otrasActividadesList, this::onActividadSelected);
        recyclerViewOtrasActividades.setAdapter(otrasActividadesAdapter);

        // Cargar otras actividades desde Firestore
        cargarOtrasActividades();

        // Manejar botón de agregar/modificar
        buttonAdd.setOnClickListener(v -> agregarOModificarActividad());

        // Manejar botón de eliminar
        buttonEliminate.setOnClickListener(v -> eliminarActividad());

        return root;
    }

    // Método para cargar las otras actividades desde Firebase Firestore
    private void cargarOtrasActividades() {
        db.collection("OtrasActividades").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    otrasActividadesList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        OtrasActividades actividad = document.toObject(OtrasActividades.class);
                        actividad.setId(document.getId());
                        otrasActividadesList.add(actividad);
                    }
                    otrasActividadesAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al cargar actividades", Toast.LENGTH_SHORT).show());
    }

    // Método para agregar o modificar una actividad
    private void agregarOModificarActividad() {
        String nombreActividad = editTextNombreOtrasActividades.getText().toString().trim();
        String descripcionActividad = editTextDescripcionOtrasActividades.getText().toString().trim();

        if (TextUtils.isEmpty(nombreActividad) || TextUtils.isEmpty(descripcionActividad)) {
            Toast.makeText(getContext(), "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> actividadData = new HashMap<>();
        actividadData.put("Nombre_OtrasActividades", nombreActividad);
        actividadData.put("Descripcion", descripcionActividad);

        if (actividadSeleccionada != null) {
            // Modificar la actividad existente
            db.collection("OtrasActividades").document(actividadSeleccionada.getId())
                    .update(actividadData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Actividad actualizada", Toast.LENGTH_SHORT).show();
                        limpiarCampos();
                        cargarOtrasActividades();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al actualizar actividad", Toast.LENGTH_SHORT).show());
        } else {
            // Agregar nueva actividad
            db.collection("OtrasActividades").add(actividadData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "Actividad agregada", Toast.LENGTH_SHORT).show();
                        limpiarCampos();
                        cargarOtrasActividades();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al agregar actividad", Toast.LENGTH_SHORT).show());
        }
    }

    // Método para eliminar una actividad
    private void eliminarActividad() {
        if (actividadSeleccionada != null) {
            db.collection("OtrasActividades").document(actividadSeleccionada.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Actividad eliminada", Toast.LENGTH_SHORT).show();
                        limpiarCampos();
                        cargarOtrasActividades();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al eliminar actividad", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(getContext(), "Selecciona una actividad para eliminar", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para seleccionar una actividad del RecyclerView
    private void onActividadSelected(OtrasActividades actividad) {
        editTextNombreOtrasActividades.setText(actividad.getNombre_OtrasActividades());
        editTextDescripcionOtrasActividades.setText(actividad.getDescripcion());
        actividadSeleccionada = actividad;
    }

    // Método para limpiar los campos y deseleccionar actividad
    private void limpiarCampos() {
        editTextNombreOtrasActividades.setText("");
        editTextDescripcionOtrasActividades.setText("");
        actividadSeleccionada = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


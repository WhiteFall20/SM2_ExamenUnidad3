package com.example.informe_tecnico.ui.sede;

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
import com.example.informe_tecnico.databinding.FragmentSedeBinding;
import com.example.informe_tecnico.ui.sede.Sede;
import com.example.informe_tecnico.ui.sede.SedeAdapter;
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

public class SedeFragment extends Fragment {

    private FragmentSedeBinding binding;
    private EditText editTextNombreArea, editTextDescripcionArea;
    private Button buttonAdd, buttonEliminate;
    private RecyclerView recyclerViewSedes;
    private SedeAdapter sedeAdapter;
    private List<Sede> sedeList;
    private FirebaseFirestore db;
    private Sede sedeSeleccionada;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSedeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Inicializar Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Inicializar las vistas
        editTextNombreArea = binding.editTextNombreArea;
        editTextDescripcionArea = binding.editTextDescripcionArea;
        buttonAdd = binding.buttonAdd;
        buttonEliminate = binding.buttonEliminate;
        recyclerViewSedes = binding.recyclerViewSedes;

        // Inicializar el RecyclerView
        recyclerViewSedes.setLayoutManager(new LinearLayoutManager(getContext()));
        sedeList = new ArrayList<>();
        sedeAdapter = new SedeAdapter(sedeList, this::onSedeSelected);
        recyclerViewSedes.setAdapter(sedeAdapter);

        // Cargar sedes desde Firestore
        cargarSedes();

        // Manejar botón de agregar/modificar
        buttonAdd.setOnClickListener(v -> agregarOModificarSede());

        // Manejar botón de eliminar
        buttonEliminate.setOnClickListener(v -> eliminarSede());

        return root;
    }

    // Método para cargar las sedes desde Firebase Firestore
    private void cargarSedes() {
        db.collection("Sede").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    sedeList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Sede sede = document.toObject(Sede.class);
                        sede.setId(document.getId());
                        sedeList.add(sede);
                    }
                    sedeAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al cargar las sedes", Toast.LENGTH_SHORT).show());
    }

    // Método para agregar o modificar una sede
    private void agregarOModificarSede() {
        String nombreSede = editTextNombreArea.getText().toString().trim();
        String descripcionSede = editTextDescripcionArea.getText().toString().trim();

        if (TextUtils.isEmpty(nombreSede) || TextUtils.isEmpty(descripcionSede)) {
            Toast.makeText(getContext(), "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> sedeData = new HashMap<>();
        sedeData.put("Nombre_Sede", nombreSede);
        sedeData.put("Descripcion", descripcionSede);

        if (sedeSeleccionada != null) {
            // Modificar la sede existente
            db.collection("Sede").document(sedeSeleccionada.getId())
                    .update(sedeData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Sede actualizada", Toast.LENGTH_SHORT).show();
                        limpiarCampos();
                        cargarSedes();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al actualizar sede", Toast.LENGTH_SHORT).show());
        } else {
            // Agregar nueva sede
            db.collection("Sede").add(sedeData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "Sede agregada", Toast.LENGTH_SHORT).show();
                        limpiarCampos();
                        cargarSedes();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al agregar sede", Toast.LENGTH_SHORT).show());
        }
    }

    // Método para eliminar una sede
    private void eliminarSede() {
        if (sedeSeleccionada != null) {
            db.collection("Sede").document(sedeSeleccionada.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Sede eliminada", Toast.LENGTH_SHORT).show();
                        limpiarCampos();
                        cargarSedes();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al eliminar sede", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(getContext(), "Selecciona una sede para eliminar", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para seleccionar una sede del RecyclerView
    private void onSedeSelected(Sede sede) {
        editTextNombreArea.setText(sede.getNombre_Sede());
        editTextDescripcionArea.setText(sede.getDescripcion());
        sedeSeleccionada = sede;
    }

    // Método para limpiar los campos y deseleccionar sede
    private void limpiarCampos() {
        editTextNombreArea.setText("");
        editTextDescripcionArea.setText("");
        sedeSeleccionada = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

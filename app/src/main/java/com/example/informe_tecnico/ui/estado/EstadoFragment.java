package com.example.informe_tecnico.ui.estado;

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
import com.example.informe_tecnico.databinding.FragmentEstadoBinding;
import com.example.informe_tecnico.ui.estado.Estado;
import com.example.informe_tecnico.ui.estado.EstadoAdapter;
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

public class EstadoFragment extends Fragment {

    private FragmentEstadoBinding binding;
    private EditText editTextNombreEstado, editTextDescripcionEstado;
    private Button buttonAdd, buttonEliminate;
    private RecyclerView recyclerViewEstados;
    private EstadoAdapter estadoAdapter;
    private List<Estado> estadoList;
    private FirebaseFirestore db;
    private Estado estadoSeleccionado;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEstadoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Inicializar Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Inicializar las vistas
        editTextNombreEstado = binding.editTextNombreEstado;
        editTextDescripcionEstado = binding.editTextDescripcionEstado;
        buttonAdd = binding.buttonAdd;
        buttonEliminate = binding.buttonEliminate;
        recyclerViewEstados = binding.recyclerViewEstados;

        // Inicializar el RecyclerView
        recyclerViewEstados.setLayoutManager(new LinearLayoutManager(getContext()));
        estadoList = new ArrayList<>();
        estadoAdapter = new EstadoAdapter(estadoList, this::onEstadoSelected);
        recyclerViewEstados.setAdapter(estadoAdapter);

        // Cargar estados desde Firestore
        cargarEstados();

        // Manejar botón de agregar/modificar
        buttonAdd.setOnClickListener(v -> agregarOModificarEstado());

        // Manejar botón de eliminar
        buttonEliminate.setOnClickListener(v -> eliminarEstado());

        return root;
    }

    // Método para cargar los estados desde Firebase Firestore
    private void cargarEstados() {
        db.collection("Estado").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    estadoList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Estado estado = document.toObject(Estado.class);
                        estado.setId(document.getId());
                        estadoList.add(estado);
                    }
                    estadoAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al cargar los estados", Toast.LENGTH_SHORT).show());
    }

    // Método para agregar o modificar un estado
    private void agregarOModificarEstado() {
        String nombreEstado = editTextNombreEstado.getText().toString().trim();
        String descripcionEstado = editTextDescripcionEstado.getText().toString().trim();

        if (TextUtils.isEmpty(nombreEstado) || TextUtils.isEmpty(descripcionEstado)) {
            Toast.makeText(getContext(), "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> estadoData = new HashMap<>();
        estadoData.put("Nombre_Estado", nombreEstado);
        estadoData.put("Descripcion", descripcionEstado);

        if (estadoSeleccionado != null) {
            // Modificar el estado existente
            db.collection("Estado").document(estadoSeleccionado.getId())
                    .update(estadoData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Estado actualizado", Toast.LENGTH_SHORT).show();
                        limpiarCampos();
                        cargarEstados();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al actualizar estado", Toast.LENGTH_SHORT).show());
        } else {
            // Agregar nuevo estado
            db.collection("Estado").add(estadoData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "Estado agregado", Toast.LENGTH_SHORT).show();
                        limpiarCampos();
                        cargarEstados();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al agregar estado", Toast.LENGTH_SHORT).show());
        }
    }

    // Método para eliminar un estado
    private void eliminarEstado() {
        if (estadoSeleccionado != null) {
            db.collection("Estado").document(estadoSeleccionado.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Estado eliminado", Toast.LENGTH_SHORT).show();
                        limpiarCampos();
                        cargarEstados();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al eliminar estado", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(getContext(), "Selecciona un estado para eliminar", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para seleccionar un estado del RecyclerView
    private void onEstadoSelected(Estado estado) {
        editTextNombreEstado.setText(estado.getNombre_Estado());
        editTextDescripcionEstado.setText(estado.getDescripcion());
        estadoSeleccionado = estado;
    }

    // Método para limpiar los campos y deseleccionar estado
    private void limpiarCampos() {
        editTextNombreEstado.setText("");
        editTextDescripcionEstado.setText("");
        estadoSeleccionado = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


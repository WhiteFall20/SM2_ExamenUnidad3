package com.example.informe_tecnico.ui.tipo_equipo;

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
import com.example.informe_tecnico.databinding.FragmentTipoEquipoBinding;
import com.example.informe_tecnico.ui.tipo_equipo.Tipo_Equipo;
import com.example.informe_tecnico.ui.tipo_equipo.Tipo_EquipoAdapter;
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

public class Tipo_EquipoFragment extends Fragment {

    private FragmentTipoEquipoBinding binding;
    private EditText editTextNombreTipoEquipo, editTextDescripcionTipoEquipo;
    private Button buttonAdd, buttonEliminate;
    private RecyclerView recyclerViewTipoEquipo;
    private Tipo_EquipoAdapter tipoEquipoAdapter;
    private List<Tipo_Equipo> tipoEquipoList;
    private FirebaseFirestore db;
    private Tipo_Equipo tipoEquipoSeleccionado;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTipoEquipoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Inicializar Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Inicializar las vistas
        editTextNombreTipoEquipo = binding.editTextNombreTipoEquipo;
        editTextDescripcionTipoEquipo = binding.editTextDescripcionTipoEquipo;
        buttonAdd = binding.buttonAdd;
        buttonEliminate = binding.buttonEliminate;
        recyclerViewTipoEquipo = binding.recyclerViewTipoEquipo;

        // Inicializar el RecyclerView
        recyclerViewTipoEquipo.setLayoutManager(new LinearLayoutManager(getContext()));
        tipoEquipoList = new ArrayList<>();
        tipoEquipoAdapter = new Tipo_EquipoAdapter(tipoEquipoList, this::onTipoEquipoSelected);
        recyclerViewTipoEquipo.setAdapter(tipoEquipoAdapter);

        // Cargar tipos de equipo desde Firestore
        cargarTiposEquipo();

        // Manejar botón de agregar/modificar
        buttonAdd.setOnClickListener(v -> agregarOModificarTipoEquipo());

        // Manejar botón de eliminar
        buttonEliminate.setOnClickListener(v -> eliminarTipoEquipo());

        return root;
    }

    // Método para cargar los tipos de equipo desde Firebase Firestore
    private void cargarTiposEquipo() {
        db.collection("Tipo_Equipo").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    tipoEquipoList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Tipo_Equipo tipo_equipo = document.toObject(Tipo_Equipo.class);
                        tipo_equipo.setId(document.getId());
                        tipoEquipoList.add(tipo_equipo);
                    }
                    tipoEquipoAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al cargar los tipos de equipo", Toast.LENGTH_SHORT).show());
    }

    // Método para agregar o modificar un tipo de equipo
    private void agregarOModificarTipoEquipo() {
        String nombreTipoEquipo = editTextNombreTipoEquipo.getText().toString().trim();
        String descripcionTipoEquipo = editTextDescripcionTipoEquipo.getText().toString().trim();

        if (TextUtils.isEmpty(nombreTipoEquipo) || TextUtils.isEmpty(descripcionTipoEquipo)) {
            Toast.makeText(getContext(), "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> tipoEquipoData = new HashMap<>();
        tipoEquipoData.put("Nombre_Tipo_Equipo", nombreTipoEquipo);
        tipoEquipoData.put("Descripcion", descripcionTipoEquipo);

        if (tipoEquipoSeleccionado != null) {
            // Modificar el tipo de equipo existente
            db.collection("Tipo_Equipo").document(tipoEquipoSeleccionado.getId())
                    .update(tipoEquipoData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Tipo de equipo actualizado", Toast.LENGTH_SHORT).show();
                        limpiarCampos();
                        cargarTiposEquipo();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al actualizar tipo de equipo", Toast.LENGTH_SHORT).show());
        } else {
            // Agregar nuevo tipo de equipo
            db.collection("Tipo_Equipo").add(tipoEquipoData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "Tipo de equipo agregado", Toast.LENGTH_SHORT).show();
                        limpiarCampos();
                        cargarTiposEquipo();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al agregar tipo de equipo", Toast.LENGTH_SHORT).show());
        }
    }

    // Método para eliminar un tipo de equipo
    private void eliminarTipoEquipo() {
        if (tipoEquipoSeleccionado != null) {
            db.collection("Tipo_Equipo").document(tipoEquipoSeleccionado.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Tipo de equipo eliminado", Toast.LENGTH_SHORT).show();
                        limpiarCampos();
                        cargarTiposEquipo();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al eliminar tipo de equipo", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(getContext(), "Selecciona un tipo de equipo para eliminar", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para seleccionar un tipo de equipo del RecyclerView
    private void onTipoEquipoSelected(Tipo_Equipo tipoEquipo) {
        editTextNombreTipoEquipo.setText(tipoEquipo.getNombre_Tipo_Equipo());
        editTextDescripcionTipoEquipo.setText(tipoEquipo.getDescripcion());
        tipoEquipoSeleccionado = tipoEquipo;
    }

    // Método para limpiar los campos y deseleccionar tipo de equipo
    private void limpiarCampos() {
        editTextNombreTipoEquipo.setText("");
        editTextDescripcionTipoEquipo.setText("");
        tipoEquipoSeleccionado = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


package com.example.informe_tecnico.ui.falla;

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
import com.example.informe_tecnico.databinding.FragmentFallaBinding;
import com.example.informe_tecnico.ui.falla.Falla;
import com.example.informe_tecnico.ui.falla.FallaAdapter;
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

public class FallaFragment extends Fragment {

    private FragmentFallaBinding binding; // Actualiza el nombre del binding
    private EditText editTextNombreFalla, editTextDescripcionFalla;
    private Button buttonAdd, buttonEliminate;
    private RecyclerView recyclerViewFallas;
    private FallaAdapter fallaAdapter;
    private List<Falla> fallaList;
    private FirebaseFirestore db;
    private Falla fallaSeleccionada;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFallaBinding.inflate(inflater, container, false); // Usa el nuevo binding
        View root = binding.getRoot();

        // Inicializar Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Inicializar las vistas
        editTextNombreFalla = binding.editTextNombreFalla; // Actualiza el nombre de los campos
        editTextDescripcionFalla = binding.editTextDescripcionFalla;
        buttonAdd = binding.buttonAdd;
        buttonEliminate = binding.buttonEliminate;
        recyclerViewFallas = binding.recyclerViewFalla;

        // Inicializar el RecyclerView
        recyclerViewFallas.setLayoutManager(new LinearLayoutManager(getContext()));
        fallaList = new ArrayList<>();
        fallaAdapter = new FallaAdapter(fallaList, this::onFallaSelected); // Usa el FallaAdapter
        recyclerViewFallas.setAdapter(fallaAdapter);

        // Cargar fallas desde Firestore
        cargarFallas();

        // Manejar botón de agregar/modificar
        buttonAdd.setOnClickListener(v -> agregarOModificarFalla());

        // Manejar botón de eliminar
        buttonEliminate.setOnClickListener(v -> eliminarFalla());

        return root;
    }

    // Método para cargar las fallas desde Firebase Firestore
    private void cargarFallas() {
        db.collection("Falla").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    fallaList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Falla falla = document.toObject(Falla.class);
                        falla.setId(document.getId());
                        fallaList.add(falla);
                    }
                    fallaAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al cargar las fallas", Toast.LENGTH_SHORT).show());
    }

    // Método para agregar o modificar una falla
    private void agregarOModificarFalla() {
        String nombreFalla = editTextNombreFalla.getText().toString().trim();
        String descripcionFalla = editTextDescripcionFalla.getText().toString().trim();

        if (TextUtils.isEmpty(nombreFalla) || TextUtils.isEmpty(descripcionFalla)) {
            Toast.makeText(getContext(), "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> fallaData = new HashMap<>();
        fallaData.put("Nombre_Falla", nombreFalla);
        fallaData.put("Descripcion", descripcionFalla);

        if (fallaSeleccionada != null) {
            // Modificar la falla existente
            db.collection("Falla").document(fallaSeleccionada.getId())
                    .update(fallaData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Falla actualizada", Toast.LENGTH_SHORT).show();
                        limpiarCampos();
                        cargarFallas();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al actualizar falla", Toast.LENGTH_SHORT).show());
        } else {
            // Agregar nueva falla
            db.collection("Falla").add(fallaData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "Falla agregada", Toast.LENGTH_SHORT).show();
                        limpiarCampos();
                        cargarFallas();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al agregar falla", Toast.LENGTH_SHORT).show());
        }
    }

    // Método para eliminar una falla
    private void eliminarFalla() {
        if (fallaSeleccionada != null) {
            db.collection("Falla").document(fallaSeleccionada.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Falla eliminada", Toast.LENGTH_SHORT).show();
                        limpiarCampos();
                        cargarFallas();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al eliminar falla", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(getContext(), "Selecciona una falla para eliminar", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para seleccionar una falla del RecyclerView
    private void onFallaSelected(Falla falla) {
        editTextNombreFalla.setText(falla.getNombre_Falla());
        editTextDescripcionFalla.setText(falla.getDescripcion());
        fallaSeleccionada = falla;
    }

    // Método para limpiar los campos y deseleccionar falla
    private void limpiarCampos() {
        editTextNombreFalla.setText("");
        editTextDescripcionFalla.setText("");
        fallaSeleccionada = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


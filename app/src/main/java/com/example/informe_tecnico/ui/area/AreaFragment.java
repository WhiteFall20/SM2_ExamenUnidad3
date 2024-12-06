package com.example.informe_tecnico.ui.area;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.informe_tecnico.R;
import com.example.informe_tecnico.ui.sede.Sede;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AreaFragment extends Fragment {

    private EditText editTextNombreArea, editTextDescripcionArea;
    private Spinner spinnerSedes;
    private Button buttonAddArea, buttonEliminate;
    private RecyclerView recyclerViewAreas;
    private FirebaseFirestore db;
    private List<Sede> listaSedes;
    private List<Area> listaAreas;
    private ArrayAdapter<String> adapterSpinner;
    private AreaAdapter areaAdapter;
    private Area areaSeleccionada;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_area, container, false);

        // Inicializar componentes
        editTextNombreArea = root.findViewById(R.id.editTextNombreArea);
        editTextDescripcionArea = root.findViewById(R.id.editTextDescripcionArea);
        spinnerSedes = root.findViewById(R.id.spinnerSedes);
        buttonAddArea = root.findViewById(R.id.buttonAddArea);
        buttonEliminate = root.findViewById(R.id.buttonEliminate);
        recyclerViewAreas = root.findViewById(R.id.recyclerViewAreas);

        // Inicializar Firebase Firestore
        db = FirebaseFirestore.getInstance();

        listaSedes = new ArrayList<>();
        listaAreas = new ArrayList<>();

        // Inicializar el spinner con las sedes
        cargarSedesEnSpinner();

        // Configurar el botón para agregar o actualizar áreas
        buttonAddArea.setOnClickListener(v -> agregarActualizarArea());

        // Manejar botón de eliminar
        buttonEliminate.setOnClickListener(v -> eliminarArea());

        // Inicializar el RecyclerView de áreas
        configurarRecyclerView();

        return root;
    }

    private void cargarSedesEnSpinner() {
        CollectionReference sedesRef = db.collection("Sede");
        sedesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> nombresSedes = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Sede sede = document.toObject(Sede.class);
                    sede.setId(document.getId()); // Establecer el ID de la sede
                    listaSedes.add(sede);
                    nombresSedes.add(sede.getNombre_Sede());
                }
                adapterSpinner = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, nombresSedes);
                adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSedes.setAdapter(adapterSpinner);
            } else {
                // Manejar el caso de error al cargar sedes
            }
        });
    }

    private void agregarActualizarArea() {
        String nombreArea = editTextNombreArea.getText().toString().trim();
        String descripcionArea = editTextDescripcionArea.getText().toString().trim();
        int posicionSedeSeleccionada = spinnerSedes.getSelectedItemPosition();

        // Validar si los campos están vacíos
        if (nombreArea.isEmpty() || descripcionArea.isEmpty() || posicionSedeSeleccionada == -1) {
            return; // Mostrar un mensaje de error si es necesario
        }

        // Obtener la sede seleccionada del Spinner
        Sede sedeSeleccionada = listaSedes.get(posicionSedeSeleccionada);

        if (areaSeleccionada == null) {
            // Crear una nueva área
            Area nuevaArea = new Area(nombreArea, descripcionArea, sedeSeleccionada.getNombre_Sede()); // Utiliza el nombre de la sede

            // Guardar el área en Firebase Firestore
            db.collection("Area").add(nuevaArea).addOnSuccessListener(documentReference -> {
                // Reiniciar los campos
                editTextNombreArea.setText("");
                editTextDescripcionArea.setText("");
                spinnerSedes.setSelection(0);

                // Recargar la lista de áreas
                cargarAreas();
            }).addOnFailureListener(e -> {
                // Manejar el caso de fallo al agregar el área
            });
        } else {
            // Actualizar el área seleccionada
            areaSeleccionada.setNombre_Area(nombreArea);
            areaSeleccionada.setDescripcion_Area(descripcionArea);
            areaSeleccionada.setSedeNombre(sedeSeleccionada.getNombre_Sede()); // Actualiza el nombre de la sede

            db.collection("Area").document(areaSeleccionada.getId())
                    .set(areaSeleccionada)
                    .addOnSuccessListener(aVoid -> {
                        // Reiniciar los campos después de actualizar
                        editTextNombreArea.setText("");
                        editTextDescripcionArea.setText("");
                        spinnerSedes.setSelection(0);
                        areaSeleccionada = null;

                        // Recargar la lista de áreas
                        cargarAreas();
                    })
                    .addOnFailureListener(e -> {
                        // Manejar el fallo de la actualización
                    });
        }
    }

    private void configurarRecyclerView() {
        // Inicializamos el AreaAdapter pasándole el listener que maneja la selección
        areaAdapter = new AreaAdapter(listaAreas, area -> {
            // Manejar la selección de un área en el RecyclerView
            areaSeleccionada = area;
            editTextNombreArea.setText(area.getNombre_Area());
            editTextDescripcionArea.setText(area.getDescripcion_Area());

            // Buscar la sede en el Spinner para seleccionar la correspondiente
            for (int i = 0; i < listaSedes.size(); i++) {
                if (listaSedes.get(i).getNombre_Sede().equals(area.getSedeNombre())) { // Comparar con el nombre de la sede
                    spinnerSedes.setSelection(i);
                    break;
                }
            }
        });

        recyclerViewAreas.setAdapter(areaAdapter);
        recyclerViewAreas.setLayoutManager(new LinearLayoutManager(getContext()));

        // Cargar las áreas almacenadas
        cargarAreas();
    }

    // Método para eliminar una area
    private void eliminarArea() {
        if (areaSeleccionada != null) {
            db.collection("Area").document(areaSeleccionada.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Estado eliminado", Toast.LENGTH_SHORT).show();
                        limpiarCampos();
                        cargarAreas();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al eliminar estado", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(getContext(), "Selecciona un estado para eliminar", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para limpiar los campos y deseleccionar estado
    private void limpiarCampos() {
        editTextNombreArea.setText("");
        editTextDescripcionArea.setText("");
        spinnerSedes.setSelection(0);
        areaSeleccionada = null;
    }

    private void cargarAreas() {
        CollectionReference areasRef = db.collection("Area");
        areasRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listaAreas.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Area area = document.toObject(Area.class);
                    area.setId(document.getId()); // Establecer el ID del área

                    // El nombre de la sede ya está almacenado en sedeNombre, no es necesario buscar el SedeID
                    listaAreas.add(area);
                }
                areaAdapter.notifyDataSetChanged();
            } else {
                // Manejar el caso de error al cargar las áreas
            }
        });
    }
}






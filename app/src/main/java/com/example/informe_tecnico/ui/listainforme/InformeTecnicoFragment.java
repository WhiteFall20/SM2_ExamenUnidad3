package com.example.informe_tecnico.ui.listainforme;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.example.informe_tecnico.ui.listainforme.InformeTecnico;
import com.example.informe_tecnico.ui.listainforme.InformeTecnicoAdapter;
import com.example.informe_tecnico.databinding.FragmentInformeTecnicoBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InformeTecnicoFragment extends Fragment {

    private FragmentInformeTecnicoBinding binding;
    private EditText editTextTitulo, editTextDiagnostico, editTextSolucionPrimaria, editTextDescripcionTipoEquipo,
            editTextSerieEquipo, editTextNombreEquipo, editTextCodigoPatrimonialEquipo, editTextMarcaEquipo,
            editTextCodigoInternoEquipo, editTextModeloEquipo, editTextColorEquipo, editTextMantenimiento, editTextObservaciones, editTextFechaSolicitud, editTextFechaInforme, editTextFechaIngresoEquipo;
    private Spinner spinnerAreas, spinnerSedes, spinnerEstado, spinnerTipoEquipo, spinnerFalla, spinnerOtrasActividades;
    private Button buttonAdd, buttonEliminate;
    private RecyclerView recyclerViewInformeTecnico;
    private InformeTecnicoAdapter informeTecnicoAdapter;
    private List<InformeTecnico> informeList;
    private FirebaseFirestore db;
    private InformeTecnico informeSeleccionado;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentInformeTecnicoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Inicializar Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Inicializar las vistas
        editTextTitulo = binding.editTextTitulo;
        editTextDiagnostico = binding.editTextDiagnostico;
        editTextSolucionPrimaria = binding.editTextSolucionPrimaria;
        editTextDescripcionTipoEquipo = binding.editTextDescripcionTipoEquipo;
        editTextSerieEquipo = binding.editTextSerieEquipo;
        editTextNombreEquipo = binding.editTextNombreEquipo;
        editTextCodigoPatrimonialEquipo = binding.editTextCodigoPatrimonialEquipo;
        editTextMarcaEquipo = binding.editTextMarcaEquipo;
        editTextCodigoInternoEquipo = binding.editTextCodigoInternoEquipo;
        editTextModeloEquipo = binding.editTextModeloEquipo;
        editTextColorEquipo = binding.editTextColorEquipo;
        editTextMantenimiento = binding.editTextMantenimiento;
        editTextObservaciones = binding.editTextObservaciones;
        editTextFechaSolicitud = binding.editTextFechaSolicitud;
        editTextFechaInforme = binding.editTextFechaInforme;
        editTextFechaIngresoEquipo = binding.editTextFechaIngresoEquipo;
        spinnerAreas = binding.spinnerAreas;
        spinnerSedes = binding.spinnerSedes;
        spinnerEstado = binding.spinnerEstado;
        spinnerTipoEquipo = binding.spinnerTipoEquipo;
        spinnerFalla = binding.spinnerFalla;
        spinnerOtrasActividades = binding.spinnerOtrasActividades;
        buttonAdd = binding.buttonAdd;
        buttonEliminate = binding.buttonEliminate;

        recyclerViewInformeTecnico = binding.recyclerViewInformeTecnico;

        // Inicializar RecyclerView
        recyclerViewInformeTecnico.setLayoutManager(new LinearLayoutManager(getContext()));
        informeList = new ArrayList<>();
        informeTecnicoAdapter = new InformeTecnicoAdapter(informeList, this::onInformeSelected);
        recyclerViewInformeTecnico.setAdapter(informeTecnicoAdapter);

        // Cargar los Spinners
        cargarSpinners();

        // Manejar el DatePicker para las fechas
        configurarDatePickers();

        // Cargar informes técnicos desde Firestore
        cargarInformesTecnicos();

        // Manejar botón de agregar/modificar
        buttonAdd.setOnClickListener(v -> agregarOModificarInforme());

        // Manejar botón de eliminar
        buttonEliminate.setOnClickListener(v -> eliminarInforme());


        return root;
    }

    // Método para cargar los Spinners con datos de Firebase
    private void cargarSpinners() {
        // Cargar datos en Spinner de Áreas desde Firebase
        db.collection("Area").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> areas = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        areas.add(document.getString("Nombre_Area"));
                    }
                    ArrayAdapter<String> adapterAreas = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, areas);
                    adapterAreas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerAreas.setAdapter(adapterAreas);
                });

        // Cargar datos en Spinner de Sedes
        db.collection("Sede").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> sedes = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        sedes.add(document.getString("Nombre_Sede"));
                    }
                    ArrayAdapter<String> adapterSedes = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, sedes);
                    adapterSedes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSedes.setAdapter(adapterSedes);
                });

        // Cargar datos en Spinner de Estado
        db.collection("Estado").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> estado = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        estado.add(document.getString("Nombre_Estado"));
                    }
                    ArrayAdapter<String> adapterEstado= new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, estado);
                    adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerEstado.setAdapter(adapterEstado);
                });

        // Cargar datos en Spinner de Tipo de Equipo
        db.collection("Tipo_Equipo").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> tipo_equipo = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        tipo_equipo.add(document.getString("Nombre_Tipo_Equipo"));
                    }
                    ArrayAdapter<String> adapterTipoEquipo= new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, tipo_equipo);
                    adapterTipoEquipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerTipoEquipo.setAdapter(adapterTipoEquipo);
                });
        // Cargar datos en Spinner de Falla
        db.collection("Falla").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> falla = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        falla.add(document.getString("Nombre_Falla"));
                    }
                    ArrayAdapter<String> adapterFalla= new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, falla);
                    adapterFalla.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerFalla.setAdapter(adapterFalla);
                });
        // Cargar datos en Spinner de Otras Actividades
        db.collection("OtrasActividades").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> otrasActividades = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        otrasActividades.add(document.getString("Nombre_OtrasActividades"));
                    }
                    ArrayAdapter<String> adapterOtrasActividades= new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, otrasActividades);
                    adapterOtrasActividades.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerOtrasActividades.setAdapter(adapterOtrasActividades);
                });
        // Aquí iría el código similar para otros spinners con datos relevantes.
    }

    // Configurar DatePickers para los campos de fechas
    private void configurarDatePickers() {
        editTextFechaSolicitud.setOnClickListener(v -> mostrarDatePicker(editTextFechaSolicitud));
        editTextFechaInforme.setOnClickListener(v -> mostrarDatePicker(editTextFechaInforme));
        editTextFechaIngresoEquipo.setOnClickListener(v -> mostrarDatePicker(editTextFechaIngresoEquipo));
    }

    // Mostrar DatePickerDialog para seleccionar una nueva fecha
    private void mostrarDatePicker(EditText editText) {
        final Calendar calendar = Calendar.getInstance();

        // Si el campo ya tiene una fecha establecida, parsearla y mostrarla en el DatePicker
        String currentText = editText.getText().toString();
        if (!currentText.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date date = sdf.parse(currentText);
                if (date != null) {
                    calendar.setTime(date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                    editText.setText(selectedDate);  // Establecer la nueva fecha seleccionada
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    // Método para cargar los informes técnicos desde Firebase Firestore
    private void cargarInformesTecnicos() {
        db.collection("Informe_Tecnico").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    informeList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        InformeTecnico informe = document.toObject(InformeTecnico.class);
                        informe.setId(document.getId());
                        informeList.add(informe);
                    }
                    informeTecnicoAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al cargar los informes", Toast.LENGTH_SHORT).show());
    }

    // Método para agregar o modificar un informe técnico
    private void agregarOModificarInforme() {
        String titulo = editTextTitulo.getText().toString().trim();
        String diagnostico = editTextDiagnostico.getText().toString().trim();
        String solucionPrimaria = editTextSolucionPrimaria.getText().toString().trim();
        String DescripcionTipoEquipo = editTextDescripcionTipoEquipo.getText().toString().trim();
        String SerieEquipo = editTextSerieEquipo.getText().toString().trim();
        String NombreEquipo = editTextNombreEquipo.getText().toString().trim();
        String CodigoPatrimonialEquipo = editTextCodigoPatrimonialEquipo.getText().toString().trim();
        String MarcaEquipo = editTextMarcaEquipo.getText().toString().trim();
        String CodigoInternoEquipo = editTextCodigoInternoEquipo.getText().toString().trim();
        String ModeloEquipo = editTextModeloEquipo.getText().toString().trim();
        String ColorEquipo = editTextColorEquipo.getText().toString().trim();
        String Mantenimiento = editTextMantenimiento.getText().toString().trim();
        String Observaciones = editTextObservaciones.getText().toString().trim();

        if (TextUtils.isEmpty(titulo) || TextUtils.isEmpty(diagnostico)) {
            Toast.makeText(getContext(), "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> informeData = new HashMap<>();
        informeData.put("Titulo", titulo);
        informeData.put("Diagnostico", diagnostico);
        informeData.put("Solucion_Primaria", solucionPrimaria);
        informeData.put("Tipo_Equipo", DescripcionTipoEquipo);
        informeData.put("Serie", SerieEquipo);
        informeData.put("Nombre_Equipo", NombreEquipo);
        informeData.put("Cod_Patrimonial", CodigoPatrimonialEquipo);
        informeData.put("Marca", MarcaEquipo);
        informeData.put("Cod_Interno", CodigoInternoEquipo);
        informeData.put("Modelo", ModeloEquipo);
        informeData.put("Color", ColorEquipo);
        informeData.put("Mantenimiento", Mantenimiento);
        informeData.put("Observaciones", Observaciones);
        informeData.put("Fecha_Solicitud", editTextFechaSolicitud.getText().toString());
        informeData.put("Fecha_Informe", editTextFechaInforme.getText().toString());
        informeData.put("Fecha_Ingreso", editTextFechaIngresoEquipo.getText().toString());
        informeData.put("areaNombre", spinnerAreas.getSelectedItem().toString());
        informeData.put("sedeNombre", spinnerSedes.getSelectedItem().toString());
        informeData.put("estadoNombre", spinnerEstado.getSelectedItem().toString());
        informeData.put("fallaNombre", spinnerFalla.getSelectedItem().toString());
        informeData.put("tipoEquipoNombre", spinnerTipoEquipo.getSelectedItem().toString());
        informeData.put("otrasActividadesNombre", spinnerOtrasActividades.getSelectedItem().toString());

        if (informeSeleccionado != null) {
            // Modificar el informe existente
            db.collection("Informe_Tecnico").document(informeSeleccionado.getId())
                    .update(informeData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Informe actualizado", Toast.LENGTH_SHORT).show();
                        limpiarCampos();
                        cargarInformesTecnicos();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al actualizar informe", Toast.LENGTH_SHORT).show());
        } else {
            // Agregar nuevo informe
            db.collection("Informe_Tecnico").add(informeData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "Informe agregado", Toast.LENGTH_SHORT).show();
                        limpiarCampos();
                        cargarInformesTecnicos();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al agregar informe", Toast.LENGTH_SHORT).show());
        }
    }

    // Método para eliminar un informe técnico
    private void eliminarInforme() {
        if (informeSeleccionado != null) {
            db.collection("Informe_Tecnico").document(informeSeleccionado.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Informe eliminado", Toast.LENGTH_SHORT).show();
                        limpiarCampos();
                        cargarInformesTecnicos();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al eliminar informe", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(getContext(), "Selecciona un informe para eliminar", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para seleccionar un informe técnico del RecyclerView
    private void onInformeSelected(InformeTecnico informe) {
        editTextTitulo.setText(informe.getTitulo());
        editTextDiagnostico.setText(informe.getDiagnostico());
        editTextSolucionPrimaria.setText(informe.getSolucion_Primaria());
        editTextDescripcionTipoEquipo.setText(informe.getTipo_Equipo());
        editTextSerieEquipo.setText(informe.getSerie());
        editTextNombreEquipo.setText(informe.getNombre_Equipo());
        editTextCodigoPatrimonialEquipo.setText(informe.getCod_Patrimonial());
        editTextMarcaEquipo.setText(informe.getMarca());
        editTextCodigoInternoEquipo.setText(informe.getCod_Interno());
        editTextModeloEquipo.setText(informe.getModelo());
        editTextColorEquipo.setText(informe.getColor());
        editTextMantenimiento.setText(informe.getMantenimiento());
        editTextObservaciones.setText(informe.getObservaciones());

        // Aquí se establecen las fechas originales del informe
        editTextFechaSolicitud.setText(informe.getFecha_Solicitud());
        editTextFechaInforme.setText(informe.getFecha_Informe());
        editTextFechaIngresoEquipo.setText(informe.getFecha_Ingreso());

        // Establecer valores de los spinners
        spinnerAreas.setSelection(getSpinnerIndex(spinnerAreas, informe.getAreaNombre()));
        spinnerSedes.setSelection(getSpinnerIndex(spinnerSedes, informe.getSedeNombre()));
        spinnerEstado.setSelection(getSpinnerIndex(spinnerEstado, informe.getEstadoNombre()));
        spinnerTipoEquipo.setSelection(getSpinnerIndex(spinnerTipoEquipo, informe.getTipoEquipoNombre()));
        spinnerFalla.setSelection(getSpinnerIndex(spinnerFalla, informe.getFallaNombre()));
        spinnerOtrasActividades.setSelection(getSpinnerIndex(spinnerOtrasActividades, informe.getOtrasActividadesNombre()));

        // Aquí puedes también actualizar los otros campos si lo deseas
        // Si lo que quieres es que solo al hacer click en "ver detalles" se vaya a la otra vista, puedes mantener solo el título
        informeSeleccionado = informe;
    }

    // Método para obtener el índice de un Spinner dado un valor
    private int getSpinnerIndex(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(value)) {
                return i;
            }
        }
        return 0;
    }

    // Método para limpiar los campos del formulario
    private void limpiarCampos() {
        editTextTitulo.setText("");
        editTextDiagnostico.setText("");
        editTextSolucionPrimaria.setText("");
        editTextDescripcionTipoEquipo.setText("");
        editTextSerieEquipo.setText("");
        editTextNombreEquipo.setText("");
        editTextCodigoPatrimonialEquipo.setText("");
        editTextMarcaEquipo.setText("");
        editTextCodigoInternoEquipo.setText("");
        editTextModeloEquipo.setText("");
        editTextColorEquipo.setText("");
        editTextMantenimiento.setText("");
        editTextObservaciones.setText("");
        editTextFechaSolicitud.setText("");
        editTextFechaInforme.setText("");
        editTextFechaIngresoEquipo.setText("");
        spinnerAreas.setSelection(0);
        spinnerSedes.setSelection(0);
        spinnerEstado.setSelection(0);
        spinnerTipoEquipo.setSelection(0);
        spinnerFalla.setSelection(0);
        spinnerOtrasActividades.setSelection(0);
        informeSeleccionado = null;
    }
}


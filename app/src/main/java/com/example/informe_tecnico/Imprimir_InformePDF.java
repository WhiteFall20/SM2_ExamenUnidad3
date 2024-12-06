package com.example.informe_tecnico;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//import android.graphics.pdf.PdfDocument;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.kernel.font.PdfFont;

import com.example.informe_tecnico.databinding.VerInformetecnicoBinding;

public class Imprimir_InformePDF extends Fragment {

    private VerInformetecnicoBinding binding;
    private Button btnVerInforme, btnImprimir;
    // Variable para almacenar la Uri de la imagen seleccionada
    private Uri selectedImageUri;
    private Spinner spinnerPlantillasPDF;
    private Spinner spinnerInformes;
    private ImageButton imageSelectLogoEmpresa;
    private FirebaseFirestore db;
    private TextView textTitulo, textDiagnostico, textSolucionPrimaria, textArea, textSede, textEstado,
            textTipoEquipo, textFalla, textOtrasActividades, textFechaSolicitud, textFechaInforme, textDesTipoEquipo,
            textSerieEquipo, textNombreEquipo, textCodPatriEquipo, textMarcaEquipo, textCodInterEquipo,
            textModeloEquipo, textColorEquipo, textMantenimiento, textObservaciones, textFechaIngreso;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inicializar el binding
        binding = VerInformetecnicoBinding.inflate(inflater, container, false);
        return binding.getRoot(); // Retornar la vista raíz del binding
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Inicializar los TextView usando findViewById
        textTitulo = binding.textTitulo;
        textDiagnostico = binding.textDiagnostico;
        textSolucionPrimaria = binding.textSolucionPrimaria;
        textArea = binding.textArea;
        textSede = binding.textSede;
        textEstado = binding.textEstado;
        textTipoEquipo = binding.textTipoEquipo;
        textFalla = binding.textFalla;
        textOtrasActividades = binding.textOtrasActividades;
        textFechaSolicitud = binding.textFechaSolicitud;
        textFechaInforme = binding.textFechaInforme;
        textDesTipoEquipo = binding.textDesTipoEquipo;
        textSerieEquipo = binding.textSerieEquipo;
        textNombreEquipo = binding.textNombreEquipo;
        textCodPatriEquipo = binding.textCodPatriEquipo;
        textMarcaEquipo = binding.textMarcaEquipo;
        textCodInterEquipo = binding.textCodInterEquipo;
        textModeloEquipo = binding.textModeloEquipo;
        textColorEquipo = binding.textColorEquipo;
        textMantenimiento = binding.textMantenimiento;
        textObservaciones = binding.textObservaciones;
        textFechaIngreso = binding.textFechaIngreso;

        // Inicializar botones y spinner
        btnVerInforme = getView().findViewById(R.id.buttonVerInforme);
        btnImprimir = getView().findViewById(R.id.buttonDownloadPDF);
        spinnerPlantillasPDF = getView().findViewById(R.id.spinnerPlantillasPDF);

        // Cargar títulos de informes en el Spinner
        cargarTitulosDeInformes();

        // Configurar el Spinner con una lista de plantillas
        List<String> plantillas = new ArrayList<>();
        plantillas.add("Plantilla estándar"); // Por ahora solo una opción
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, plantillas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlantillasPDF.setAdapter(adapter);

        // Acción del botón Ver Informe
        btnVerInforme.setOnClickListener(v -> {
            String informeSeleccionado = spinnerInformes.getSelectedItem().toString();
            if (!TextUtils.isEmpty(informeSeleccionado)) {
                obtenerInformePorTitulo(informeSeleccionado);
            } else {
                Toast.makeText(getContext(), "Selecciona un informe", Toast.LENGTH_SHORT).show();
            }
        });

        // Acción del botón Imprimir PDF
        btnImprimir.setOnClickListener(v -> {
            String plantillaSeleccionada = spinnerPlantillasPDF.getSelectedItem().toString();
            generarPDFConPlantilla(plantillaSeleccionada);
        });

        // Acción para seleccionar imagen desde la galería
        imageSelectLogoEmpresa.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });


    }

    private void cargarTitulosDeInformes() {
        // Obtener los títulos de los informes desde Firestore
        db.collection("informes")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> titulosInformes = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String titulo = document.getString("titulo");
                            titulosInformes.add(titulo);
                        }

                        // Configurar el Spinner con los títulos de los informes
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                                android.R.layout.simple_spinner_item, titulosInformes);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerInformes.setAdapter(adapter);
                    } else {
                        Toast.makeText(getContext(), "Error al cargar informes", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Método para obtener Imagen de Logo
    private ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    // Obtener la URI de la imagen seleccionada
                    Uri imageUri = result.getData().getData();
                    imageSelectLogoEmpresa.setImageURI(imageUri);  // Mostrar la imagen en el ImageView
                    selectedImageUri = imageUri;  // Almacenar la URI de la imagen seleccionada
                }
            }
    );

    private String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String filePath = cursor.getString(column_index);
            cursor.close();
            return filePath;
        }
        return null;
    }



    private void obtenerInformePorTitulo(String informeTitulo) {
        // Buscar informe por título y llenar los TextViews
        db.collection("informes")
                .whereEqualTo("titulo", informeTitulo)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Obtener el primer documento coincidente
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);

                        // Rellenar los TextViews con los datos del informe
                        textTitulo.setText(document.getString("titulo"));
                        textDiagnostico.setText(document.getString("diagnostico"));
                        textSolucionPrimaria.setText(document.getString("solucionPrimaria"));
                        textArea.setText(document.getString("area"));
                        textSede.setText(document.getString("sede"));
                        textEstado.setText(document.getString("estado"));
                        textTipoEquipo.setText(document.getString("tipoEquipo"));
                        textFalla.setText(document.getString("falla"));
                        textOtrasActividades.setText(document.getString("otrasActividades"));
                        textFechaSolicitud.setText(document.getString("fechaSolicitud"));
                        textFechaInforme.setText(document.getString("fechaInforme"));
                        textDesTipoEquipo.setText(document.getString("descripcionEquipo"));
                        textSerieEquipo.setText(document.getString("serieEquipo"));
                        textNombreEquipo.setText(document.getString("nombreEquipo"));
                        textCodPatriEquipo.setText(document.getString("codigoPatrimonial"));
                        textMarcaEquipo.setText(document.getString("marcaEquipo"));
                        textCodInterEquipo.setText(document.getString("codigoInterno"));
                        textModeloEquipo.setText(document.getString("modeloEquipo"));
                        textColorEquipo.setText(document.getString("colorEquipo"));
                        textMantenimiento.setText(document.getString("mantenimiento"));
                        textObservaciones.setText(document.getString("observaciones"));
                        textFechaIngreso.setText(document.getString("fechaIngreso"));
                    } else {
                        Toast.makeText(getContext(), "Informe no encontrado", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Método para generar el PDF usando la plantilla seleccionada
    private void generarPDFConPlantilla(String plantilla) {
        // Dependiendo de la plantilla seleccionada, ajustar el formato del PDF
        if (plantilla.equals("Plantilla estándar")) {
            generarPDFEstandar();
        }
        // Aquí puedes agregar más plantillas en el futuro
    }

    private void generarPDFEstandar() {
        // Mostrar mensaje al usuario
        Toast.makeText(getContext(), "Generando PDF con la Plantilla Estándar", Toast.LENGTH_SHORT).show();

        // Ruta donde se guardará el PDF
        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
        File file = new File(pdfPath, "InformeTecnico.pdf");

        try {
            // Crear el documento PDF
            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument, PageSize.A4);
            document.setMargins(50, 50, 25, 25);

            // Declarar la variable de la imagen fuera del bloque if
            Image image = null;

            // Obtener la ruta de la imagen seleccionada desde la Uri
            String imagePath = getRealPathFromURI(selectedImageUri);

            if (imagePath != null) {
                // Cargar la imagen seleccionada por el usuario
                ImageData imageData = ImageDataFactory.create(imagePath);
                image = new Image(imageData);
                image.setWidth(100);
                image.setHeight(50);

                // Añadir la imagen al documento
                document.add(image);
            } else {
                Toast.makeText(getContext(), "No se pudo cargar la imagen seleccionada", Toast.LENGTH_SHORT).show();
            }

            // Crear tabla para el encabezado con dos columnas
            float[] columnWidths = {1f, 1f};
            Table headerTable = new Table(columnWidths);
            headerTable.setWidth(UnitValue.createPercentValue(100));

            // Verificar si la imagen fue cargada correctamente antes de añadirla a la celda
            if (image != null) {
                // Celdas para la imagen (izquierda)
                Cell leftCell = new Cell().add(image).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT);
                headerTable.addCell(leftCell);
            } else {
                // Si no hay imagen, añadir una celda vacía
                Cell leftCell = new Cell().setBorder(Border.NO_BORDER);
                headerTable.addCell(leftCell);
            }

            // Agregar la tabla del encabezado al documento
            document.add(headerTable);

            // Fuentes y estilos
            PdfFont titleFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // Título y subtítulo
            Paragraph title = new Paragraph("INFORME TÉCNICO DE SOPORTE INFORMÁTICO\n")
                    .setFont(titleFont)
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            String informeID = "01";  // Puedes usar un campo dinámico aquí si es necesario
            Paragraph subtitle = new Paragraph("N° 0" + informeID + " - 2024\n\n")
                    .setFont(titleFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(subtitle);

            // Tabla de Datos del Usuario
            Table userTable = new Table(new float[]{2, 2});
            userTable.setWidth(UnitValue.createPercentValue(100));
            userTable.addCell(new Cell().add(new Paragraph("1.- DATOS DEL USUARIO").setFont(titleFont).setBackgroundColor(ColorConstants.LIGHT_GRAY)).setBorder(Border.NO_BORDER));
            userTable.addCell(new Cell().setBorder(Border.NO_BORDER));  // Celda vacía para formato

            // Reemplazar valores estáticos con variables dinámicas obtenidas de Firestore
            userTable.addCell(new Cell().add(new Paragraph("Fecha Solicitud").setFont(normalFont)).setBorder(Border.NO_BORDER));
            userTable.addCell(new Cell().add(new Paragraph(textFechaSolicitud.getText().toString()).setFont(normalFont)).setBorder(Border.NO_BORDER));

            userTable.addCell(new Cell().add(new Paragraph("Gerencia / Sub Gerencia").setFont(normalFont)).setBorder(Border.NO_BORDER));
            userTable.addCell(new Cell().add(new Paragraph(textArea.getText().toString()).setFont(normalFont)).setBorder(Border.NO_BORDER));

            userTable.addCell(new Cell().add(new Paragraph("Sede").setFont(normalFont)).setBorder(Border.NO_BORDER));
            userTable.addCell(new Cell().add(new Paragraph(textSede.getText().toString()).setFont(normalFont)).setBorder(Border.NO_BORDER));

            userTable.addCell(new Cell().add(new Paragraph("Fecha del Informe").setFont(normalFont)).setBorder(Border.NO_BORDER));
            userTable.addCell(new Cell().add(new Paragraph(textFechaInforme.getText().toString()).setFont(normalFont)).setBorder(Border.NO_BORDER));

            // Añadir tabla de datos del usuario al documento
            document.add(userTable);

            // Tabla de Solicitud de Soporte Informático
            Table soporteTable = new Table(new float[]{2, 2});
            soporteTable.setWidth(UnitValue.createPercentValue(100));
            soporteTable.addCell(new Cell().add(new Paragraph("2.- SOLICITUD DE SOPORTE INFORMÁTICO").setFont(titleFont).setBackgroundColor(ColorConstants.LIGHT_GRAY)).setBorder(Border.NO_BORDER));
            soporteTable.addCell(new Cell().setBorder(Border.NO_BORDER));  // Celda vacía para formato

            soporteTable.addCell(new Cell().add(new Paragraph("Falla Reportada").setFont(normalFont)).setBorder(Border.NO_BORDER));
            soporteTable.addCell(new Cell().add(new Paragraph(textFalla.getText().toString()).setFont(normalFont)).setBorder(Border.NO_BORDER));

            soporteTable.addCell(new Cell().add(new Paragraph("Equipo Defectuoso").setFont(normalFont)).setBorder(Border.NO_BORDER));
            soporteTable.addCell(new Cell().add(new Paragraph(textTipoEquipo.getText().toString()).setFont(normalFont)).setBorder(Border.NO_BORDER));

            // Añadir tabla de soporte al documento
            document.add(soporteTable);

            // Tabla de Solicitud de Actividades
            Table actividadesTable = new Table(new float[]{2, 2});
            actividadesTable.setWidth(UnitValue.createPercentValue(100));
            actividadesTable.addCell(new Cell().add(new Paragraph("3.- ACTIVIDADES").setFont(titleFont).setBackgroundColor(ColorConstants.LIGHT_GRAY)).setBorder(Border.NO_BORDER));
            actividadesTable.addCell(new Cell().setBorder(Border.NO_BORDER));  // Celda vacía para formato

            actividadesTable.addCell(new Cell().add(new Paragraph("Otras Actividades").setFont(normalFont)).setBorder(Border.NO_BORDER));
            actividadesTable.addCell(new Cell().add(new Paragraph(textOtrasActividades.getText().toString()).setFont(normalFont)).setBorder(Border.NO_BORDER));

            // Añadir tabla de actividades al documento
            document.add(actividadesTable);

            // Tabla de Hardware y Software
            Table detalleTecnicoTable = new Table(new float[]{2, 2, 2, 2});  // Ajustado a 4 columnas
            detalleTecnicoTable.setWidth(UnitValue.createPercentValue(100));

            // Título de la tabla (ocupando las 4 columnas)
            detalleTecnicoTable.addCell(new Cell(1, 4).add(new Paragraph("4.- DETALLE TÉCNICO DEL HARDWARE Y SOFTWARE")
                            .setFont(titleFont)
                            .setBackgroundColor(ColorConstants.LIGHT_GRAY))
                    .setBorder(Border.NO_BORDER));

            // Primera fila
            detalleTecnicoTable.addCell(new Cell().add(new Paragraph("Tipo").setFont(normalFont)).setBorder(Border.NO_BORDER));
            detalleTecnicoTable.addCell(new Cell().add(new Paragraph(textNombreEquipo.getText().toString()).setFont(normalFont)).setBorder(Border.NO_BORDER));
            detalleTecnicoTable.addCell(new Cell().add(new Paragraph("Sub Tipo").setFont(normalFont)).setBorder(Border.NO_BORDER));
            detalleTecnicoTable.addCell(new Cell().add(new Paragraph(textDesTipoEquipo.getText().toString()).setFont(normalFont)).setBorder(Border.NO_BORDER));

            // Segunda fila
            detalleTecnicoTable.addCell(new Cell().add(new Paragraph("Color").setFont(normalFont)).setBorder(Border.NO_BORDER));
            detalleTecnicoTable.addCell(new Cell().add(new Paragraph(textColorEquipo.getText().toString()).setFont(normalFont)).setBorder(Border.NO_BORDER));
            detalleTecnicoTable.addCell(new Cell().add(new Paragraph("Modelo").setFont(normalFont)).setBorder(Border.NO_BORDER));
            detalleTecnicoTable.addCell(new Cell().add(new Paragraph(textModeloEquipo.getText().toString()).setFont(normalFont)).setBorder(Border.NO_BORDER));

            // Tercera fila
            detalleTecnicoTable.addCell(new Cell().add(new Paragraph("Serie").setFont(normalFont)).setBorder(Border.NO_BORDER));
            detalleTecnicoTable.addCell(new Cell().add(new Paragraph(textSerieEquipo.getText().toString()).setFont(normalFont)).setBorder(Border.NO_BORDER));
            detalleTecnicoTable.addCell(new Cell().add(new Paragraph("Marca").setFont(normalFont)).setBorder(Border.NO_BORDER));
            detalleTecnicoTable.addCell(new Cell().add(new Paragraph(textMarcaEquipo.getText().toString()).setFont(normalFont)).setBorder(Border.NO_BORDER));

            // Cuarta fila
            detalleTecnicoTable.addCell(new Cell().add(new Paragraph("Cód. Patrimonial").setFont(normalFont)).setBorder(Border.NO_BORDER));
            detalleTecnicoTable.addCell(new Cell().add(new Paragraph(textCodPatriEquipo.getText().toString()).setFont(normalFont)).setBorder(Border.NO_BORDER));
            detalleTecnicoTable.addCell(new Cell().add(new Paragraph("Código Interno").setFont(normalFont)).setBorder(Border.NO_BORDER));
            detalleTecnicoTable.addCell(new Cell().add(new Paragraph(textCodInterEquipo.getText().toString()).setFont(normalFont)).setBorder(Border.NO_BORDER));

            // Añadir tabla de Hardware y Software al documento
            document.add(detalleTecnicoTable);


            // Observaciones
            document.add(new Paragraph("\nOBSERVACIONES").setFont(titleFont));
            document.add(new Paragraph(textObservaciones.getText().toString()).setFont(normalFont));

            // Diagnóstico
            document.add(new Paragraph("\nDIAGNOSTICO").setFont(titleFont));
            document.add(new Paragraph(textDiagnostico.getText().toString()).setFont(normalFont));

            // Solución Primaria
            document.add(new Paragraph("\nSOLUCIÓN PRIMARIA").setFont(titleFont));
            document.add(new Paragraph(textSolucionPrimaria.getText().toString()).setFont(normalFont));

            // Tabla de firmas
            document.add(new Paragraph("\n"));
            Table finalTable = new Table(new float[]{1, 1, 1});
            finalTable.setWidth(UnitValue.createPercentValue(100));

            finalTable.addCell(new Cell().add(new Paragraph("USUARIO").setFont(normalFont)).setTextAlignment(TextAlignment.CENTER));
            finalTable.addCell(new Cell().add(new Paragraph("SOPORTE").setFont(normalFont)).setTextAlignment(TextAlignment.CENTER));
            finalTable.addCell(new Cell().add(new Paragraph("RESPONSABLE DE EFTIC").setFont(normalFont)).setTextAlignment(TextAlignment.CENTER));

            // Celdas en blanco para firmas
            finalTable.addCell(new Cell().add(new Paragraph("\n")).setHeight(40f).setBorder(Border.NO_BORDER));
            finalTable.addCell(new Cell().add(new Paragraph("\n")).setHeight(40f).setBorder(Border.NO_BORDER));
            finalTable.addCell(new Cell().add(new Paragraph("\n")).setHeight(40f).setBorder(Border.NO_BORDER));

            // Añadir tabla final al documento
            document.add(finalTable);

            // Cerrar el documento
            document.close();

            // Mensaje de confirmación
            Toast.makeText(getContext(), "PDF generado correctamente", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error al generar el PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}


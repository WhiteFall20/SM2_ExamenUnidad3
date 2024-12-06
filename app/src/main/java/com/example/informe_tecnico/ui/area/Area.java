package com.example.informe_tecnico.ui.area;

public class Area {
    private String id; // Agregar campo para el ID
    private String Nombre_Area;
    private String Descripcion_Area;
    private String sedeNombre; // Para almacenar el nombre de la sede, no el ID

    // Constructor vacío (si lo necesitas)
    public Area() {
    }

    // Constructor para inicializar los campos fácilmente
    public Area(String nombre_Area, String descripcion_Area, String sedeNombre) {
        this.Nombre_Area = nombre_Area;
        this.Descripcion_Area = descripcion_Area;
        this.sedeNombre = sedeNombre;
    }

    // Getters y setters para los campos

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre_Area() {
        return Nombre_Area;
    }

    public void setNombre_Area(String nombre_Area) {
        this.Nombre_Area = nombre_Area;
    }

    public String getDescripcion_Area() {
        return Descripcion_Area;
    }

    public void setDescripcion_Area(String descripcion_Area) {
        this.Descripcion_Area = descripcion_Area;
    }

    public String getSedeNombre() {
        return sedeNombre;
    }

    public void setSedeNombre(String sedeNombre) {
        this.sedeNombre = sedeNombre;
    }
}

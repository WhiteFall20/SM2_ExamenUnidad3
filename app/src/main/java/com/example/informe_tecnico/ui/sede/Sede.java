package com.example.informe_tecnico.ui.sede;

public class Sede {
    private String id;
    private String Nombre_Sede;
    private String Descripcion;

    // Constructor vacío requerido por Firebase
    public Sede() {}

    // Constructor con parámetros
    public Sede(String id, String nombre_Sede, String descripcion) {
        this.id = id;
        this.Nombre_Sede = nombre_Sede;
        this.Descripcion = descripcion;
    }

    // Getters y setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre_Sede() {
        return Nombre_Sede;
    }

    public void setNombre_Sede(String nombre_Sede) {
        this.Nombre_Sede = nombre_Sede;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.Descripcion = descripcion;
    }
}

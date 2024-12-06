package com.example.informe_tecnico.ui.falla;

public class Falla {
    private String id;
    private String Nombre_Falla;
    private String Descripcion;

    // Constructor vacío requerido por Firebase
    public Falla() {}

    // Constructor con parámetros
    public Falla(String id, String nombre_Falla, String descripcion) {
        this.id = id;
        this.Nombre_Falla = nombre_Falla;
        this.Descripcion = descripcion;
    }

    // Getters y setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre_Falla() {
        return Nombre_Falla;
    }

    public void setNombre_Sede(String nombre_Falla) {
        this.Nombre_Falla = nombre_Falla;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.Descripcion = descripcion;
    }
}

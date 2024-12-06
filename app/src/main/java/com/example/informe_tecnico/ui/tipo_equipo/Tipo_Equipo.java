package com.example.informe_tecnico.ui.tipo_equipo;

public class Tipo_Equipo {
    private String id;
    private String Nombre_Tipo_Equipo;
    private String Descripcion;

    // Constructor vacío requerido por Firebase
    public Tipo_Equipo() {}

    // Constructor con parámetros
    public Tipo_Equipo(String id, String nombre_Tipo_Equipo, String descripcion) {
        this.id = id;
        this.Nombre_Tipo_Equipo = nombre_Tipo_Equipo;
        this.Descripcion = descripcion;
    }

    // Getters y setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre_Tipo_Equipo() {
        return Nombre_Tipo_Equipo;
    }

    public void setNombre_Tipo_Equipo(String nombre_Tipo_Equipo) {
        this.Nombre_Tipo_Equipo = nombre_Tipo_Equipo;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.Descripcion = descripcion;
    }
}

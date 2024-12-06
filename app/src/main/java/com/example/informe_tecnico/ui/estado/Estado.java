package com.example.informe_tecnico.ui.estado;

public class Estado {
    private String id;
    private String Nombre_Estado;
    private String Descripcion;

    // Constructor vacío requerido por Firebase
    public Estado() {}

    // Constructor con parámetros
    public Estado(String id, String nombre_Estado, String descripcion) {
        this.id = id;
        this.Nombre_Estado = nombre_Estado;
        this.Descripcion = descripcion;
    }

    // Getters y setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre_Estado() {
        return Nombre_Estado;
    }

    public void setNombre_Estado(String nombre_Estado) {
        this.Nombre_Estado = nombre_Estado;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.Descripcion = descripcion;
    }
}

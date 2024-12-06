package com.example.informe_tecnico.ui.o_actividades;

public class OtrasActividades {
    private String id;
    private String Nombre_OtrasActividades;
    private String Descripcion;

    // Constructor vacío requerido por Firebase
    public OtrasActividades() {}

    // Constructor con parámetros
    public OtrasActividades(String id, String nombre_OtrasActividades, String descripcion) {
        this.id = id;
        this.Nombre_OtrasActividades = nombre_OtrasActividades;
        this.Descripcion = descripcion;
    }

    // Getters y setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre_OtrasActividades() {
        return Nombre_OtrasActividades;
    }

    public void setNombre_Sede(String nombre_OtrasActividades) {
        this.Nombre_OtrasActividades = nombre_OtrasActividades;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.Descripcion = descripcion;
    }
}

package com.example.informe_tecnico.ui.listainforme;

public class InformeTecnico {
    private String id;
    private String Titulo;
    private String Fecha_Solicitud;
    private String Fecha_Informe;
    private String Fecha_Ingreso;
    private String Diagnostico;
    private String Solucion_Primaria;
    private String Nombre_Equipo;
    private String Tipo_Equipo;
    private String Color;
    private String Serie;
    private String Cod_Patrimonial;
    private String Cod_Interno;
    private String Modelo;
    private String Marca;
    private String Mantenimiento;
    private String Observaciones;
    private String sedeNombre;
    private String areaNombre;
    private String tipoEquipoNombre;
    private String estadoNombre;
    private String fallaNombre;
    private String otrasActividadesNombre;

    // Constructor vacío requerido por Firebase
    public InformeTecnico() {}

    // Constructor para inicializar todos los campos
    public InformeTecnico(String id, String titulo, String fecha_Solicitud, String fecha_Informe,
                          String fecha_Ingreso, String diagnostico, String solucion_Primaria,
                          String nombre_Equipo, String tipo_Equipo, String color, String serie,
                          String cod_Patrimonial, String cod_Interno, String modelo, String marca,
                          String mantenimiento, String observaciones, String sedeNombre, String areaNombre,
                          String tipoEquipoNombre, String estadoNombre, String fallaNombre,
                          String otrasActividadesNombre) {
        this.id = id;
        this.Titulo = titulo;
        this.Fecha_Solicitud = fecha_Solicitud;
        this.Fecha_Informe = fecha_Informe;
        this.Fecha_Ingreso = fecha_Ingreso;
        this.Diagnostico = diagnostico;
        this.Solucion_Primaria = solucion_Primaria;
        this.Nombre_Equipo = nombre_Equipo;
        this.Tipo_Equipo = tipo_Equipo;
        this.Color = color;
        this.Serie = serie;
        this.Cod_Patrimonial = cod_Patrimonial;
        this.Cod_Interno = cod_Interno;
        this.Modelo = modelo;
        this.Marca = marca;
        this.Mantenimiento = mantenimiento;
        this.Observaciones = observaciones;
        this.sedeNombre = sedeNombre;
        this.areaNombre = areaNombre;
        this.tipoEquipoNombre = tipoEquipoNombre;
        this.estadoNombre = estadoNombre;
        this.fallaNombre = fallaNombre;
        this.otrasActividadesNombre = otrasActividadesNombre;
    }

    // Getters y Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return Titulo;
    }

    public void setTitulo(String titulo) {
        this.Titulo = titulo;
    }

    public String getFecha_Solicitud() {
        return Fecha_Solicitud;
    }

    public void setFecha_Solicitud(String fecha_Solicitud) {
        this.Fecha_Solicitud = fecha_Solicitud;
    }

    public String getFecha_Informe() {
        return Fecha_Informe;
    }

    public void setFecha_Informe(String fecha_Informe) {
        this.Fecha_Informe = fecha_Informe;
    }

    public String getFecha_Ingreso() {
        return Fecha_Ingreso;
    }

    public void setFecha_Ingreso(String fecha_Ingreso) {
        this.Fecha_Ingreso = fecha_Ingreso;
    }

    public String getDiagnostico() {
        return Diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.Diagnostico = diagnostico;
    }

    public String getSolucion_Primaria() {
        return Solucion_Primaria;
    }

    public void setSolucion_Primaria(String solucion_Primaria) {
        this.Solucion_Primaria = solucion_Primaria;
    }

    public String getNombre_Equipo() {
        return Nombre_Equipo;
    }

    public void setNombre_Equipo(String nombre_Equipo) {
        this.Nombre_Equipo = nombre_Equipo;
    }

    public String getTipo_Equipo() {
        return Tipo_Equipo;
    }

    public void setTipo_Equipo(String tipo_Equipo) {
        this.Tipo_Equipo = tipo_Equipo;
    }

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        this.Color = color;
    }

    public String getSerie() {
        return Serie;
    }

    public void setSerie(String serie) {
        this.Serie = serie;
    }

    public String getCod_Patrimonial() {
        return Cod_Patrimonial;
    }

    public void setCod_Patrimonial(String cod_Patrimonial) {
        this.Cod_Patrimonial = cod_Patrimonial;
    }

    public String getCod_Interno() {
        return Cod_Interno;
    }

    public void setCod_Interno(String cod_Interno) {
        this.Cod_Interno = cod_Interno;
    }

    public String getModelo() {
        return Modelo;
    }

    public void setModelo(String modelo) {
        this.Modelo = modelo;
    }

    public String getMarca() {
        return Marca;
    }

    public void setMarca(String marca) {
        this.Marca = marca;
    }

    public String getMantenimiento() {
        return Mantenimiento;
    }

    public void setMantenimiento(String mantenimiento) {
        this.Mantenimiento = mantenimiento;
    }

    public String getObservaciones() {
        return Observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.Observaciones = observaciones;
    }

    public String getSedeNombre() {
        return sedeNombre;
    }

    public void setSedeNombre(String sedeNombre) {
        this.sedeNombre = sedeNombre;
    }

    public String getAreaNombre() {
        return areaNombre;
    }

    public void setAreaNombre(String areaNombre) {
        this.areaNombre = areaNombre;
    }

    public String getTipoEquipoNombre() {
        return tipoEquipoNombre;
    }

    public void setTipoEquipoNombre(String tipoEquipoNombre) {
        this.tipoEquipoNombre = tipoEquipoNombre;
    }

    public String getEstadoNombre() {
        return estadoNombre;
    }

    public void setEstadoNombre(String estadoNombre) {
        this.estadoNombre = estadoNombre;
    }

    public String getFallaNombre() {
        return fallaNombre;
    }

    public void setFallaNombre(String fallaNombre) {
        this.fallaNombre = fallaNombre;
    }

    public String getOtrasActividadesNombre() {
        return otrasActividadesNombre;
    }

    public void setOtrasActividadesNombre(String otrasActividadesNombre) {
        this.otrasActividadesNombre = otrasActividadesNombre;
    }
}

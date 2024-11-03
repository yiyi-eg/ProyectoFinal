package com.mycompany.proyecto;

public class Video {
    private String nombre;
    private String extension;
    private String duracion;
    private String ruta;
    private String tamanio;

    public Video(String nombre, String extension, String duracion, String ruta, String tamanio) {
        this.nombre = nombre;
        this.extension = extension;
        this.duracion = duracion;
        this.ruta = ruta;
        this.tamanio = tamanio;
    }

    public String getNombre() { return nombre; }
    public String getExtensión() { return extension; }
    public String getDuración() { return duracion; }
    public String getRuta() { return ruta; }
    public String getTamaño() { return tamanio; }
}

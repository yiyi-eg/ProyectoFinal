
package com.mycompany.proyecto;

public class ImagenData {
    private String nombre;
    private String extension;
    private String ruta;

    public ImagenData(String nombre, String extension, String ruta) {
        this.nombre = nombre;
        this.extension = extension;
        this.ruta = ruta;
    }

    public String getNombre() {
        return nombre;
    }

    public String getExtension() {
        return extension;
    }

    public String getRuta() {
        return ruta;
    }
}

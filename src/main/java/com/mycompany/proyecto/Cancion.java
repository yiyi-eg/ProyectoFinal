package com.mycompany.proyecto;


public class Cancion {
    private String nombre, extension, duracion, genero, artista, album, anio, ruta, tamanio;

    public Cancion(String nombre, String extension, String duracion, String genero,
                   String artista, String album, String anio, String ruta, String tamanio) {
        this.nombre = nombre;
        this.extension = extension;
        this.duracion = duracion;
        this.genero = genero;
        this.artista = artista;
        this.album = album;
        this.anio = anio;
        this.ruta = ruta;
        this.tamanio = tamanio;
    }

    public String getRuta() { return ruta; }
    public String getNombre() { return nombre; }
    public String getExtencion() { return extension; }
    public String getDuracion() { return duracion; }
    public String getGenero() { return genero; }
    public String getArtista() { return artista; }
    public String getAlbum() { return album; }
    public String getAnio() { return anio; }
    public String getTamanio() { return tamanio; }

}

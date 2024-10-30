
package com.mycompany.proyecto;

public class Cancion {
    private String nombre;
    private String duracion;
    private String genero;
    private String artista;
    private String album;
    private String tamanio;

    // Constructor
    public Cancion(String nombre, String duracion, String genero, String artista, String album, String tamanio) {
        this.nombre = nombre;
        this.duracion = duracion;
        this.genero = genero;
        this.artista = artista;
        this.album = album;
        this.tamanio = tamanio;
    }

    // Getters
    public String getNombre() { return nombre; }
    public String getDuracion() { return duracion; }
    public String getGenero() { return genero; }
    public String getArtista() { return artista; }
    public String getAlbum() { return album; }
    public String getTamanio() { return tamanio; }
}


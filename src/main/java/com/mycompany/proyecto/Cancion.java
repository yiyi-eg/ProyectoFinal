package com.mycompany.proyecto;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.table.DefaultTableModel;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.*;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

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

    public static ArrayList<Cancion> buscarCancionesEnDirectorio(File directorio) {
        ArrayList<Cancion> canciones = new ArrayList<>();
        if (directorio.isDirectory()) {
            for (File archivo : directorio.listFiles()) {
                if (archivo.getName().endsWith(".mp3") || archivo.getName().endsWith(".wav")) {
                    Cancion cancion = obtenerInformacionCancion(archivo);
                    if (cancion != null) {  // Verificación adicional por si algún archivo falla
                        canciones.add(cancion);
                    }
                }
            }
        }
        return canciones;
    }

    public static Cancion obtenerInformacionCancion(File archivo) {
        String nombre = archivo.getName();
        String extension = nombre.substring(nombre.lastIndexOf('.') + 1);
        String duracion = "";
        String genero = "";
        String artista = "";
        String album = "";
        String anio = "";
        String ruta = archivo.getAbsolutePath();
        String tamanio = archivo.length() / 1024 + " KB";

        try {
            AudioFile audioFile = AudioFileIO.read(archivo);
            Tag tag = audioFile.getTag();
            duracion = String.format("%d min", audioFile.getAudioHeader().getTrackLength() / 60);
            genero = tag.getFirst(FieldKey.GENRE);
            artista = tag.getFirst(FieldKey.ARTIST);
            album = tag.getFirst(FieldKey.ALBUM);
            anio = tag.getFirst(FieldKey.YEAR);
        } catch (Exception e) {
            System.out.println("Error al obtener metadatos del archivo: " + archivo.getName() + " - " + e.getMessage());
            return null; // Devuelve null si ocurre algún error
        }

        return new Cancion(nombre, extension, duracion, genero, artista, album, anio, ruta, tamanio);
    }

    public static void mostrarDuplicados(ArrayList<Cancion> listaCanciones, DefaultTableModel modeloTabla) {
        Set<String> archivosUnicos = new HashSet<>();
        ArrayList<Cancion> duplicados = new ArrayList<>();

        for (Cancion cancion : listaCanciones) {
            String key = cancion.getNombre() + cancion.getTamanio() + cancion.getDuracion();
            if (!archivosUnicos.add(key)) {
                duplicados.add(cancion);
            }
        }

        modeloTabla.setRowCount(0); // Limpiar tabla
        for (Cancion cancion : duplicados) {
            modeloTabla.addRow(new Object[]{
                cancion.getNombre(), cancion.getExtencion(), cancion.getArtista(), cancion.getAlbum(),
                cancion.getGenero(), cancion.getDuracion(), cancion.getAnio(), cancion.getRuta(), cancion.getTamanio()
            });
        }
    }

    public static void eliminarDuplicados(ArrayList<Cancion> listaCanciones) {
        Set<String> archivosUnicos = new HashSet<>();
        Iterator<Cancion> iterador = listaCanciones.iterator();

        while (iterador.hasNext()) {
            Cancion cancion = iterador.next();
            String key = cancion.getNombre() + cancion.getTamanio() + cancion.getDuracion();
            if (!archivosUnicos.add(key)) {
                iterador.remove();
            }
        }
    }
}

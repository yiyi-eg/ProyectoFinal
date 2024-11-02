package com.mycompany.proyecto;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.*;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

public class ReproductorMusica {
    private final List<File> listaCanciones;
    private int indiceActual = 0;
    private Clip clip;
    private AdvancedPlayer mp3Player;
    private boolean enPausa = false;
    private boolean esMP3 = false;
    private Thread mp3Thread;

    public ReproductorMusica(ArrayList<File> canciones) {
        this.listaCanciones = canciones;
    }

    public static ReproductorMusica fromCanciones(ArrayList<Cancion> listaCanciones) {
    ArrayList<File> archivos = new ArrayList<>();
    for (Cancion cancion : listaCanciones) {
        archivos.add(new File(cancion.getRuta()));
    }
    return new ReproductorMusica(archivos);
}

    public void reproducir() {
        if (listaCanciones.isEmpty()) {
            Logger.getLogger(ReproductorMusica.class.getName()).log(Level.WARNING, "No hay canciones para reproducir.");
            return;
        }

        detener(); // Detener cualquier reproducción en curso

        File cancion = listaCanciones.get(indiceActual);
        esMP3 = cancion.getName().endsWith(".mp3");

        try {
            if (esMP3) {
                FileInputStream fis = new FileInputStream(cancion);
                mp3Player = new AdvancedPlayer(fis);
                mp3Thread = new Thread(() -> {
                    try {
                        mp3Player.play();
                        siguiente();
                    } catch (JavaLayerException e) {
                        Logger.getLogger(ReproductorMusica.class.getName()).log(Level.SEVERE, "Error al reproducir MP3", e);
                    }
                });
                mp3Thread.start();
            } else {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(cancion);
                clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.start();
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        siguiente();
                    }
                });
            }
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException | JavaLayerException e) {
            Logger.getLogger(ReproductorMusica.class.getName()).log(Level.SEVERE, "Error al reproducir el archivo de audio", e);
        }
    }

    public void pausar() {
        if (esMP3 && mp3Player != null) {
            enPausa = true;
            detener(); // Pausar deteniendo en MP3
        } else if (clip != null && clip.isRunning()) {
            clip.stop();
            enPausa = true;
        }
    }

    public void siguiente() {
        if (indiceActual < listaCanciones.size() - 1) {
            indiceActual++;
            reproducir();
        } else {
            Logger.getLogger(ReproductorMusica.class.getName()).log(Level.INFO, "No hay más canciones en la lista.");
        }
    }

    public void anterior() {
        if (indiceActual > 0) {
            indiceActual--;
            reproducir();
        } else {
            Logger.getLogger(ReproductorMusica.class.getName()).log(Level.INFO, "Estás en el inicio de la lista.");
        }
    }

    public void detener() {
        if (clip != null) {
            clip.stop();
            clip.close();
            clip = null;
        }
        if (mp3Player != null) {
            mp3Player.close();
            mp3Player = null;
        }
        if (mp3Thread != null) {
            mp3Thread.interrupt();
            mp3Thread = null;
        }
        enPausa = false;
    }

    public boolean estaReproduciendo() {
        return (clip != null && clip.isRunning()) || (mp3Player != null);
    }

    public boolean estaPausado() {
        return enPausa;
    }

    public void setIndiceActual(int nuevoIndice) {
        if (nuevoIndice >= 0 && nuevoIndice < listaCanciones.size()) {
            indiceActual = nuevoIndice;
            reproducir();
        } else {
            Logger.getLogger(ReproductorMusica.class.getName()).log(Level.WARNING, "Índice fuera de rango.");
        }
    }

    public void detenerAlSalir() {
        detener();
        Logger.getLogger(ReproductorMusica.class.getName()).log(Level.INFO, "Reproducción detenida al salir al menú.");
    }
}
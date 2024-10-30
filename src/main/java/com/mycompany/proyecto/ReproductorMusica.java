package com.mycompany.proyecto;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

public class ReproductorMusica {
    private final List<File> listaCanciones;
    private int indiceActual = 0;
    private Clip clip;
    private AdvancedPlayer mp3Player;
    private boolean enPausa = false;
    private boolean esMP3 = false;

    public ReproductorMusica(List<File> canciones) {
        this.listaCanciones = canciones;
    }

    public void reproducir() {
        if (listaCanciones.isEmpty()) {
            System.out.println("No hay canciones para reproducir.");
            return;
        }
        if (enPausa && esMP3) {
            enPausa = false;
            return;
        } else if (enPausa && clip != null) {
            clip.start();
            enPausa = false;
            return;
        }

        detener();
        File cancion = listaCanciones.get(indiceActual);
        esMP3 = cancion.getName().endsWith(".mp3");

        try {
            if (esMP3) {
                FileInputStream fis = new FileInputStream(cancion);
                mp3Player = new AdvancedPlayer(fis);
                new Thread(() -> {
                    try {
                        mp3Player.play();
                        siguiente();
                    } catch (JavaLayerException | UnsupportedAudioFileException | LineUnavailableException e) {
                        Logger.getLogger(ReproductorMusica.class.getName()).log(Level.SEVERE, "Error al reproducir MP3", e);
                    }
                }).start();
            } else {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(cancion);
                clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.start();
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        try {
                            siguiente();
                        } catch (JavaLayerException | UnsupportedAudioFileException | LineUnavailableException e) {
                            Logger.getLogger(ReproductorMusica.class.getName()).log(Level.SEVERE, "Error en reproducción siguiente", e);
                        }
                    }
                });
            }
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException | JavaLayerException e) {
            System.out.println("Error al reproducir el archivo de audio: " + e.getMessage());
        }
    }

    public void pausar() {
        if (esMP3 && mp3Player != null) {
            enPausa = true;
            mp3Player.close();
        } else if (clip != null && clip.isRunning()) {
            clip.stop();
            enPausa = true;
        }
    }

    public void siguiente() throws JavaLayerException, UnsupportedAudioFileException, LineUnavailableException {
        if (indiceActual < listaCanciones.size() - 1) {
            indiceActual++;
            reproducir();
        } else {
            System.out.println("No hay más canciones en la lista.");
        }
    }

    public void anterior() throws JavaLayerException, UnsupportedAudioFileException, LineUnavailableException {
        if (indiceActual > 0) {
            indiceActual--;
            reproducir();
        } else {
            System.out.println("Estás en el inicio de la lista.");
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
        enPausa = false;
    }

    public boolean estaReproduciendo() {
        return (clip != null && clip.isRunning()) || (mp3Player != null);
    }

    public boolean estaPausado() {
        return enPausa;
    }

    public void setIndiceActual(int nuevoIndice) throws JavaLayerException, UnsupportedAudioFileException, LineUnavailableException {
        if (nuevoIndice >= 0 && nuevoIndice < listaCanciones.size()) {
            indiceActual = nuevoIndice;
            reproducir();
        } else {
            System.out.println("Índice fuera de rango.");
        }
    }

    public void detenerAlSalir() {
        detener();
        System.out.println("Reproducción detenida al salir al menú.");
    }
}



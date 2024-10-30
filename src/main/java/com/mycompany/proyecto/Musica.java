package com.mycompany.proyecto;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javazoom.jl.decoder.JavaLayerException;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.TagException;

public class Musica extends JFrame {
    private JPanel panel,panelControles;
    private JButton btnRegresar, btnBuscar, btnSeleccionarRuta;
    private JButton btnAnterior, btnReproducir, btnSiguiente, btnPausar; 
    private JTextField rutaTexto;
    private JTable tablaCanciones;
    private DefaultTableModel modeloTabla;
    private JScrollPane scrollPane;
    private String rutaSeleccionada;
    private final ArrayList<File> listaCanciones = new ArrayList<>(); 
    private ReproductorMusica reproductorMusica;

    public Musica() {
        this.setTitle("CANCIONES");
        this.setSize(1200, 720);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeComponents();
            }
        });
        componentes();
    }

    private void componentes() {
        panel();
        btnRegresar();
        btnRegresarAcciones();
        campoRuta();
        btnSeleccionarRuta();
        btnBuscarCanciones();
        tablaCanciones();
        agregarControlesMusica();
        efectoHover( btnRegresar);
    }

    private void panel() {
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon imagen = new ImageIcon(getClass().getResource("/FONDOS/backMusica.png"));
                g.drawImage(imagen.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        
        panel.setLayout(new BorderLayout());
        this.getContentPane().add(panel);
    }

  private void btnRegresar() {
        btnRegresar = new JButton();
        ImageIcon im = new ImageIcon(getClass().getResource("/iconos/regreso.png"));
        btnRegresar.setIcon(new ImageIcon(im.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH)));
        btnRegresar.setOpaque(false);
        btnRegresar.setContentAreaFilled(false);
        btnRegresar.setBorderPainted(false);
        btnRegresar.setFocusPainted(false);
        panel.add(btnRegresar);
    }

    private void btnRegresarAcciones() {
        btnRegresar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (reproductorMusica != null) {
                    reproductorMusica.detenerAlSalir();  // Llamada para detener la reproducción
                }
                dispose(); // Cerrar la ventana
            }
        });
}

    
    private void efectoHover(JButton boton) {
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                boton.setBackground(Color.LIGHT_GRAY);
                boton.setOpaque(true);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                boton.setBackground(null);
                boton.setOpaque(false);
            }
        });
    }
    
    private void campoRuta() {
        rutaTexto = new JTextField();
        panel.add(rutaTexto);
    }

    
    private void btnSeleccionarRuta() {
        btnSeleccionarRuta = new JButton("...");
        btnSeleccionarRuta.addActionListener(e -> seleccionarRuta());
        panel.add(btnSeleccionarRuta);
    }

    private void seleccionarRuta() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de música", "mp3", "wav"));
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            rutaTexto.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }
    private void btnBuscarCanciones(){
    btnBuscar = new JButton("Buscar Canciones");
        btnBuscar.addActionListener(e -> {
            rutaSeleccionada = rutaTexto.getText();
            if (!rutaSeleccionada.isEmpty()) {
                cargarCanciones();
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione una ruta válida.");
            }
            
        });
        panel.add(btnBuscar);
    }
    private void tablaCanciones() {
        String[] columnas = {"Nombre", "Duración", "Género", "Artista", "Álbum", "Tamaño"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaCanciones = new JTable(modeloTabla);
        scrollPane = new JScrollPane(tablaCanciones);
        panel.add(scrollPane, BorderLayout.CENTER);
    }

    private void cargarCanciones() {
        new Thread(() -> {
            modeloTabla.setRowCount(0);
            listaCanciones.clear();
            ArrayList<Cancion> canciones = buscarCancionesEnDirectorio(new File(rutaSeleccionada));

            SwingUtilities.invokeLater(() -> {
                for (Cancion cancion : canciones) {
                    modeloTabla.addRow(new Object[]{
                        cancion.getNombre(), cancion.getDuracion(), cancion.getGenero(),
                        cancion.getArtista(), cancion.getAlbum(), cancion.getTamanio()
                    });
                }
                btnAnterior.setEnabled(!canciones.isEmpty());
                btnReproducir.setEnabled(!canciones.isEmpty());
                btnSiguiente.setEnabled(!canciones.isEmpty());
                btnPausar.setEnabled(!canciones.isEmpty());
            });

            reproductorMusica = new ReproductorMusica(listaCanciones);
        }).start();
    }

    private ArrayList<Cancion> buscarCancionesEnDirectorio(File directorio) {
        ArrayList<Cancion> canciones = new ArrayList<>();
        if (directorio.isDirectory()) {
            for (File archivo : directorio.listFiles()) {
                if (archivo.getName().endsWith(".mp3") || archivo.getName().endsWith(".wav")) {
                    listaCanciones.add(archivo);
                    canciones.add(obtenerInformacionCancion(archivo));
                }
            }
        }
        return canciones;
    }

    private Cancion obtenerInformacionCancion(File archivo) {
        String nombre = archivo.getName();
        String duracion = "";
        String genero = "";
        String artista = "";
        String album = "";
        String tamanio = archivo.length() / 1024 + " KB";
        try {
            AudioFile audioFile = AudioFileIO.read(archivo);
            var tag = audioFile.getTag();
            duracion = String.format("%d min", audioFile.getAudioHeader().getTrackLength() / 60);
            genero = tag.getFirst(FieldKey.GENRE);
            artista = tag.getFirst(FieldKey.ARTIST);
            album = tag.getFirst(FieldKey.ALBUM);
        } catch (IOException | CannotReadException | InvalidAudioFrameException | ReadOnlyFileException | KeyNotFoundException | TagException e) {
            System.out.println("Error al obtener metadatos: " + e.getMessage());
        }
        return new Cancion(nombre, duracion, genero, artista, album, tamanio);
    }

    private void agregarControlesMusica() {
        panelControles = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        btnAnterior = new JButton("Anterior");
        btnAnterior.setEnabled(false);
        btnAnterior.addActionListener((ActionEvent e) -> {
            try {
                reproductorMusica.anterior();
            } catch (JavaLayerException | UnsupportedAudioFileException | LineUnavailableException ex) {
                Logger.getLogger(Musica.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        btnReproducir = new JButton("Reproducir");
        btnReproducir.setEnabled(false);
        btnReproducir.addActionListener(e -> {
            reproductorMusica.reproducir();
        });

        btnPausar = new JButton("Pausar");
        btnPausar.setEnabled(false);
        btnPausar.addActionListener(e -> reproductorMusica.pausar());

        btnSiguiente = new JButton("Siguiente");
        btnSiguiente.setEnabled(false);
        btnSiguiente.addActionListener(e -> {
            try {
                reproductorMusica.siguiente();
            } catch (JavaLayerException | UnsupportedAudioFileException | LineUnavailableException ex) {
                Logger.getLogger(Musica.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        panelControles.add(btnAnterior);
        panelControles.add(btnReproducir);
        panelControles.add(btnPausar);
        panelControles.add(btnSiguiente);

        panel.add(panelControles, BorderLayout.SOUTH);
    }
    private void resizeComponents() {
        Dimension size = panel.getSize();
        btnRegresar.setBounds((int) (size.width * 0.02), (int) (size.height * 0.02), (int) (size.width * 0.04), (int) (size.height * 0.06));
        rutaTexto.setBounds((int) (size.width * 0.07), (int) (size.height * 0.03), (int) (size.width * 0.3), (int) (size.height * 0.05));
        btnSeleccionarRuta.setBounds((int) (size.width * 0.38), (int) (size.height * 0.03), (int) (size.width * 0.05), (int) (size.height * 0.05));
        btnBuscar.setBounds((int) (size.width * 0.44), (int) (size.height * 0.03), (int) (size.width * 0.2), (int) (size.height * 0.05));
        scrollPane.setBounds((int) (size.width * 0.02), (int) (size.height * 0.1), (int) (size.width * 0.95), (int) (size.height * 0.7));
        panelControles.setBounds((int) (size.width * 0.25), (int) (size.height * 0.8), (int) (size.width * 0.5), (int) (size.height * 0.09));
        btnAnterior.setBounds((int) (size.width * 0.05), (int) (size.height * 0.01), (int) (size.width * 0.09), (int) (size.height * 0.06));
        btnReproducir.setBounds((int) (size.width * 0.15), (int) (size.height * 0.01), (int) (size.width * 0.09), (int) (size.height * 0.06));
        btnPausar.setBounds((int) (size.width * 0.25), (int) (size.height * 0.01), (int) (size.width * 0.09), (int) (size.height * 0.06));
        btnSiguiente.setBounds((int) (size.width * 0.35), (int) (size.height * 0.01), (int) (size.width * 0.09), (int) (size.height * 0.06));
    }
}
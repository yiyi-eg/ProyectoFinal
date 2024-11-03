package com.mycompany.proyecto;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Videos extends JFrame {
    private JTextField rutaTexto;
    private JTable tablaVideos;
    private final ArrayList<Video> listaVideos = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    private JButton btnRegresar; // Botón de regresar

    public Videos() {
        setTitle("VIDEOS");
        setSize(1200, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        rutaTexto = new JTextField(40);
        JButton btnSeleccionarRuta = new JButton("Seleccionar Ruta");
        btnSeleccionarRuta.addActionListener(e -> seleccionarRuta());
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> cargarVideos());

        // Crear botones de navegación
        btnRegresar = crearBotonIcono("/iconos/regreso.png", 35);
        btnRegresar.addActionListener(e -> {
            // Acciones al hacer clic en el botón de regresar
            dispose(); // Cierra la ventana actual
        });

        topPanel.add(rutaTexto);
        topPanel.add(btnSeleccionarRuta);
        topPanel.add(btnBuscar);
        topPanel.add(btnRegresar); // Agregar botón de regresar al panel superior
        add(topPanel, BorderLayout.NORTH);

        // Configuración de la tabla
        tablaVideos = new JTable(new DefaultTableModel(new Object[]{"Nombre", "Extensión", "Duración"}, 0));

        JScrollPane scrollPane = new JScrollPane(tablaVideos);
        add(scrollPane, BorderLayout.CENTER);

        // Crear panel de reproducción de video con JFXPanel
        JFXPanel videoPanel = new JFXPanel();
        add(videoPanel, BorderLayout.SOUTH);
        videoPanel.setPreferredSize(new Dimension(800, 500));

        tablaVideos.getSelectionModel().addListSelectionListener(event -> {
            int selectedRow = tablaVideos.getSelectedRow();
            if (selectedRow != -1) {
                Video video = listaVideos.get(selectedRow);
                reproducirVideo(video.getRuta(), videoPanel);
            }
        });
    }

    // Método para crear un botón con un ícono
    public JButton crearBotonIcono(String rutaIcono, int tamano) {
        JButton boton = new JButton();
        ImageIcon im = new ImageIcon(getClass().getResource(rutaIcono));
        boton.setIcon(new ImageIcon(im.getImage().getScaledInstance(tamano, tamano, Image.SCALE_SMOOTH)));
        boton.setOpaque(false);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        return boton;
    }

    private void seleccionarRuta() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = fileChooser.getSelectedFile();
            rutaTexto.setText(selectedDirectory.getAbsolutePath());
            cargarVideos();
        }
    }

    private void cargarVideos() {
        String rutaSeleccionada = rutaTexto.getText();
        if (!rutaSeleccionada.isEmpty()) {
            File directorio = new File(rutaSeleccionada);
            if (directorio.isDirectory()) {
                List<Video> videos = buscarVideosEnDirectorio(directorio);
                DefaultTableModel model = (DefaultTableModel) tablaVideos.getModel();
                model.setRowCount(0); // Limpiar la tabla
                listaVideos.clear();
                listaVideos.addAll(videos);
                for (Video video : videos) {
                    model.addRow(new Object[]{video.getNombre(), video.getExtensión(), video.getDuración()});
                }
            } else {
                showAlert("Error", "La ruta seleccionada no es un directorio válido.");
            }
        } else {
            showAlert("Error", "Seleccione una ruta válida.");
        }
    }

    public List<Video> buscarVideosEnDirectorio(File directorio) {
        List<Video> videos = new ArrayList<>();
        File[] archivos = directorio.listFiles((dir, name) -> name.endsWith(".mp4") || name.endsWith(".avi") || name.endsWith(".mkv"));
        if (archivos != null) {
            for (File archivo : archivos) {
                Video video = obtenerInformacionVideo(archivo);
                if (video != null) {
                    videos.add(video);
                }
            }
        }
        return videos;
    }

    private Video obtenerInformacionVideo(File archivo) {
        String nombre = archivo.getName();
        String extension = nombre.substring(nombre.lastIndexOf('.') + 1);
        String duracion = "0:00"; // Placeholder
        String ruta = archivo.getAbsolutePath();
        String tamanio = archivo.length() / 1024 + " KB";
        return new Video(nombre, extension, duracion, ruta, tamanio);
    }

    private void showAlert(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private void reproducirVideo(String ruta, JFXPanel videoPanel) {
        Platform.runLater(() -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            Media media = new Media(new File(ruta).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            MediaView mediaView = new MediaView(mediaPlayer);

            Scene scene = new Scene(new javafx.scene.Group(mediaView));
            videoPanel.setScene(scene);
            mediaPlayer.play();
        });
    }

}

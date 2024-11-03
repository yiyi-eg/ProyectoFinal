package com.mycompany.proyecto;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Clase principal para manejar la visualización de imágenes
public final class Imagen extends JFrame {
    private JTextField rutaTexto;
    private JTable tablaImagenes;
    private final ArrayList<ImagenData> listaImagenes = new ArrayList<>();
    private ImageView imageView;

    public Imagen() {
        setTitle("IMÁGENES");
    setSize(1200, 720);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    rutaTexto = new JTextField(40);
    JButton btnSeleccionarRuta = new JButton("Seleccionar Ruta");
    btnSeleccionarRuta.addActionListener(e -> seleccionarRuta());
    JButton btnBuscar = new JButton("Buscar");
    btnBuscar.addActionListener(e -> cargarImagenes());

    // Crear botón de regresar
    JButton btnRegresar = crearBotonIcono("/iconos/regreso.png", 35, "Regresar");
    btnRegresar.addActionListener(e -> dispose()); // Cierra la ventana actual

    // Agregar botones y campo de texto al panel superior
    topPanel.add(rutaTexto);
    topPanel.add(btnSeleccionarRuta);
    topPanel.add(btnBuscar);
    topPanel.add(btnRegresar);
    add(topPanel, BorderLayout.NORTH);

    // Configuración de la tabla
    tablaImagenes = new JTable(new DefaultTableModel(new Object[]{"Nombre", "Extensión"}, 0));
    JScrollPane scrollPane = new JScrollPane(tablaImagenes);
    add(scrollPane, BorderLayout.CENTER);

    // Crear panel de visualización de imágenes con JFXPanel
    JFXPanel imagePanel = new JFXPanel();
    imagePanel.setPreferredSize(new Dimension(800, 450));
    add(imagePanel, BorderLayout.SOUTH);

    tablaImagenes.getSelectionModel().addListSelectionListener(event -> {
        int selectedRow = tablaImagenes.getSelectedRow();
        if (selectedRow != -1) {
            ImagenData imagen = listaImagenes.get(selectedRow);
            mostrarImagen(imagen.getRuta(), imagePanel);
        }
    });
}

// Método modificado para crear un botón con un ícono (si está disponible) o texto
public JButton crearBotonIcono(String rutaIcono, int tamano, String textoAlternativo) {
    JButton boton = new JButton(textoAlternativo); // Texto alternativo por defecto
    try {
        ImageIcon im = new ImageIcon(getClass().getResource(rutaIcono));
        if (im.getIconWidth() > 0) { // Verificar si el ícono se ha cargado correctamente
            boton.setIcon(new ImageIcon(im.getImage().getScaledInstance(tamano, tamano, java.awt.Image.SCALE_SMOOTH)));
            boton.setText(""); // Quitar el texto si el ícono se carga
        }
    } catch (Exception e) {
        System.out.println("Icono no encontrado: " + rutaIcono);
    }
    boton.setOpaque(false);
    boton.setContentAreaFilled(false);
    boton.setBorderPainted(false);
    boton.setFocusPainted(false);
    return boton;
}

    private BufferedImage redimensionarImagen(String ruta, int ancho, int alto) {
        try {
            BufferedImage imagenOriginal = ImageIO.read(new File(ruta));
            BufferedImage imagenEscalada = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = imagenEscalada.createGraphics();
            g.drawImage(imagenOriginal, 0, 0, ancho, alto, null);
            g.dispose();
            return imagenEscalada;
        } catch (IOException e) {
            return null;
        }
    }

    private void seleccionarRuta() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = fileChooser.getSelectedFile();
            rutaTexto.setText(selectedDirectory.getAbsolutePath());
            cargarImagenes();
        }
    }

    private void cargarImagenes() {
        String rutaSeleccionada = rutaTexto.getText();
        if (!rutaSeleccionada.isEmpty()) {
            File directorio = new File(rutaSeleccionada);
            if (directorio.isDirectory()) {
                List<ImagenData> imagenes = buscarImagenesEnDirectorio(directorio);
                DefaultTableModel model = (DefaultTableModel) tablaImagenes.getModel();
                model.setRowCount(0); // Limpiar la tabla
                listaImagenes.clear();
                listaImagenes.addAll(imagenes);
                for (ImagenData imagen : imagenes) {
                    model.addRow(new Object[]{imagen.getNombre(), imagen.getExtension()});
                }
            } else {
                showAlert("Error", "La ruta seleccionada no es un directorio válido.");
            }
        } else {
            showAlert("Error", "Seleccione una ruta válida.");
        }
    }

    public List<ImagenData> buscarImagenesEnDirectorio(File directorio) {
        List<ImagenData> imagenes = new ArrayList<>();
        File[] archivos = directorio.listFiles((dir, name) -> name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".gif"));
        if (archivos != null) {
            for (File archivo : archivos) {
                ImagenData imagen = obtenerInformacionImagen(archivo);
                if (imagen != null) {
                    imagenes.add(imagen);
                }
            }
        }
        return imagenes;
    }

    private ImagenData obtenerInformacionImagen(File archivo) {
        String nombre = archivo.getName();
        String extension = nombre.substring(nombre.lastIndexOf('.') + 1);
        String ruta = archivo.getAbsolutePath();
        return new ImagenData(nombre, extension, ruta);
    }

    private void showAlert(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

   private void mostrarImagen(String ruta, JFXPanel imagePanel) {
    Platform.runLater(() -> {
        BufferedImage imagenRedimensionada = redimensionarImagen(ruta, 800, 450); // Redimensionar a 800x450

        if (imagenRedimensionada != null) {
            Image image = SwingFXUtils.toFXImage(imagenRedimensionada, null); // Convertir a imagen de JavaFX
            imageView = new ImageView(image);
            imageView.setPreserveRatio(true);

            Scene scene = new Scene(new javafx.scene.Group(imageView));
            imagePanel.setScene(scene);
        } else {
            System.out.println("No se pudo cargar la imagen.");
        }
    });
}

}

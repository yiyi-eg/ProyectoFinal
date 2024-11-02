package com.mycompany.proyecto;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

public class Musica extends JFrame {
    //<------------------------ ATRIBUTOS ------------------------------>
    private JPanel panel, panelControles,panelControles2;
    private JButton btnRegresar, btnBuscar, btnSeleccionarRuta,boton;
    private JButton btnAnterior, btnReproducir, btnSiguiente, btnPausar; 
    private JTextField rutaTexto;
    private JTable tablaCanciones;
    private DefaultTableModel modeloTabla;
    private JScrollPane scrollPane;
    private String rutaSeleccionada;
    private JButton btnMostrarDuplicados;
    private JButton btnEliminarDuplicados;
    private JButton btnEliminarSeleccionados;
    private JLabel lblEspacioTotal;
    private ArrayList<Cancion> listaCanciones = new ArrayList<>(); 
    private ReproductorMusica reproductorMusica;
    //<---------------------- METODO CONSTRUCTOR ---------------------------->
    public Musica() {
        this.listaCanciones = new ArrayList<>();
        this.setTitle("CANCIONES");
        this.setSize(1500, 720);
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
    //<-------- FUNCION DONDE SE ALOJAN CADA COMPONENTE DEL SISTEMA -------->
    private void componentes() {
        
        panel();
        btnRegresar();
        btnRegresarAcciones();
        campoRuta();
        btnSeleccionarRuta();
        btnBuscarCanciones();
        tablaCanciones();
        agregarControlesMusica();
        btnDeDuplicados();
        efectoHover(btnRegresar);
        efectoHover(btnSeleccionarRuta);
    }
    //<------ PANEL PRINCIPAL PARA PODER PINTAR TODO LO DEL SISTEMA ------>
    private void panel() {
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon imagen = new ImageIcon(getClass().getResource("/FONDOS/bac.jpg"));
                g.drawImage(imagen.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        
        panel.setLayout(new BorderLayout()); 
        this.getContentPane().add(panel);
    }
    //<--------- JTEXFIEL PARA PINTAR LA RUTA -------------------->
    private void campoRuta() {
        rutaTexto = new JTextField();
        panel.add(rutaTexto);
    }
    //<--------- FUNCION PARA PODER SELECCIONAR LA RUTA --------->
    private void seleccionarRuta() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de música", "mp3", "wav"));
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            rutaTexto.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }
    //<- FUNCIONES PARA MOSTRAR LAS CANCIONES, CARGAR LAS CANCIONES Y BUSCAR->
    private void tablaCanciones() {
     String[] columnas = {"Nombre","extencion","Artista", "Álbum", "Genero", "Duracion", "Año","Ruta","Tamaño"};
     modeloTabla = new DefaultTableModel(columnas, 0) {
         @Override
         public boolean isCellEditable(int row, int column) {
             return false;
         }
     };

     tablaCanciones = new JTable(modeloTabla);
     tablaCanciones.setOpaque(false); // Hace que la tabla en sí sea transparente
     ((DefaultTableCellRenderer) tablaCanciones.getDefaultRenderer(Object.class)).setOpaque(false); // Hace las celdas transparentes

     JTableHeader header = tablaCanciones.getTableHeader();
     header.setOpaque(false); // Hace el encabezado transparente
     header.setForeground(Color.MAGENTA);
     header.setFont(new Font("Times New Roman", Font.BOLD, 18));
     header.setBackground(new Color(100, 100, 100, 100)); // Color semi-transparente para el encabezado

     // Personalizar el renderizador de celdas para cambiar el color del texto
     tablaCanciones.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
         @Override
         public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
             Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
             cell.setForeground(Color.GREEN); // Cambia el color del texto aquí
             return cell;
         }
     });

     scrollPane = new JScrollPane(tablaCanciones);
     scrollPane.setOpaque(false);
     scrollPane.getViewport().setOpaque(false);
     scrollPane.getViewport().setBackground(new Color(0, 0, 0, 0)); // Fondo del viewport completamente transparente
     scrollPane.setBorder(BorderFactory.createEmptyBorder());

     panel.add(scrollPane);
 }
    private void cargarCanciones() {
       new Thread(() -> {
           modeloTabla.setRowCount(0);
           listaCanciones.clear();

           ArrayList<Cancion> canciones = buscarCancionesEnDirectorio(new File(rutaSeleccionada));

           SwingUtilities.invokeLater(() -> {
               for (Cancion cancion : canciones) {
                   modeloTabla.addRow(new Object[]{
                       cancion.getNombre(),
                       cancion.getExtencion(),
                       cancion.getArtista(),
                       cancion.getAlbum(),
                       cancion.getGenero(),
                       cancion.getDuracion(),
                       cancion.getAnio(),
                       cancion.getRuta(),
                       cancion.getTamanio()
                   });
               }
               btnAnterior.setEnabled(!canciones.isEmpty());
               btnReproducir.setEnabled(!canciones.isEmpty());
               btnSiguiente.setEnabled(!canciones.isEmpty());
               btnPausar.setEnabled(!canciones.isEmpty());
           });

           reproductorMusica = ReproductorMusica.fromCanciones(listaCanciones);
       }).start();
   }
    public ArrayList<Cancion> buscarCancionesEnDirectorio(File directorio) {
        ArrayList<Cancion> canciones = new ArrayList<>();
        if (directorio.isDirectory()) {
            for (File archivo : directorio.listFiles()) {
                if (archivo.getName().endsWith(".mp3") || archivo.getName().endsWith(".wav")) {
                    Cancion cancion = obtenerInformacionCancion(archivo);
                    if (cancion != null) {  // Verificación adicional por si algún archivo falla
                        listaCanciones.add(cancion);  
                        canciones.add(cancion);
                    }
                }
            }
        }
        return canciones;
    }
    //<- FUNCIONES PARA OBTENER LOS METADATOS DE LAS CANCUINES Y MOSTRAR EL ESPACIO ->
    private Cancion obtenerInformacionCancion(File archivo) {
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
        } catch (CannotReadException | IOException | TagException | ReadOnlyFileException 
                 | InvalidAudioFrameException e) {
            System.out.println("Error al obtener metadatos del archivo: " + archivo.getName() + " - " + e.getMessage());
            return null; // Devuelve null si ocurre algún error
        }

        return new Cancion(nombre, extension, duracion, genero, artista, album, anio, ruta, tamanio);
    }
    private void actualizarEspacioTotal() {
    long espacioTotal = listaCanciones.stream().mapToLong(cancion -> new File(cancion.getRuta()).length()).sum();
    lblEspacioTotal.setText("Espacio total ocupado: " + espacioTotal / (1024 * 1024) + " MB");
}
    //<- FUNCIONES PARA MOSTRAR DUPLICADOS Y ELIMINAR, TAMBIEN PODER SELECCIONAR EH ELIMINAR ->
    private void mostrarDuplicados() {
    Set<String> archivosUnicos = new HashSet<>();
    ArrayList<Cancion> duplicados = new ArrayList<>();

    for (Cancion cancion : listaCanciones) {
        String key = cancion.getNombre() + cancion.getTamanio() + cancion.getDuracion();
        if (!archivosUnicos.add(key)) {
            duplicados.add(cancion);
        }
    }

    if (duplicados.isEmpty()) {
        JOptionPane.showMessageDialog(null, "No se encontraron canciones duplicadas.");
        return;
    }

    modeloTabla.setRowCount(0); // Limpiar tabla
    for (Cancion cancion : duplicados) {
        modeloTabla.addRow(new Object[]{
            cancion.getNombre(), cancion.getExtencion(), cancion.getArtista(), cancion.getAlbum(),
            cancion.getGenero(), cancion.getDuracion(), cancion.getAnio(), cancion.getRuta(), cancion.getTamanio()
        });
    }
    }
    public void eliminarDuplicados() {
    Set<String> cancionesUnicas = new HashSet<>();
    Iterator<Cancion> iterador = listaCanciones.iterator();
    boolean hayDuplicados = false;

    while (iterador.hasNext()) {
        Cancion cancion = iterador.next();
        String key = cancion.getNombre() + cancion.getTamanio() + cancion.getDuracion();

        // Si ya existe la clave, es duplicado y lo eliminamos
        if (!cancionesUnicas.add(key)) {
            iterador.remove();
            hayDuplicados = true;
        }
    }

    if (hayDuplicados) {
        JOptionPane.showMessageDialog(this, "Canciones duplicadas eliminadas.");
        actualizarTabla();
    } else {
        JOptionPane.showMessageDialog(this, "No se encontraron canciones duplicadas.");
    }
    actualizarEspacioTotal();
    
}
    public void eliminarSeleccionados() {
        int[] filasSeleccionadas = tablaCanciones.getSelectedRows();

        if (filasSeleccionadas.length == 0) {
            JOptionPane.showMessageDialog(this, "No hay canciones seleccionadas para eliminar.");
            return;
        }

        // Recorremos las filas seleccionadas en orden descendente para evitar errores de índice
        for (int i = filasSeleccionadas.length - 1; i >= 0; i--) {
            int fila = filasSeleccionadas[i];
            String rutaCancion = (String) modeloTabla.getValueAt(fila, 7); // Columna de la ruta
            listaCanciones.removeIf(cancion -> cancion.getRuta().equals(rutaCancion));
            modeloTabla.removeRow(fila);
        }

        JOptionPane.showMessageDialog(this, "Canciones seleccionadas eliminadas.");
        actualizarEspacioTotal();
        
    }
    // Actualiza la tabla con las canciones restantes
    private void actualizarTabla() {
        modeloTabla.setRowCount(0); // Limpia la tabla
        for (Cancion cancion : listaCanciones) {
            modeloTabla.addRow(new Object[]{
                cancion.getNombre(), cancion.getExtencion(), cancion.getArtista(), cancion.getAlbum(),
                cancion.getGenero(), cancion.getDuracion(), cancion.getAnio(), cancion.getRuta(), cancion.getTamanio()
            });
        }
    }
    //<---------------------- BOTONES -------------------------->
    public JButton crearBotonIcono(String rutaIcono, int tamano) {
        boton = new JButton();
        ImageIcon im = new ImageIcon(getClass().getResource(rutaIcono));
        boton.setIcon(new ImageIcon(im.getImage().getScaledInstance(tamano, tamano, Image.SCALE_SMOOTH)));
        boton.setOpaque(false);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        return boton;
    }
    private void btnRegresar() {
        btnRegresar = crearBotonIcono("/iconos/regreso.png", 35);
        panel.add(btnRegresar);
    }
    private void btnRegresarAcciones() {
        btnRegresar.addActionListener(evt -> {
            if (reproductorMusica != null) {
                reproductorMusica.detenerAlSalir();
            }
            dispose();
        });
    }
    private void btnSeleccionarRuta() {
        btnSeleccionarRuta = crearBotonIcono("/iconos/buscar-archivo.png", 35);
        btnSeleccionarRuta.addActionListener(e -> seleccionarRuta());
        panel.add(btnSeleccionarRuta);
    }
    private void btnBuscarCanciones() {
        btnBuscar = crearBotonIcono("/iconos/buscando.png", 35);
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
    //<------ FUNCION PARA AGREGAR LOS BOTONES DE DUPLICADOS ------->
    private void btnDeDuplicados(){
        panelControles2 = new JPanel((new FlowLayout(FlowLayout.CENTER)));
        panelControles2.setOpaque(false);
        
        btnMostrarDuplicados = new JButton("Mostrar Duplicados");
        
        btnMostrarDuplicados.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mostrarDuplicados();
            }
        });
        
        btnEliminarDuplicados = new JButton("Eliminar Duplicados");
        
        btnEliminarDuplicados.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                eliminarDuplicados();
            }
        });
        
        btnEliminarSeleccionados = new JButton("Eliminar Seleccionados");
        
        btnEliminarSeleccionados.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                eliminarSeleccionados();
            }
        });
        
        lblEspacioTotal = new JLabel("Espacio total ocupado: 0 KB");
        lblEspacioTotal.setForeground(Color.red);
        actualizarEspacioTotal();

        panel.add(panelControles2);
        panelControles2.add(btnMostrarDuplicados);
        panelControles2.add(btnEliminarDuplicados);
        panelControles2.add(btnEliminarSeleccionados);
        panelControles2.add(lblEspacioTotal);
    }
   //<------ FUNCION PARA AGREGAR LOS BOTONES PARA CONTROLAR LA MUSICA ------->
    private void agregarControlesMusica() {
        panelControles = new JPanel((new FlowLayout(FlowLayout.CENTER)));
        panelControles.setOpaque(false);
        
        btnAnterior = crearBotonIcono("/iconos/atras.png", 35);
        btnAnterior.setEnabled(false);
        btnAnterior.addActionListener(e -> reproductorMusica.anterior());
        
        btnReproducir = crearBotonIcono("/iconos/play.png", 35);
        btnReproducir.setEnabled(false);
        btnReproducir.addActionListener(e -> reproductorMusica.reproducir());
        
        btnPausar = crearBotonIcono("/iconos/pausa.png", 35);
        btnPausar.setEnabled(false);
        btnPausar.addActionListener(e -> reproductorMusica.pausar());
        
        btnSiguiente = crearBotonIcono("/iconos/adelante.png", 35);
        btnSiguiente.setEnabled(false);
        btnSiguiente.addActionListener(e -> reproductorMusica.siguiente());
        
        panel.add(panelControles);
        panelControles.add(btnSiguiente);
        panelControles.add(btnPausar);
        panelControles.add(btnReproducir);
        panelControles.add(btnAnterior);
    }
    //<--------------- EFECTO PARA LOS BOTONES ----------------->
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
    //<- FUNCION PARA PODER HACER RESPONSIVE ->
    private void resizeComponents() {
        Dimension size = panel.getSize();
        
        btnRegresar.setBounds((int) (size.width * 0.02), (int) (size.height * 0.02), (int) (size.width * 0.04), (int) (size.height * 0.06));
        
        rutaTexto.setBounds((int) (size.width * 0.07), (int) (size.height * 0.03), (int) (size.width * 0.3), (int) (size.height * 0.05));
        
        btnSeleccionarRuta.setBounds((int) (size.width * 0.38), (int) (size.height * 0.03), (int) (size.width * 0.05), (int) (size.height * 0.05));
        
        btnBuscar.setBounds((int) (size.width * 0.42), (int) (size.height * 0.03), (int) (size.width * 0.05), (int) (size.height * 0.05));
        
        panelControles2.setBounds((int) (size.width * 0.47), (int) (size.height * 0.03), (int) (size.width * 0.49), (int) (size.height * 0.05));
        
        btnMostrarDuplicados.setBounds((int) (size.width * 0.01), (int) (size.height * 0.01), (int) (size.width * 0.1), (int) (size.height * 0.05));
        
        btnEliminarDuplicados.setBounds((int) (size.width * 0.12), (int) (size.height * 0.01), (int) (size.width * 0.1), (int) (size.height * 0.05));
        
        btnEliminarSeleccionados.setBounds((int) (size.width * 0.23), (int) (size.height * 0.01), (int) (size.width * 0.1), (int) (size.height * 0.05));
        
        lblEspacioTotal.setBounds((int) (size.width * 0.34), (int) (size.height * 0.01), (int) (size.width * 0.15), (int) (size.height * 0.05));
        
        scrollPane.setBounds((int) (size.width * 0.025), (int) (size.height * 0.1), (int) (size.width * 0.95), (int) (size.height * 0.7));
        
        panelControles.setBounds((int) (size.width * 0.25), (int) (size.height * 0.85), (int) (size.width * 0.5), (int) (size.height * 0.1));
        
        btnAnterior.setBounds((int) (size.width * 0.05), (int) (size.height * 0.023), (int) (size.width * 0.09), (int) (size.height * 0.06));
        
        btnReproducir.setBounds((int) (size.width * 0.15), (int) (size.height * 0.023), (int) (size.width * 0.09), (int) (size.height * 0.06));
        
        btnPausar.setBounds((int) (size.width * 0.25), (int) (size.height * 0.023), (int) (size.width * 0.09), (int) (size.height * 0.06));
        
        btnSiguiente.setBounds((int) (size.width * 0.35), (int) (size.height * 0.023), (int) (size.width * 0.09), (int) (size.height * 0.06));
       
    }
}
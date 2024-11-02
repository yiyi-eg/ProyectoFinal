package com.mycompany.proyecto;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class Ventana1 extends JFrame {
    public JPanel panel;
    private JLabel txtPrincipal, txtImagenes, txtVideos,txtMusica;
    public JButton botonImagen,botonCanciones,botonVideo;
    
    public Ventana1() {
        this.setTitle("ADMINISTRADOR DE ARCHIVOS MULTIMEDIA");
        this.setSize(1200, 720);
        this.setLocationRelativeTo(null);
        this.setMinimumSize(new Dimension(720, 720));
         // Agregar ComponentListener para hacer la ventana responsive
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeComponents();
            }
        });
        this.Componentes();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void Componentes() {
        panel();
        textoPrincipal();
        txtImagenes() ;
        txtVideos();
        txtMusica();
        botonImagenes();
        botonVideo();
        botonCanciones();
        EfectoHover(botonImagen);
        EfectoHover(botonCanciones);
        EfectoHover(botonVideo);
        accionesBotones();
    }

    //<------------------- PANEL ------------------------->
    private void panel() {
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon imagen = new ImageIcon(getClass().getResource("/FONDOS/bac3.jpg"));
                g.drawImage(imagen.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };

        panel.setLayout(null);
        panel.setBackground(Color.darkGray);
        this.getContentPane().add(panel);
        
    }

    // <------------------------------- ETIQUETAS ---------------------------------->
    
    private void textoPrincipal(){
        txtPrincipal = new JLabel("ADMINISTRADOR DE ARCHIVOS MULTIMEDIA", SwingConstants.CENTER);
        txtPrincipal.setForeground(Color.GREEN);
        txtPrincipal.setFont(new Font("times new roman", Font.BOLD, 45));
        panel.add(txtPrincipal);
        
    }
    private void txtImagenes() {
        txtImagenes = new JLabel("IMAGENES", SwingConstants.CENTER);
        txtImagenes.setForeground(Color.ORANGE);
        txtImagenes.setFont(new Font("times new roman", Font.BOLD, 30));
        panel.add(txtImagenes);
}
    private void txtVideos(){
        txtVideos = new JLabel("VIDEOS", SwingConstants.CENTER);
        txtVideos.setForeground(Color.ORANGE);
        txtVideos.setFont(new Font("times new roman", Font.BOLD, 30));
        panel.add(txtVideos);

    }
    private void txtMusica(){
        txtMusica = new JLabel("MUSICA", SwingConstants.CENTER);
        txtMusica.setForeground(Color.ORANGE);
        txtMusica.setFont(new Font("times new roman", Font.BOLD, 30));
        panel.add(txtMusica);

    }
    // <------------------------------ BOTONES -------------------------------->
    private void botonImagenes() {
        botonImagen = new JButton();

        // Cargar la imagen del botón
        ImageIcon im = new ImageIcon(getClass().getResource("/iconos/carpeta-del-album.png"));
        
        // Asignar el ícono al botón (ajustado a su tamaño)
        botonImagen.setIcon(new ImageIcon(im.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH)));  // Ajusta según sea necesario

        //hacemos el boton transaparente 
        botonImagen.setOpaque(false);
        //desactivamos el relleno
        botonImagen.setContentAreaFilled(false);
        // Eliminar el borde del botón (incluye el borde de la imagen)
        botonImagen.setBorderPainted(false);
        // Eliminar el borde de enfoque (focus border)
        botonImagen.setFocusPainted(false);
        
        panel.add(botonImagen);
}
    private void botonVideo(){
        botonVideo = new JButton();
        ImageIcon im2 = new ImageIcon(getClass().getResource("/iconos/carpeta.png"));
        botonVideo.setIcon(new ImageIcon(im2.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
    
        //hacemos el boton transaparente 
        botonVideo.setOpaque(false);
        //desactivamos el relleno
        botonVideo.setContentAreaFilled(false);
        // Eliminar el borde del botón (incluye el borde de la imagen)
        botonVideo.setBorderPainted(false);
        // Eliminar el borde de enfoque (focus border)
        botonVideo.setFocusPainted(false);
        panel.add(botonVideo);
           
    }
    private void botonCanciones() {
    botonCanciones = new JButton();
   
    ImageIcon im2 = new ImageIcon(getClass().getResource("/iconos/carpeta-de-musica.png"));
    botonCanciones.setIcon(new ImageIcon(im2.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
    
    botonCanciones.setOpaque(false);
    botonCanciones.setContentAreaFilled(false);
    botonCanciones.setBorderPainted(false);  
    botonCanciones.setFocusPainted(false);   

    
    panel.add(botonCanciones);
}

    private void EfectoHover(JButton boton) {
    boton.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent evt) {
            // Cambiar el color de fondo cuando el mouse pasa sobre el botón
            boton.setBackground(Color.DARK_GRAY); 
            boton.setOpaque(true); // Hacer visible el fondo al pasar el mouse
        }

        @Override
        public void mouseExited(MouseEvent evt) {
            // Restaurar el color de fondo original cuando el mouse sale
            boton.setBackground(null);
            boton.setOpaque(false); // Restaurar la transparencia
        }
    });
}
    private void accionesBotones(){
    
        botonCanciones.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            Musica m = new Musica();
            m.setVisible(true);
        }
    });
        
        botonVideo.addMouseListener(new java.awt.event.MouseAdapter() {
    
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt){
        Video v = new Video();
        v.setVisible(true);
        }
        
    });
        botonImagen.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt){
            Imagen i = new Imagen();
            i.setVisible(true );
            }
        
        });
    }
    private void resizeComponents() {
        // Redimensionar y reposicionar componentes según el tamaño actual de la ventana
        Dimension size = panel.getSize();

        txtPrincipal.setBounds((int) (size.width * 0.04), (int) (size.height * 0.05), (int) (size.width * 0.9), (int) (size.height * 0.05));
        txtImagenes.setBounds((int) (size.width * 0.1), (int) (size.height * 0.35), (int) (size.width * 0.2), (int) (size.height * 0.05));
        txtVideos.setBounds((int) (size.width * 0.7), (int) (size.height * 0.35), (int) (size.width * 0.2), (int) (size.height * 0.05));
        txtMusica.setBounds((int)(size.width * 0.4), (int)(size.height * 0.35),(int)(size.width * 0.2), (int)(size.height * 0.05));
        
        botonImagen.setBounds((int) (size.width * 0.17), (int) (size.height * 0.421), (int) (size.width * 0.06),(int) (size.height * 0.09));
        botonCanciones.setBounds((int) (size.width * 0.472), (int) (size.height * 0.421), (int) (size.width * 0.06), (int) (size.height * 0.09));
        botonVideo.setBounds((int) (size.width * 0.77), (int) (size.height * 0.421), (int) (size.width * 0.06), (int) (size.height * 0.09));
        
    }
}
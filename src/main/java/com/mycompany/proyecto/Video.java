package com.mycompany.proyecto;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Video extends JFrame{
    
        private JPanel panelMusic;
    public Video (){
        this.setTitle("VIDEOS");
        this.setSize(1200, 720);
        this.setLocationRelativeTo(null);
        this.setMinimumSize(new Dimension(720, 720));
         
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
               resizeComponents();
            }
        });
        componentes();
    }
    private void componentes(){
        panelMusica();
    
    }
    private void panelMusica(){
    panelMusic = new JPanel(){
        @Override
        protected void paintComponent(Graphics g){
        super.paintComponent(g);
        ImageIcon imagen = new ImageIcon(getClass().getResource("/FONDOS/backVideo.png"));
        g.drawImage(imagen.getImage(),0, 0, getWidth(),getHeight(),this);
        }
    };
    panelMusic.setLayout(new BorderLayout());
    this.getContentPane().add(panelMusic);
    
    }
    private void resizeComponents() {
        Dimension size = panelMusic.getSize();

    }
}

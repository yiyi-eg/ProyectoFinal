package com.mycompany.proyecto;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JFrame;

public class Imagen extends JFrame{
    
    public Imagen(){
    
    this.setTitle("IMAGENES");
        this.setSize(1080, 720);
        this.setLocationRelativeTo(null);
        this.setMinimumSize(new Dimension(720, 720));
         // Agregar ComponentListener para hacer la ventana responsive
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                //resizeComponents();
            }
        });
    
    }
    
}

import javax.swing.*;
import java.awt.*;

public class Interfaz {
    ///Creamos una ventana con Swing para el chat
    private JFrame ventana;
    public JTextField texto;
    public JTextArea area;
    public JButton boton;

    public Interfaz() {
        ventana = new JFrame("Chat");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(400,400);
        ventana.setLayout(null);
        texto = new JTextField();
        texto.setBounds(10,350,300,30);
        area = new JTextArea();
        area.setBounds(10,10,300,330);
        boton = new JButton("Enviar");
        boton.setBounds(320,350,70,30);
        ventana.add(texto);
        ventana.add(area);
        ventana.add(boton);
        ventana.setVisible(true);
    }
}

package cliente;

import javax.swing.*;
import java.io.IOException;

public class Interfaz {
    ///Creamos una ventana con Swing para el chat
    private final JFrame ventana;
    public JTextField textoMensaje, textoNombre, textoDestinatario;
    public JTextArea area;
    public JButton boton, emoji;
    public JLabel labelMensaje, labelDestinatario;
    String nombre;
    private Cliente cliente;

    String bear = "\ud83d\udc3b";

    int bearCodepoint = bear.codePointAt(bear.offsetByCodePoints(0, 0));
    int mysteryAnimalCodepoint = bearCodepoint + 1;
    char[] mysteryAnimal = {Character.highSurrogate(mysteryAnimalCodepoint),
            Character.lowSurrogate(mysteryAnimalCodepoint)};

    public Interfaz() {
        ventana = new JFrame("Chat");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(400,500);
        ventana.setLayout(null);
        ventana.setResizable(false);

        textoNombre = new JTextField();
        textoNombre.setBounds(10,10,100,30);
        textoNombre.setEditable(false);

        labelDestinatario = new JLabel("Destinatario:");
        labelDestinatario.setBounds(180,10,100,30);

        textoDestinatario = new JTextField();
        textoDestinatario.setBounds(260,10,100,30);

        area = new JTextArea();
        area.setBounds(10,50,360,300);
        area.setEditable(false);

        labelMensaje = new JLabel("Mensaje");
        labelMensaje.setBounds(10,350,100,30);

        textoMensaje = new JTextField();
        textoMensaje.setBounds(10,373,360,30);

        boton = new JButton("Enviar");
        boton.setBounds(270,410,100,30);

        emoji = new JButton(new String(mysteryAnimal));
        emoji.setBounds(10,410,60,30);

        ventana.add(textoMensaje);
        ventana.add(labelMensaje);
        ventana.add(textoDestinatario);
        ventana.add(labelDestinatario);
        ventana.add(textoNombre);
        ventana.add(area);
        ventana.add(boton);
        ventana.setVisible(true);
        ventana.add(emoji);

        nombre = pedirNombre();
        textoNombre.setText(nombre);

        boton.addActionListener(e -> {
            if (textoMensaje.getText().isEmpty()) return;
            String mensaje = nombre + ": " + textoMensaje.getText();
            String destinatario = textoDestinatario.getText();

            try {
                if (destinatario.isEmpty()) {
                    cliente.enviaMensajeMulticast(mensaje);
                } else {
                    cliente.enviaMensajePrivado(destinatario, "[Privado] " + mensaje);
                }

                textoMensaje.setText("");
                textoMensaje.requestFocus();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        emoji.addActionListener(e -> textoMensaje.setText(textoMensaje.getText() + String.valueOf(mysteryAnimal)));
    }

    void append(String texto) {
        area.append(texto + "\n");
    }

    //Solicitamos el nombre con una ventana de dialogo
    public String pedirNombre(){
        String nombre = null;

        while (nombre == null || nombre.isEmpty()) {
            nombre = JOptionPane.showInputDialog(ventana, "Introduce tu nombre");
        }

        return nombre;
    }

    public static void main(String[] args) {
        Interfaz interfaz = new Interfaz();
        interfaz.cliente = new Cliente(interfaz);
    }
}


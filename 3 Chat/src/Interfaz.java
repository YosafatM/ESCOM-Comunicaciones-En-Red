import javax.swing.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Interfaz {
    ///Creamos una ventana con Swing para el chat
    private final JFrame ventana;
    public JTextField textoMensaje, textoNombre;
    public JTextArea area;
    public JScrollPane scroll;
    public JButton boton;
    String nombre;

    public Interfaz() {
        ventana = new JFrame("Chat");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(400,300);
        ventana.setLayout(null);

        textoNombre = new JTextField();
        textoNombre.setBounds(10,10,100,30);
        textoNombre.setEditable(false);

        area = new JTextArea();
        area.setBounds(10,50,360,100);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        textoMensaje = new JTextField();
        textoMensaje.setBounds(10,170,360,30);

        boton = new JButton("Enviar");
        boton.setBounds(270,210,100,30);

        ventana.add(textoMensaje);
        ventana.add(textoNombre);
        ventana.add(area);
        ventana.add(boton);
        ventana.setVisible(true);

        nombre = pedirNombre();
        textoNombre.setText(nombre);

        boton.addActionListener(e -> {
            if (textoMensaje.getText().isEmpty()) return;
            String mensaje = nombre + ": " + textoMensaje.getText();

            try {
                Chat.envia_mensaje_multicast(mensaje.getBytes(StandardCharsets.UTF_8));
                textoMensaje.setText("");
                textoMensaje.requestFocus();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
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

    public static void main(String[] args) throws IOException {
        Interfaz interfaz = new Interfaz();
        new Chat.Worker(interfaz).start();
    }
}

package cliente;

import static cliente.SockEscuchaMulticast.K_IP;
import static cliente.SockEscuchaMulticast.K_PORT;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import usuario.DatosUsuario;

public class EnviarMensaje extends Thread {

    DatosUsuario du;    //Objeto que será enviado a Busqueda.java
    String userName;
    Scanner s = new Scanner(System.in);

    public EnviarMensaje(String userName) {
        this.userName = userName;
    }

}

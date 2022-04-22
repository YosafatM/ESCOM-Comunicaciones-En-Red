package cliente;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

// Clase que sirve para enviar mensajes MULTICAST
public class SockEscuchaMulticast extends Thread {
    Interfaz interfaz;
    MulticastSocket socket;                                     //Multisocket
    DatagramSocket socketRecibe;
    static final int K_PORT = 10000;                            //Puerto donde se ejecuta la app
    static final int K_SIZE = 200;                              //Tamaño del arreglo de bytes
    static final String K_IP = "230.0.0.0";                     //Dirección IP de clase D
    
    public SockEscuchaMulticast(Interfaz interfaz) throws IOException {          //Constructor
        this.interfaz = interfaz;
        InetAddress ip_grupo = InetAddress.getByName(K_IP);     //ip del grupo MULTICAST
        socket = new MulticastSocket(K_PORT);                   //puerto MULTICAST  
        socket.joinGroup(ip_grupo);                             //Uniendo la ip al grupo MULTICAST
    }

    public void run() {
        try {
            int x = 0;
            while (true) {  //Ciclo infinito 
                System.out.println("Esperando mensaje multicast...");
                byte[] buffer = recibe_mensaje_multicast(socket, K_SIZE);   //Se recibe el mensaje MULTICAST, SE DETIENE la ejecución hasta que se reciba algo 
                String mensaje = new String(buffer, StandardCharsets.UTF_8);
                interfaz.append(mensaje);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static byte[] recibe_mensaje_multicast(MulticastSocket socket, int longitud_mensaje) throws IOException {
        byte[] buffer = new byte[longitud_mensaje];                         //Longitud mensaje determina el tamaño del ARRAY 
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
        socket.receive(paquete);
        System.out.println();
        return paquete.getData();   //Se retorna la información del paquete 
    }
}


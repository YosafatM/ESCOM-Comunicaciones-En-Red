package cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketEscucha extends Thread {
    Interfaz interfaz;

    public SocketEscucha(Interfaz interfaz) {
        this.nombreUsuario = interfaz.nombre;
        this.interfaz = interfaz;
    }

    public DatagramSocket socketRecibe;
    private int longuitudMsj = 200;
    private String nombreUsuario;
    public DatagramSocket socketEnvia;

    public static byte[] recibe_mensaje_multicast2(DatagramSocket socket, int longitud_mensaje) throws IOException {
        byte[] buffer = new byte[longitud_mensaje];                         //Longitud mensaje determina el tamaño del ARRAY 
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
        socket.receive(paquete);    //Aquí el HILO se detiene HASTA QUE RECIBA ALGO 
        System.out.println();
        return paquete.getData();   //Se retorna la información del paquete 
    }

    //El método siguiente OCURRE cada vez que se ejecute un CLIENTE
    public void enviaDatosAlServidor(){

        try {
            int pto = 1234; //Pto DESTINO, es el del SERVIDOR
            String dir = "127.0.0.1";   //Ip destino, LA PROPIA MÁQUINA 
            InetAddress dst = InetAddress.getByName(dir);
            
            socketRecibe = new DatagramSocket();   //No se asigna un puerto, SE GENERA UNO ALEATORIO DISPONIBLE
                                                   //Este puerto se guardará en du.setPto(p.getPort()) del Archivo "IPyPtoAleatorio. java" (L. 44) 
            
            //Si no se asigna una dst(InnetAddress) se asigna la 127.0.0.1 POR DEFECTO
            
            byte[] b = nombreUsuario.getBytes();    //Lo que se va a mandar a una LISTA del lado del SERVIDOR, es el nombre de USUARIO y el PUERTO.
            DatagramPacket p = new DatagramPacket(b, b.length, dst, pto);   //dst y pto son direcciones DESTINO
            socketRecibe.send(p);   //Se envía el NOMBRE DE USUARIO
            System.out.println("Se ha enviando el nombre " + nombreUsuario + " al servidor.");
            System.out.println(socketRecibe.getLocalPort());
        } catch (IOException ex) {
            Logger.getLogger(SocketEscucha.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public void enviaMsjPrivado(String msj){
        try {
            socketEnvia = new DatagramSocket();
            byte [] buf= msj.getBytes("UTF-8"); 
            String dir = "127.0.0.1";   //Ip destino
            InetAddress dst = InetAddress.getByName(dir);
            DatagramPacket p= new DatagramPacket(buf, buf.length, dst, 1234);
            socketEnvia.send(p);
            
        } catch (IOException ex) {
            Logger.getLogger(SocketEscucha.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    @Override
    public void run() {
        Scanner s = new Scanner(System.in);
        try {

            while (true) {  //Ciclo infinito
                
                //El "SocketRecibe" siempre estará esperando un mensaje y ya se mandaraon los DATOS de este mismo socket AL SERVIDOR 
                byte[] buffer = recibe_mensaje_multicast2(socketRecibe, longuitudMsj);   //Se recibe el mensaje PRIVADO 
                String msj = new String(buffer, StandardCharsets.UTF_8);
                interfaz.append(msj);
                System.out.println(msj);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

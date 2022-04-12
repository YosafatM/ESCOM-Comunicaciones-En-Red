package chatp3_yosafat;

import ipYObjetos.Cliente;
import ipYObjetos.IPyPuerto;
import ipYObjetos.MensajesPrivados;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;

/**
 *
 * @author L450
 */
public class Chat3 {

    static final String K_IP = "230.0.0.0";  //Dirección IP de clase D 
    static final int K_PORT = 10000;         //Puerto donde se ejecuta la app
    static final int K_SIZE = 40;           //Tamaño del arreglo de bytes 

    //ENVÍO DE DATAGRAMAS, se aprecia en el main 
    //ip e ipPuerto DESTINO
    static void envia_mensaje_multicast(byte[] buffer, String ip, int puerto) throws IOException {
        DatagramSocket socket = new DatagramSocket();   //Se crea un nuevo SOCKET 
        socket.send(new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ip), puerto));
        socket.close();
        try {   //Forzando a un cambio de contexto
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            System.out.println(ex);
        }
    }

    //Se reciben mensajes a través de un SOCKET MULTICAST 
    static byte[] recibe_mensaje_multicast(MulticastSocket socket, int longitud_mensaje) throws IOException {
        byte[] buffer = new byte[longitud_mensaje];                         //Longitud mensaje determina el tamaño del ARRAY 
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
        socket.receive(paquete);
        System.out.println();
        return paquete.getData();   //Se retorna la información del paquete 
    }

    public static byte[] recibe_mensaje_multicast2(DatagramSocket socket, int longitud_mensaje) throws IOException {
        byte[] buffer = new byte[longitud_mensaje];                         //Longitud mensaje determina el tamaño del ARRAY 
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
        socket.receive(paquete);    //Aquí el HILO se detiene HASTA QUE RECIBA ALGO 
        System.out.println();
        return paquete.getData();   //Se retorna la información del paquete 
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new Worker().start();  //Este hilo genera el MULTICAST, lo recibe

        System.out.println("Ingrese su nombre: ");
        Scanner scanner = new Scanner(System.in);
        String nombre = scanner.nextLine();
        Cliente cliente = new Cliente(nombre);    //Se le pide al servidor una nueva Ip y un nuevo puerto y se CONECTA UN SOCKET AL SERVIDOR 
        cliente.start();                        //Asignando PUERTOS e IPs aleatorios
        int x = 0;
        while (true) {
            try {   //Forzando a un cambio de contexto
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
            System.out.println("Los Usuarios conectados son:");
            cliente.d1.imprimeLista();  //Imprime la lista de usuarios conectados
            
            // ---> SIRVE PARA PONER EN ESCUCHA A UN SOCKET DE DATAGRAMA, DE ESTE CLIENTE<---
            MensajesPrivados mP;
            if (!(x > 0)) { //Sólo se debe de ejecutar UNA VEZ, para no generar un  
                mP = new MensajesPrivados(cliente.d1.getIp(), cliente.d1.getPto());
                mP.start(); //Está a la espera de recibir algo 
            }
            
            
            System.out.println("\nIngrese el mensaje a enviar:");
            String linea = scanner.nextLine();
            String mensaje = nombre + ":" + linea;

            System.out.println("Ingresa el nombre del destinatario del mensaje: ");
            String nombreDestino = scanner.nextLine();

            IPyPuerto usuarioDestino = cliente.d1.buscaNombre(cliente, nombreDestino); //Buscando el nombre
            
            
            Thread.sleep(1000);
            if (usuarioDestino != null) {
                System.out.println("-------------------");
                System.out.println("Ip:     " + usuarioDestino.getIp());
                System.out.println("Puerto: " + usuarioDestino.getPto());
                System.out.println("-------------------");
                envia_mensaje_multicast(mensaje.getBytes("UTF-8"), usuarioDestino.getIp(), usuarioDestino.getPto());

            }
            else{
            //Envío multicast
                envia_mensaje_multicast(mensaje.getBytes("UTF-8"), K_IP, K_PORT);
            }

            x++;
        }

    }
}

package primero;
/**
 * @author Alberto Peña 
 */
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Chat {
    static final String K_IP = "230.0.0.0";  //Dirección IP de clase D 
    static final int K_PORT = 10000;         //Puerto donde se ejecuta la app
    static final int K_SIZE = 40;           //Tamaño del arreglo de bytes 

    //ENVÍO DE DATAGRAMAS, se aprecia en el main 
    //ip e ipPuerto DESTINO
    static void envia_mensaje_multicast(byte[] buffer, String ip, int puerto) throws IOException {
        DatagramSocket socket = new DatagramSocket();   //Se crea un nuevo SOCKET 
        socket.send(new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ip), puerto));
        socket.close();
    }
    
    

    //Se reciben mensajes a través de un SOCKET MULTICAST 
    static byte[] recibe_mensaje_multicast(MulticastSocket socket, int longitud_mensaje) throws IOException {
        byte[] buffer = new byte[longitud_mensaje];                         //Longitud mensaje determina el tamaño del ARRAY 
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
        socket.receive(paquete);
        System.out.println();
        return paquete.getData();   //Se retorna la información del paquete 
    }
    
    static byte[] recibe_mensaje_multicast2(DatagramSocket socket, int longitud_mensaje) throws IOException {
        byte[] buffer = new byte[longitud_mensaje];                         //Longitud mensaje determina el tamaño del ARRAY 
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
        socket.receive(paquete);    //Aquí el HILO se detiene HASTA QUE RECIBA ALGO 
        System.out.println();
        return paquete.getData();   //Se retorna la información del paquete 
    }


    static String format(String base) throws IOException {
        int space = K_SIZE - base.length();             //Espacio que resta, dada la cadena base que se enviará
        StringBuilder repeated = new StringBuilder();

        while (repeated.length() != space)  
            repeated.append(" ");
        
        System.out.println("Repeatede: "+repeated);
        
        return base + repeated;
    }

    
    
    // Esta clase contiene un MULTICAST SOCKET que va a RECIBIR el mensaje y luego lo IMPRIMIRÁ
    static class Worker extends Thread {    //Esta clase creará HILOS 
        MulticastSocket socket; //Multisocket
        DatagramSocket socketRecibe;
        Worker() throws IOException {   //Constructor
            InetAddress ip_grupo = InetAddress.getByName(K_IP);     //ip del grupo MULTICAST
            socket = new MulticastSocket(K_PORT);                   //puerto MULTICAST  
            socket.joinGroup(ip_grupo);                             //Uniendo la ip al grupo MULTICAST

        }

        
        public void run() {
            try {
                while (true) {  //Ciclo infinito 
                    byte[] buffer = recibe_mensaje_multicast(socket, K_SIZE);   //Se recibe el mensaje MULTICAST
                    //Este es el mensaje transmitido al SOCKET MULTICAST
                    System.out.println(new String(buffer, "UTF-8"));    //Se IMPRIME la info del BUFFER que es una cadena y se imprime en UTF-8
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    //Clase que recibe mensajes privados nada más 
    static class MensajesPrivados extends Thread{
        DatagramSocket socketRecibe;
        public MensajesPrivados() throws UnknownHostException, SocketException {
            InetAddress ip = InetAddress.getByName("127.0.0.2");     // ip ASIGNADA ALEATORIAMENTE
                                                                     // Puerto asignado ALEATORIAMENTE
                                                                     
            socketRecibe = new DatagramSocket(1235, ip); //***HACER ESTO CON VALORES MÁS DINÁMICOS
        }
        
      
        public void run() {
            try {
                while (true) {  //Ciclo infinito 
                    byte[] buffer = recibe_mensaje_multicast2(socketRecibe, K_SIZE);   //Se recibe el mensaje MULTICAST
                    //Este es el mensaje transmitido al SOCKET MULTICAST
                    System.out.println(new String(buffer, "UTF-8"));    //Se IMPRIME la info del BUFFER que es una cadena y se imprime en UTF-8
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        
        
        
    } 

    public static void main(String[] args) throws IOException {
        
        // La utilidad del HILO consiste en que se enviará texto en la Línea 85 
        // Y lo recibirá WORKER, quien es un SOCKET MULTICAST y que se ejecuta infinitamente 
        // EL ENVÍO DE MENSAJE y el MULTICAST con WHILE infinito se ejecutan a la VEZ, por eso se puede ver 
        // Cómo se recibe el mensaje, por la CONCURRENCIA ENTRE EL HILO WORKER y MAIN 
        
        new Worker().start();   //El HILO main y el WORKER se ejecutan a la vez
                                //El HILO Worker tiene un ciclo WHILE infinito
        
        
//        new MensajesPrivados().start(); //Se ejecuta otro hilo
        
        //Worker y Mensajes Privados se quedan esperando por el mensaje 
        
        
       new MensajesPrivados().start();                        
                                
        System.out.println("Ingrese su nombre: ");
        Scanner scanner = new Scanner(System.in);
        String nombre = scanner.nextLine();
   
        
        System.out.println();

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
            System.out.println("Ingrese el mensaje a enviar:");
            String linea = scanner.nextLine();
            String mensaje = nombre + ":" + linea;  //El texto que se mostará será el "nombre del usuario" + el mensaje ESCRITO
            
            System.out.println("Desea hacer envio MULTICAST?");
            String choice=scanner.nextLine();
            if(choice.equalsIgnoreCase("si")){
                envia_mensaje_multicast(mensaje.getBytes("UTF-8"),K_IP, K_PORT);
            }
            else{

                envia_mensaje_multicast(mensaje.getBytes("UTF-8"),"127.0.0.1", 1234);
            }
            
            
            
        }
    }
}

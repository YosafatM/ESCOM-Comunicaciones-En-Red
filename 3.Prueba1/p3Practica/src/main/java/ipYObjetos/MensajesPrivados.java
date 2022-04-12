package ipYObjetos;

import static chatp3_yosafat.Chat3.recibe_mensaje_multicast2;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MensajesPrivados extends Thread{
    DatagramSocket socketRecibe;
    public static final int K_SIZE = 40;           //Tamaño MÁXIMO del arreglo de bytes 

    public DatagramSocket getSocketRecibe() {
        return socketRecibe;
    }
    
        //Este es el DATAGRAMA que recibe
        public MensajesPrivados(String ipOrigen,int ptoOrigen) throws UnknownHostException, SocketException {
            InetAddress ip = InetAddress.getByName(ipOrigen);     // ip ASIGNADA ALEATORIAMENTE
                                                                     // Puerto asignado ALEATORIAMENTE
                                                                     
            socketRecibe = new DatagramSocket(ptoOrigen, ip); //***HACER ESTO CON VALORES MÁS DINÁMICOS
            System.out.println("*****************************");
            System.out.println("Socket Escuchando: ");
            System.out.println("Ip"+socketRecibe.getLocalAddress());
            System.out.println("Puerto: "+socketRecibe.getLocalPort());
            System.out.println("*****************************");
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

package chatp3_yosafat;

import static chatp3_yosafat.Chat3.K_IP;
import static chatp3_yosafat.Chat3.K_PORT;
import static chatp3_yosafat.Chat3.K_SIZE;
import static chatp3_yosafat.Chat3.recibe_mensaje_multicast;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 *
 * @author L450
 */
public class Worker extends Thread{
    MulticastSocket socket; //Multisocket
        DatagramSocket socketRecibe;

        public Worker() throws IOException {   //Constructor
            InetAddress ip_grupo = InetAddress.getByName(K_IP);     //ip del grupo MULTICAST
            socket = new MulticastSocket(K_PORT);                   //puerto MULTICAST  
            socket.joinGroup(ip_grupo);                             //Uniendo la ip al grupo MULTICAST

        }

        public void run() {
            try {
                int x=0;
                while (true) {  //Ciclo infinito 
                    try {   //Forzando a un cambio de contexto
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
                    byte[] buffer = recibe_mensaje_multicast(socket, K_SIZE);   //Se recibe el mensaje MULTICAST, SE DETIENE la ejecución hasta que se reciba algo 
                    System.out.println(new String(buffer, "UTF-8"));    //Se IMPRIME la info del BUFFER que es una cadena y se imprime en UTF-8
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
}

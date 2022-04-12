package chatp3_yosafat;


import auxi.Chat2;
import Interfaz.GUI;
import static chatp3_yosafat.Chat3.recibe_mensaje_multicast2;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UsuarioMsjPrivado extends Thread{
    
    
    public  static UsuarioMsjPrivado usuarios[]=new UsuarioMsjPrivado[10];
    private String nombre;
    private InetAddress ip;
    private DatagramSocket socketRecibe;
    private int ptoIndividual;
    private static int ctrAux=0;

 

    public UsuarioMsjPrivado(String nombre,String ip,int ptoOrigen) throws SocketException, UnknownHostException {
        this.nombre = nombre;
        InetAddress ipOrigen= InetAddress.getByName(ip); 
        socketRecibe = new DatagramSocket(ptoOrigen, ipOrigen);
    }
    
    public static void addUsuario(UsuarioMsjPrivado usuario){
        UsuarioMsjPrivado.usuarios[ctrAux]=usuario;
        ++ctrAux;
    }
    
    //Cuerpo del HILO
    public void run() {
      
            try {
                while (true) {  //Ciclo infinito 
                                   
                    byte[] buffer = recibe_mensaje_multicast2(socketRecibe, Chat3.K_SIZE);   //Se recibe el mensaje MULTICAST
                    //Este es el mensaje transmitido al SOCKET MULTICAST
                    
                    
                    System.out.println(new String(buffer, "UTF-8"));    //Se IMPRIME la info del BUFFER que es una cadena DE UN MENSAJE Y SU EMISOR y se imprime en UTF-8
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    
    public String getNombre() {
        return nombre;
    }

    
    
    public static Integer BuscaPersona(String nombreBuscado){
        int i;
        for ( i = 0; i < usuarios.length; i++) {
            if(usuarios[i].getNombre().equalsIgnoreCase(nombreBuscado)){
                return i;
            }
        }
        return null;
    }

   
    
    
}

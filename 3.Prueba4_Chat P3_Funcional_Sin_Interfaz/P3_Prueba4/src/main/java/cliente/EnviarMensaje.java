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
    
    // Este RUN, envía un OBJETO al RUN de Busqueda.java
    @Override
    public void run() {
        try {
            InetAddress dst = InetAddress.getByName("127.0.0.1");   //Ip DESTION
            du = new DatosUsuario();
            du.setNombre(userName);     //Fijando el nombre de USUARIO, introducido por el cliente en "Cliente.java" L.33
            while (true) {
                Thread.sleep(650);
                
                //Preguntando si se desea enviar un mensaje multicast
                System.out.println("\n\nDesea enviar mensaje multicast?");
                System.out.println("si");
                System.out.println("no");
                System.out.println("Respuesta:");
                String choice = s.nextLine();
                
                //---->Se envía mensaje MULTICAST<----
                if (choice.equalsIgnoreCase("si")) {
                    System.out.println("Ingrese su mensaje multicast: ");
                    String msjMulticast = s.nextLine();     //Esperando el mensaje multicast del TECLADO
                    String msjMultiMasUsuario = userName + " dice:" + msjMulticast; //Concatenando el NOMBRE DE USUARIO
                    byte[] buffer = msjMultiMasUsuario.getBytes("UTF-8");   //Guardando el mensaje en un ARREGLO DE BYTES
                    envia_mensaje_multicast(buffer, K_IP, K_PORT);  //Enviando un mensaje a TODOS LOS DATAGRAMAS ASOCIADOS al puerto MULTICAST
                } else {
                    
                    //---> ENVIANDO MENSAJE PRIVADO <---
                    System.out.println("Ingresa el mensaje privado: ");
                    du.setMensaje(s.nextLine());    //Fijando el mensaje en el atributo "mensaje" del objeto du(Datos Usuario) se recuperará en la línea 63 de "Búsqueda.java"
                    System.out.println("Ingrese el nombre del destinatario: ");
                    du.setNombreDestino(s.nextLine());  //Fijando el nombreDestion en el atributo "nombreDestino" del objeto du(Datos Usuario) se recuperará en la línea 60 de "Búsqueda.java" 
                    
                    // ---> ENVIANDO EL OBJETO al RUN de "Busqueda.java"
                    DatagramSocket enviaDtno = new DatagramSocket();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();    //Necesitamos obtener arreglos de BYTES PARA METERLO AL DATAGRAMPACKET
                    ObjectOutputStream oos = new ObjectOutputStream(baos);  //Aquí, el flujo ObjectOutput permite ESCRIBIR EN ESE ARREGLO DE BYTES
                    oos.writeObject(du);    //Escribiendo en el baos
                    oos.flush();            //Enviando al baos
                    byte[] b2 = baos.toByteArray();  //Extraigo el ARRAY que tiene el objeto SERIALIZADO
//                System.out.println("b[] mide " + b2.length + " bytes     "); //Aquí se puede ver la METAINFORMACIÓN que se está mandando
                    
                    
                    DatagramPacket p2 = new DatagramPacket(b2, b2.length, dst, 8888); // dst --> Dirección IP destino, que es la de esta máquina
                                                                                      // 8888 --> Es el puerto del lado del servidor que contiene
                                                                                      //          la lista de clientes, ver la clase Busqueda.java L.37 para entender mejor
                    enviaDtno.send(p2); //Ya se envía
                }
            }
        } catch (SocketException ex) {
            Logger.getLogger(EnviarMensaje.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EnviarMensaje.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(EnviarMensaje.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static void envia_mensaje_multicast(byte[] buffer, String ip, int puerto) throws IOException {
        DatagramSocket socket = new DatagramSocket();   //Se crea un nuevo SOCKET 
        socket.send(new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ip), puerto));
        socket.close();
    }

}

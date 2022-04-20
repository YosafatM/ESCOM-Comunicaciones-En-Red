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
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import usuario.DatosUsuario;

public class Cliente {

//    static void envia_mensaje_multicast(byte[] buffer, String ip, int puerto) throws IOException {
//        DatagramSocket socket = new DatagramSocket();   //Se crea un nuevo SOCKET 
//        socket.send(new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ip), puerto));
//        socket.close();
//    }

    public static void main(String[] args) {

        try {
            //Variable de lectura de datos del teclado
            Scanner s = new Scanner(System.in);
            
            System.out.println("Ingresa el nombre del nuevo usuario: ");
            String userName = s.nextLine();
            SocketEscucha se = new SocketEscucha(userName);             //Recibe el nombre de usuario y tiene un HILO que ejecuta el proceso de siempre estar escuchando o recibiendo un Datagrama, que es un mensaje PRIVADO
            SockEscuchaMulticast multicast=new SockEscuchaMulticast();  //Es lo mismo que con el objeto "se" pero ahora en MULTICAST, se espera un MENSAJE MULTICAST
            se.enviaDatosAlServidor();  //Cada que se ejecute un cliente se envían los datos del SOCKET DE DATAGRAMA AL SERVIDOR(El PUERTO principalmente)
            se.start();          //Escuchando MENSAJES PRIVADOS
            multicast.start();   //Escuchando MENSAJES MULTICAST
            
            Thread.sleep(500);   //No ejecuta lo demás, SE BLOQUEA, para que se termine de ejecutar el HILO de SocketEscucha
            EnviarMensaje em= new EnviarMensaje(userName);  //Se envía el nombre de usuario para añadirlo al mensaje
            em.start(); //Se envía el mensaje
            
//            System.out.println("Desea enviar mensaje multicast?: ");
//            System.out.println("si");
//            System.out.println("no");
//            System.out.println("Respuesta:");
//            String choice= s.nextLine();
//            
//            if(choice.equalsIgnoreCase("si")){
//                System.out.println("Ingrese su mensaje: ");
//                String msjMulticast=s.nextLine();
//                String msjMultiMasUsuario=userName+" dice:"+msjMulticast;
//                byte []buffer=msjMultiMasUsuario.getBytes("UTF-8");
//                envia_mensaje_multicast(buffer, K_IP, K_PORT);
//            }
//            else{
//                System.out.println("Enviando mensaje privado...");
//                EnviarMensaje em= new EnviarMensaje(userName);
//                em.start();
//            
//            }
            
            // ----> ENVIANDO OBJETO <----
//            DatosUsuario du = new DatosUsuario();
//            du.setNombre(userName);
//            System.out.println("Ingresa el mensaje a enviar: ");
//            du.setMensaje(s.nextLine());
//            System.out.println("Ingrese el destinatario: ");
//            du.setNombreDestino(s.nextLine());
//            DatagramSocket enviaDtno = new DatagramSocket();
//
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();    //Necesitamos obtener arreglos de BYTES PARA METERLO AL DATAGRAMPACKET
//            ObjectOutputStream oos = new ObjectOutputStream(baos);  //Aquí, el flujo ObjectOutput permite ESCRIBIR EN ESE ARREGLO DE BYTES
//            oos.writeObject(du);
//            oos.flush();
//            byte[] b2 = baos.toByteArray();  //Extraigo el ARRAY que tiene el objeto SERIALIZADO
//            System.out.println("b[] mide " + b2.length + " bytes     "); //Aquí se puede ver la METAINFORMACIÓN que se está mandando 
//
//            DatagramPacket p2 = new DatagramPacket(b2, b2.length,dst,8888); // dst --> Dirección IP destino, que es la de esta máquina
//                                                                            // 8888 --> Es el puerto del lado del servidor que contiene 
//                                                                            //          la lista de clientes, ver la clase Busqueda.java para entender mejor
//
//            enviaDtno.send(p2);

           


        } catch (InterruptedException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}

package servidor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import usuario.DatosUsuario;

/**
 *
 * @author L450
 */
public class Busqueda extends Thread {

    ListaUsuarios lu;

    public Busqueda(ListaUsuarios lu) {
        this.lu = lu;   //Se recibe la lista de usuario, para buscar en ELLA, al Usuario DESTINO del MENSAJE
    }

    public void run() {
        try {
            Thread.sleep(1000);
            InetAddress dst = InetAddress.getByName("127.0.0.1");
            int ptoRecibe = 1234;
            int ptoBuscaYEnvia = 8888;
            String destinatario ;
            String ipBase = "127.0.0.";
            String msj;
            byte buf[];
            DatagramSocket s = new DatagramSocket(ptoBuscaYEnvia);   //Este DATAGRAM SOCKET Recibe lo del RUN de "EnviarMensaje.java"
            DatosUsuario usuarioEncontrado; //Buscará al usuario DESTINO
            DatosUsuario usuarioOrigen;     //Buscará el nombre del USUARIO que envía el mensaje, para que se le envíe el mensaje a él MISMO y a otro USUARIO
            s.setReuseAddress(true);
            // s.setBroadcast(true);
            System.out.println("Servidor de busqueda iniciado... esperando datagramas..");

            while (true) {
                byte[] b = new byte[65535];
                DatagramPacket p = new DatagramPacket(b, b.length);
                System.out.println("Esperando nombre destino...");

                // ---> RECIBIENDO OBJETO DE "EnviarMensaje.java" L.72<----
                s.receive(p);
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(p.getData()));
                DatosUsuario o = (DatosUsuario) ois.readObject();
                System.out.println("Objeto recibido con los datos:");
                System.out.println("Nombre usuario Origen: "+o.getNombre());
                System.out.println("Nombre Destino: " + o.getNombreDestino());
                System.out.println("Mensaje: "+o.getMensaje());
                System.out.println("Puerto origen: "+o.getPto());
                
                
                destinatario = o.getNombreDestino();    // Se obtiene el nombre DESTINO del OBJETO
                System.out.println("Destinatario: " + destinatario);
                
                //---->ESTE ES EL MENSAJE PRIVADO QUE SE MOSTRARÁ <----
                msj=o.getNombre()+" dice: "+o.getMensaje(); 
                System.out.println(msj);

                usuarioEncontrado = lu.buscaUsuario(destinatario);  //Se busca el nombre de USUARIO DESTINO, en la "lu" o LISTA DE USUARIOS del lado del SERVIDOR
                usuarioOrigen= lu.buscaUsuario(o.getNombre());  //Es obvio que se encontrará el nombre del usuario origen
                
                if (usuarioEncontrado != null) {    //Si NO ES NULL, sí existe el Usuario DESTINO
                    System.out.println("El usuario: " + usuarioEncontrado.getNombre() + " destino, se encontró");
                    System.out.println("Enviando a Pto: " + usuarioEncontrado.getPto() + " del datagrama.");
                    buf = msj.getBytes("UTF-8");   //Se convierte el mensaje a un ARRAY DE BYTES
                    DatagramPacket p2 = new DatagramPacket(buf, buf.length, dst, usuarioEncontrado.getPto());   //Se envía a un MENSAJE PRIVADO al PUERTO DEL SOCKET DE DATAGRAMA CLIENTE, que siempre está ESCUCHANDO
                    DatagramPacket p3= new DatagramPacket(buf, buf.length,dst,usuarioOrigen.getPto());          //Se envía el MENSAJE al propio cliente que ENVIA el MENSAJE PRIVADO, a otro.
                    System.out.println("----->"+usuarioOrigen.getPto()+"<------");  //Se muestra el mensaje en CONSOLA
                    s.send(p2);     //Se manda el mensaje privado a otro usuario
                    s.send(p3);     //Se manda el mensaje al propio usuario 
                } 
                
                //Si es NULO significa que no se hayó al DESTINATARIO
                else {    
                    System.out.println("Usuario no encontrado");
                }

            }

        } catch (UnknownHostException ex) {
            Logger.getLogger(Busqueda.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(Busqueda.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Busqueda.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Busqueda.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Busqueda.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}

package cliente;

import usuario.DatosUsuario;

import static cliente.SockEscuchaMulticast.K_IP;
import static cliente.SockEscuchaMulticast.K_PORT;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cliente {
    Interfaz interfaz;

    Cliente(Interfaz interfaz) {
        this.interfaz = interfaz;

        try {
            SocketEscucha se = new SocketEscucha(interfaz);          //Recibe el nombre de usuario y tiene un HILO que ejecuta el proceso de siempre estar escuchando o recibiendo un Datagrama, que es un mensaje PRIVADO
            SockEscuchaMulticast multicast = new SockEscuchaMulticast(interfaz);    //Es lo mismo que con el objeto "se" pero ahora en MULTICAST, se espera un MENSAJE MULTICAST
            se.enviaDatosAlServidor();                                      //Cada que se ejecute un cliente se envían los datos del SOCKET DE DATAGRAMA AL SERVIDOR(El PUERTO principalmente)
            se.start();                                                     //Escuchando MENSAJES PRIVADOS
            multicast.start();                                              //Escuchando MENSAJES MULTICAST

            Thread.sleep(500);                                         //No ejecuta lo demás, SE BLOQUEA, para que se termine de ejecutar el HILO de SocketEscucha
            EnviarMensaje em= new EnviarMensaje(interfaz.nombre);                  //Se envía el nombre de usuario para añadirlo al mensaje
            em.start();                                                     //Se envía el mensaje
        } catch (InterruptedException | IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void enviaMensajeMulticast(String mensaje) throws IOException {
        byte[] buffer = mensaje.getBytes(StandardCharsets.UTF_8);
        DatagramSocket socket = new DatagramSocket();
        socket.send(new DatagramPacket(buffer, buffer.length, InetAddress.getByName(K_IP), K_PORT));
        socket.close();
    }

    public void enviaMensajePrivado(String destino, String mensaje) throws IOException {
        InetAddress dst = InetAddress.getByName("127.0.0.1");   //Ip DESTINO
        DatosUsuario du = new DatosUsuario();
        du.setNombre(interfaz.nombre);
        du.setMensaje(mensaje);
        du.setNombreDestino(destino);

        // ---> ENVIANDO EL OBJETO al RUN de "Busqueda.java"
        DatagramSocket enviaDtno = new DatagramSocket();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();    //Necesitamos obtener arreglos de BYTES PARA METERLO AL DATAGRAMPACKET
        ObjectOutputStream oos = new ObjectOutputStream(baos);  //Aquí, el flujo ObjectOutput permite ESCRIBIR EN ESE ARREGLO DE BYTES
        oos.writeObject(du);
        oos.flush();
        byte[] b2 = baos.toByteArray();
        DatagramPacket p2 = new DatagramPacket(b2, b2.length, dst, 8888);
        enviaDtno.send(p2);
        enviaDtno.close();
    }
}

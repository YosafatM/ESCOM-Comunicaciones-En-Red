package ipYObjetos;

import java.awt.List;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author L450
 * 
 *  NOTA: Las clases creadas en este main NO SON las mismas a las creadas en otro MAIN 
 * Para esos se OCUPAN LOS SOCKETS
 */
public class Servidor {

    private static int ptoIndividual = 1235;    //Puerto que cambiará 
    private static int ctrIps = 2;              //Último dígito de la Dirección Ip cambiará 
    private static ArrayList<IPyPuerto> usuarios = new ArrayList<>();   //Lista de CLIENTES CONECTADOS del lado del SERVER que luego se pasará al lado del CLIENTE

    public static void main(String[] args) throws IOException {
        String nombre;
        ServerSocket s = new ServerSocket(1234);    //Servidor siempre encendido

        s.setReuseAddress(true);
        /* === s.setOption(StandardSocketOptions.SO_REUSEADDR,true) */ // Modifican las opciones de socket 
        System.out.println("Servidor iniciado, esperando conexión...");
        int x=0;
        // Esto se hará conforme entren los clientes
        for (;;) {
            Socket c1 = s.accept();   //Aceptamos la conexión, se retorna un Socket
            System.out.println(c1);
            /*getInetAddress() me retorna la dirección ip*/ /* getPort() me retorna el puerto del proceso asociado al CLIENTE */
            System.out.println("Cliente conectado desde: " + c1.getInetAddress() + ": " + c1.getPort());

            //Recibiendo el nombre del cliente
            DataInputStream dis = new DataInputStream(c1.getInputStream());
            nombre = dis.readUTF();

//            -----> Enviando un objecto <---
//          Es recomendando utilizar un flujo orientado a bytes 
            ObjectOutputStream oos = new ObjectOutputStream(c1.getOutputStream());
            
            // datos --> Es el objeto que se enviará 
            IPyPuerto datos = new IPyPuerto(nombre, "127.0.0." + Integer.toString(ctrIps), Servidor.ptoIndividual);
            usuarios.add(datos);        //Se agrega el nuevo OBJETO a la lista de Usuarios del lado del SERVER, con el nombre y datos que envió el CLIENTE,  
            addALaListaCliente(datos);  //Se agrega el nuevo OBJETO a la lista de Usuarios del lado del CLIENTE, con el nombre y datos que envió el CLIENTE,  
//            datos.imprimeLista();
            oos.writeObject(datos); //Se manda el objeto al CLIENTE
            oos.flush();   // Con este método, se quita de la cola del buffer y se envía el Objeto al Cliente

            oos.close();
            dis.close();
            c1.close();

            ++ctrIps;           //Sirve para tener Ips diferentes 
            ++ptoIndividual;    //Sirve para tener puertos diferentes 
            imprimeLista();
            
        }

    }
    // IMPRIME LA LISTA DEL LADO DEL SERVIDOR
    public static void imprimeLista() {

        for (IPyPuerto usuario : usuarios) {

            System.out.println(usuario.getIp());
            System.out.println(usuario.getNombre());
            System.out.println(usuario.getPto());
            System.out.println("");

        }

    }

    //Se agrega el nuevo OBJETO a la lista de Usuarios del lado del CLIENTE
    public static void addALaListaCliente(IPyPuerto datosCliente) {
        for (IPyPuerto usuario : usuarios) {

            datosCliente.getListaUsuarios().add(usuario);
            System.out.println("Añadido");
        }
    }
    
    public IPyPuerto buscaNombre(String nombreBuscado){
        
        ArrayList<IPyPuerto> listaUsuarios = usuarios; 
//        usuarioBuscado.d1.imprimeLista();
        for (IPyPuerto listaUsuario : usuarios) {
            
            if(listaUsuario.getNombre().equalsIgnoreCase(nombreBuscado)){
                System.out.println("!!!!!!!!!!!!!!");
                System.out.println("Nombre: "+listaUsuario.getNombre()+" encontrado");
                System.out.println("!!!!!!!!!!!!!!");
                return listaUsuario;
            }
 
        }
        
        return null;    //Si no se encuentra, se retorna NULL
    }
    
}

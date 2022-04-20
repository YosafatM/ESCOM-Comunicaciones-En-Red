package servidor;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import usuario.DatosUsuario;

public class IPyPtoAleatorio extends Thread {

    private ListaUsuarios lu;

    public IPyPtoAleatorio(ListaUsuarios lu) {
        this.lu = lu;   //La clase recibe una lista de Usuarios, esta será COMPARTIDO por el HILO de BÚSQUEDA
    }
    
    
    // ---> RUN, funciona con todo lo enviado por el cliente, ver el método "enviaDatosAlServidor" línea 36 de Cliente.Java 
    @Override   
    public void run() {
        try {
            InetAddress dst = InetAddress.getByName("127.0.0.1");   //Dirección IP destino del DATAGRAMA, la misma MÁQUINA
            int ptoRecibe = 1234; //Puerto del SOCKET DE DATAGRAMA QUE SIEMPRE ESPERARÁ CONEXIONES
            String nombreUsuario = "";
            String ipBase = "127.0.0.";
            DatagramSocket s = new DatagramSocket(ptoRecibe);   //SOCKET QUE ESPERA CONEXIONES
            
            s.setReuseAddress(true);
            System.out.println("Servidor de datos iniciado... esperando datagramas..");
            int x = 20;

            for (;;) {
                byte[] b = new byte[65535];
                DatagramPacket p = new DatagramPacket(b, b.length);
                s.receive(p);     //Se reciben EL NOMBRE del usuario y SE GUARDARÁ EN UNA un objeto de DATOS USUARIOS y este objeto irá a una LISTA DE DATOS DE USUARIO
                nombreUsuario = new String(p.getData(), 0, p.getLength());    // Se reconstruye el arreglo de BYTES ÚTIL, DESDE 0 hasta el TAMAÑO que tenga y se convierte a STRING
                System.out.println("Se ha recibido NOMBRE DE USUARIO desde "+nombreUsuario+", desde" + p.getAddress() + ":" + p.getPort());
                
               
                //Creando el objeto que se guardará en la LISTA
                DatosUsuario du = new DatosUsuario();
                
                du.setPto(p.getPort()); //Puerto del SOCKET que envió el NOMBRE DE USUARIO
                                        //Es muy necesario guardar el PUERTO de este SOCKET DE DATAGRAMA 
                                        //porque hay que recordar que este mismo SOCKET está escuchando o esperando
                                        //un mensaje siempre, entonces, más adelante, se buscará en la lista del SERVIDOR 
                                        //a este puerto, para enviarle un mensaje y que LO RECIBA 
                
//                du.setIp(ipBase + x);  
                du.setNombre(nombreUsuario); //Lo que se recibe del Cliente 

                
                lu.addALaListaCliente(du);      //Guardando en LA LISTA              
                lu.printListaClientes();        //Imprimiendo en la lista
//                System.out.println("Lista desde usuario: "+lu.toString());
                Thread.sleep(1000); 

            }//for

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    

}

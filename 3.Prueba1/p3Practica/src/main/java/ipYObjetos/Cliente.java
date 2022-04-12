package ipYObjetos;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author L450
 */

/* 
    ---> ESTA CLASE SIRVE PARA ASIGNAR Ip y Puertos distintos en cada ejecución <---    
    Así como tener información de cuántos clientes están CONECTADOS
*/
public class Cliente extends Thread{
    public String nombre;
    public IPyPuerto d1;        //d1 tiena la lista de Usuarios 
    public Cliente(String nombre) {
        this.nombre=nombre;
    }
    

    public void run() {
        // Cada que ocupo el flujo 
//        NOTA: No importa la pila del protocolo, se puede hacer con ipv4 o ipv6
//      --->ERROR: Si no funcion el programa el ERROR está en la dirección del ipv4, cambiarla
        String dirIpv4 = "localhost";      // Para saber este puerto nos vamos a CMD con permisos de ADMI y escribimos: "ipconfig", CUIDADO POR QUE ESTE PARÁMETRO VARIA
        String dirIpv6 = "2806:105e:7:643c:ac99:c71b:3e40:f20e";
        
        int pto = 1234; // Definido en el SOCKET SERVIDOR 
        try {
            Socket c1 = new Socket(dirIpv4, pto);
            System.out.println("Conexión establecida con el servidor");
            System.out.println(c1);
            
            //Flujo de salida, que indica el nombre del Cliente
            DataOutputStream dos=new DataOutputStream(c1.getOutputStream());
            dos.writeUTF(this.nombre);
            dos.flush();    //Enviar
            
//                  ----> RECIBIENDO OBJETOS<----
            ObjectInputStream ois = new ObjectInputStream(c1.getInputStream());
            try {
                d1 = (IPyPuerto) ois.readObject();    //Se lee el objeto que envía el servidor
                System.out.println("\nObjeto recibido");
                System.out.println("IP: " + d1.getIp());
                System.out.println("Nombre: " + d1.getNombre());
                System.out.println("Puerto: " + d1.getPto());
                System.out.println();
                
//                System.out.println("Los Usuarios conectados son:");
//                d1.imprimeLista();
                ois.close();
                dos.close();
                c1.close();
            } catch (ClassNotFoundException ex) {
                System.out.println(ex);
            }

        } catch (IOException ex) {
            System.out.println(ex);
        }
        
        

    }

}

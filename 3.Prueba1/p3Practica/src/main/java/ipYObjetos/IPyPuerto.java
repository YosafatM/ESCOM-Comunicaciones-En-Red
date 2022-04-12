package ipYObjetos;

import java.io.Serializable;
import java.util.ArrayList;

/* CLIENTES CONECTADOS == Nombre, ip y pto*/
public class IPyPuerto implements Serializable{
   
    private String nombre;      //Nombre del cliente
    private String ip;          //Ip Único del cliente generado por el Servidor
    private int pto;            //Puerto Único del cliente generado por el Servidor
    private  ArrayList<IPyPuerto> listaUsuarios = new ArrayList<>();

    

    public IPyPuerto(String nombre, String ip, int pto) {
        this.nombre = nombre;
        this.ip = ip;
        this.pto = pto;
    }
    
    //Lista que recibe Información de los USUARIOS CONECTADOS
    public ArrayList<IPyPuerto> getListaUsuarios() {
        return listaUsuarios;
    }
    
    
    public String getNombre() {
        return nombre;
    }

    public String getIp() {
        return ip;
    }

    public int getPto() {
        return pto;
    }
    
    //Este método se ocupa del lado del SERVER, en donde se añaden LOS CLIENTES CONECTADOS
    public void addList(IPyPuerto elemento){
        listaUsuarios.add(elemento);
    }
    
    //Se imprime la lista dada por el server
    public void imprimeLista() {

        for (IPyPuerto listaUsuario : listaUsuarios) {
            System.out.println("Ip: "+listaUsuario.getIp());
            System.out.println("Nombre: "+listaUsuario.getNombre());
            System.out.println("Puerto: "+listaUsuario.getPto());
            System.out.println("");
        }

    }
    
    //Busqueda para enviar mensaje al CHAT
    public IPyPuerto buscaNombre(Cliente usuarioBuscado,String nombreBuscado){
        
        ArrayList<IPyPuerto> listaUsuarios = usuarioBuscado.d1.getListaUsuarios(); 
        usuarioBuscado.d1.imprimeLista();
        for (IPyPuerto listaUsuario : listaUsuarios) {
            
            if(listaUsuario.nombre.equalsIgnoreCase(nombreBuscado)){ 
                return listaUsuario;
            }
 
        }
        
        return null;    //Si no se encuentra, se retorna NULL
    }
    
    
    
}

package servidor;

import java.util.ArrayList;
import usuario.DatosUsuario;

//Lista de Datos Usuarios que sirve para buscar el USUARIO DESTINO de un MENSAJE PRIVADO
public class ListaUsuarios {
    private  ArrayList<DatosUsuario> usuarios = new ArrayList<>();

    public ArrayList<DatosUsuario> getUsuarios() {
        return usuarios;
    }
    public  void addALaListaCliente(DatosUsuario du) {
        usuarios.add(du);
    }

    public void printListaClientes() {

        for (DatosUsuario usuario : usuarios) {
            System.out.println("\n\n----------------------------");
            System.out.println("Datos del Usuario");
            System.out.println("Nombre: " + usuario.getNombre());
            System.out.println("Pto: " + usuario.getPto());
            System.out.println("Ip: " + usuario.getIp());
            System.out.println("-------------------------------");
        }

    }
    
    //Busca un usuario en la lista
    public DatosUsuario buscaUsuario(String nombreBuscado) {
        for (DatosUsuario usuario : usuarios) {

            if (usuario.getNombre().equalsIgnoreCase(nombreBuscado)) {
                return usuario;
            }

        }
        return null;
    }

    @Override
    public String toString() {
        return "ListaUsuarios{" + "usuarios=" + usuarios + '}';
    }
}

package servidor;

public class Servidor {
    public static void main(String[] args) {
        try {
            //Guardando datos de los clientes que se conectan en una LISTA DE USUARIO
            ListaUsuarios  lu= new ListaUsuarios();     //Esta variable será compartida por 2 HILOS, IPyPtoAleatorio y Busqueda
            IPyPtoAleatorio ip = new IPyPtoAleatorio(lu);   //Pasando la variable "lu" al HILO
            ip.start();  //Este hilo sirve para gurdar los datos del SOCKET de DATAGRAMA en una lista del LADO DEL SERVIDOR

            Busqueda bu= new Busqueda(lu);                  //Pasando la variable "lu" al HILO, compartida con el objeto "ip"
            bu.start();
            ip.join();
            bu.join();
            
            
            
        } catch (InterruptedException ex) {
            System.out.println(ex);
        }
    }
}

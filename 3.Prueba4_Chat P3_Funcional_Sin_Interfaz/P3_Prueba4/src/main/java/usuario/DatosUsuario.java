package usuario;

import java.io.Serializable;

//Clase que sirve para guardar los Datos del Usuario introducidos 
public class DatosUsuario implements Serializable{
    private String ip;
    private int pto;
    private String nombre;
    private String mensaje;
    private String nombreDestino;

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public void setNombreDestino(String nombreDestino) {
        this.nombreDestino = nombreDestino;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getNombreDestino() {
        return nombreDestino;
    }


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPto() {
        return pto;
    }

    public void setPto(int pto) {
        this.pto = pto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}

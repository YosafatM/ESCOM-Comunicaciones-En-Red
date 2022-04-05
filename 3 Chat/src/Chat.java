import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Chat {
    static final String K_IP = "230.0.0.0";
    static final int K_PORT = 10000;
    static final int K_SIZE = 40;

    static void envia_mensaje_multicast(byte[] buffer) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        socket.send(new DatagramPacket(buffer, buffer.length, InetAddress.getByName(K_IP), K_PORT));
        socket.close();
    }

    static byte[] recibe_mensaje_multicast(MulticastSocket socket, int longitud_mensaje) throws IOException {
        byte[] buffer = new byte[longitud_mensaje];
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
        socket.receive(paquete);
        return paquete.getData();
    }

    static String format(String base) throws IOException {
        int space = K_SIZE - base.length();
        StringBuilder repeated = new StringBuilder();

        while (repeated.length() != space)
            repeated.append(" ");

        return base + repeated;
    }

    static class Worker extends Thread {
        MulticastSocket socket;
        private Interfaz interfaz;

        Worker(Interfaz interfaz) throws IOException {
            InetAddress ip_grupo = InetAddress.getByName(K_IP);
            this.interfaz = interfaz;
            socket = new MulticastSocket(K_PORT);
            socket.joinGroup(ip_grupo);
        }

        public void run() {
            try {
                while (true) {
                    byte[] buffer = recibe_mensaje_multicast(socket, K_SIZE);
                    interfaz.append(new String(buffer, StandardCharsets.UTF_8));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

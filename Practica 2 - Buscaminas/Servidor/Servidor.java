package Servidor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Servidor {
    private static final String RUTA = "./records.txt", GANO = "¡¡ G A N A S T E !! ^^";
    private static final int PUERTO = 8000;
    private static String resultado = "";
    private static long recordFacil, recordIntermedio, recordExperto;

    private static boolean hacerMovimiento(DataInputStream entrada, DataOutputStream salida) throws IOException {
        boolean sonCoordenadasInvalidas = true, yaPerdio = false;
        String fila = "", columna = "", marca = "";

        // Recibir coordenadas
        while (sonCoordenadasInvalidas) {
            fila = entrada.readUTF();
            columna = entrada.readUTF().toLowerCase();
            marca = entrada.readUTF().toLowerCase();

            sonCoordenadasInvalidas = !Tablero.columnasValidas.contains(columna) || !Tablero.filasValidas.contains(fila);
            salida.writeBoolean(sonCoordenadasInvalidas);
            if (sonCoordenadasInvalidas) salida.writeUTF("Ingrese unas coordenadas válidas");
        }

        int x = Tablero.columnasValidas.indexOf(columna), y = Tablero.filasValidas.indexOf(fila);
        if (marca.equals("s")) {
            if (Tablero.getTablero()[y][x] == Tablero.PLACEHOLDER) Tablero.getTablero()[y][x] = Tablero.BANDERA;
            return true;
        }

        if (Tablero.getMetatablero()[y][x] == Tablero.MINA) {
            resultado = "¡P E R D I S T E! :c";
            Tablero.getTablero()[y][x] = 'X';
            yaPerdio = true;
        }

        Tablero.actualizarTablero(y, x);
        Tablero.mandarTablero(salida);
        return !yaPerdio && debeSeguirMoviendo();
    }

    private static boolean debeSeguirMoviendo() {
        for (int i = 0; i < Tablero.getTablero().length; i++) {
            for (int j = 0; j < Tablero.getTablero()[0].length; j++) {
                if (Tablero.getMetatablero()[i][j] == Tablero.MINA) continue;
                if (Tablero.getMetatablero()[i][j] != Tablero.getTablero()[i][j]) return true;
            }
        }

        resultado = GANO;
        return false;
    }

    private static void imprimirRecords(DataOutputStream salida) throws IOException {
        File archivo = new File(RUTA);

        if (!archivo.exists()) {
            archivo.createNewFile();
            recordFacil = recordIntermedio = recordExperto = 0;
        } else {
            Scanner lector = new Scanner(archivo);
            recordFacil = Long.parseLong(lector.nextLine());
            recordIntermedio = Long.parseLong(lector.nextLine());
            recordExperto = Long.parseLong(lector.nextLine());
            lector.close();
        }

        salida.writeUTF("**************  Records  **************");
        salida.writeUTF("FÁCIL: " + recordFacil + " segundos.");
        salida.writeUTF("INTERMEDIO: " + recordIntermedio + " segundos.");
        salida.writeUTF("EXPERTO: " + recordExperto + " segundos.\n\n");
    }

    private static void guardarRecords(int dificultad, long tiempo) throws IOException {
        FileWriter archivo = new FileWriter(RUTA);

        switch (dificultad) {
            case 0 -> { if (tiempo < recordFacil || recordFacil == 0) recordFacil = tiempo; }
            case 1 -> { if (tiempo < recordIntermedio || recordIntermedio == 0) recordIntermedio = tiempo; }
            case 2 -> { if (tiempo < recordExperto || recordExperto == 0) recordExperto = tiempo; }
        }

        archivo.write(recordFacil + "\n");
        archivo.write(recordIntermedio + "\n");
        archivo.write(recordExperto + "\n");
        archivo.close();
    }

    public static void main(String[] args) {
        try {
            ServerSocket servicio = new ServerSocket(PUERTO);
            System.out.println("Servidor esperando la conexión del Cliente");
            Socket conexion = servicio.accept();
            DataInputStream entrada = new DataInputStream(conexion.getInputStream());
            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());

            imprimirRecords(salida);

            int dificultad = entrada.readInt();
            boolean sigaJugando = true;

            Tablero.inicializarTablero(dificultad, salida);
            LocalDateTime comienzo = LocalDateTime.now();
            Tablero.imprimirTablero(Tablero.getMetatablero());
            System.out.println();

            Tablero.imprimirTablero(Tablero.getTablero());

            while (sigaJugando) {
                sigaJugando = hacerMovimiento(entrada, salida);
                Tablero.imprimirTablero(Tablero.getTablero());
                salida.writeBoolean(sigaJugando);
            }

            salida.writeUTF(resultado);

            Duration tiempoDeJuego = Duration.between(comienzo, LocalDateTime.now());
            salida.writeUTF("\nTiempo de juego: " + tiempoDeJuego.getSeconds() + " segundos.");
            if (resultado.equals(GANO)) guardarRecords(dificultad, tiempoDeJuego.getSeconds());

            entrada.close(); salida.close(); conexion.close(); servicio.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

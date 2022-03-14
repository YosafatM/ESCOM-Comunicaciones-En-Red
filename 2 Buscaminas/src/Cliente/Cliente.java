package Cliente;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class Cliente {
    private static final int PUERTO = 8000;
    private static final String HOST = "localhost";
    private static final int PRINCIPIANTE = 0, INTERMEDIO = 1, EXPERTO = 2;
    private static final char MINA = 'X', PLACEHOLDER = '-', BANDERA = 'B';

    private static char[][] tablero;

    private static void inicializarTablero(DataInputStream entrada) throws IOException {
        tablero = new char[entrada.readInt()][entrada.readInt()];
        for (char[] fila : tablero) Arrays.fill(fila, PLACEHOLDER);
    }

    private static void imprimirTablero(char[][] imprimible) {
        int j = 1;
        System.out.println();

        // Primera fila del tablero
        System.out.print("  "); // Por el tamaño del número
        for (char letra = 'a'; letra < 'a' + tablero[0].length; letra++) System.out.print(" " + letra);
        System.out.println();

        // Resto del tablero
        for (char[] fila : imprimible) {
            System.out.printf("%2s", j);
            for (char valor : fila) System.out.print(valor == '0' ? "  " : " " + valor);
            System.out.println();
            j++;
        }
    }

    private static int seleccionarDificultad(Scanner lector) {
        boolean debePreguntar = true;
        int dificultad = -1;

        while (debePreguntar) {
            debePreguntar = false;
            System.out.println("Bienvenido a Buscaminas");
            System.out.println("Por favor, seleccione una dificultad:");
            System.out.println("A) Principiante (9x9, 10 minas)");
            System.out.println("B) Intermedio (16x16, 40 minas)");
            System.out.println("C) Experto (30x16, 99 minas)");

            switch (lector.nextLine().toUpperCase()) {
                case "A" -> dificultad = PRINCIPIANTE;
                case "B" -> dificultad = INTERMEDIO;
                case "C" -> dificultad = EXPERTO;
                default -> debePreguntar = true;
            }
        }

        return dificultad;
    }

    private static void mandarCoordenadas(DataInputStream entrada, DataOutputStream salida, Scanner lector) throws IOException {
        boolean debeSeguirPreguntando = true;

        while (debeSeguirPreguntando) {
            System.out.println("\nIngrese la fila:");
            salida.writeUTF(lector.nextLine());
            System.out.println("Ingrese la columna:");
            salida.writeUTF(lector.nextLine());
            System.out.println("Ponemos bandera? (s):");
            salida.writeUTF(lector.nextLine());

            debeSeguirPreguntando = entrada.readBoolean();
            if (debeSeguirPreguntando) System.out.println(entrada.readUTF());
        }
    }

    private static void actualizarTablero(DataInputStream entrada) throws IOException {
        byte[] mensaje = new byte[2 * tablero.length * tablero[0].length];
        entrada.readNBytes(mensaje, 0, mensaje.length);
        ByteArrayInputStream baos = new ByteArrayInputStream(mensaje);
        DataInputStream lector = new DataInputStream(baos);

        for (char[] fila : tablero)
            for (int i = 0; i < fila.length; i++)
                fila[i] = lector.readChar();
    }

    public static void main(String[] args) {
        try {
            Socket conexion = new Socket(HOST, PUERTO);
            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
            DataInputStream entrada = new DataInputStream(conexion.getInputStream());
            Scanner lector = new Scanner(System.in);
            boolean sigueJugando = true;

            // Imprimir los records
            System.out.println(entrada.readUTF());
            System.out.println(entrada.readUTF());
            System.out.println(entrada.readUTF());
            System.out.println(entrada.readUTF());

            salida.writeInt(seleccionarDificultad(lector));
            inicializarTablero(entrada);
            imprimirTablero(tablero);

            while (sigueJugando) {
                mandarCoordenadas(entrada, salida, lector);
                actualizarTablero(entrada);
                imprimirTablero(tablero);
                sigueJugando = entrada.readBoolean();
            }

            System.out.println(entrada.readUTF());
            System.out.println(entrada.readUTF());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package Servidor;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tablero {
    private static char[][] tablero, metatablero;
    private static final int[] ANCHO = {9, 16, 30}, ALTO = {9, 16, 16}, MINAS = {1, 40, 99};
    static final char MINA = 'X';
    static final char PLACEHOLDER = '-';
    static final char BANDERA = 'B';

    static final List<String> columnasValidas = new ArrayList<>(), filasValidas = new ArrayList<>();

    static char[][] getTablero() { return tablero; }
    static char[][] getMetatablero() { return metatablero; }

    static void inicializarTablero(int dificultad, DataOutputStream salida) throws IOException {
        tablero = new char[ALTO[dificultad]][ANCHO[dificultad]];
        metatablero = new char[ALTO[dificultad]][ANCHO[dificultad]];
        salida.writeInt(ALTO[dificultad]);
        salida.writeInt(ANCHO[dificultad]);

        for (char[] fila : tablero) Arrays.fill(fila, PLACEHOLDER);
        for (char[] fila : metatablero) Arrays.fill(fila, '0');
        for (char letra = 'a'; letra < 'a' + ANCHO[dificultad]; letra++) columnasValidas.add(String.valueOf(letra));
        for (int i = 1; i <= ALTO[dificultad]; i++) filasValidas.add(String.valueOf(i));

        int minas = 0;
        while (minas < MINAS[dificultad]) {
            int fila = (int)(Math.random() * ALTO[dificultad]);
            int columna = (int)(Math.random() * ANCHO[dificultad]);

            if (metatablero[fila][columna] == MINA) continue;

            metatablero[fila][columna] = MINA;
            minas++;

            // Le sumamos a las celdas adyacentes, preguntamos a qué direcciones se puede hacer y si no son minas
            boolean izq = columna != 0;
            boolean der = columna != ANCHO[dificultad]-1;
            boolean arr = fila != 0;
            boolean aba = fila != ALTO[dificultad]-1;

            if (izq && metatablero[fila][columna-1] != MINA) ++metatablero[fila][columna-1];
            if (der &&  metatablero[fila][columna+1] != MINA) ++metatablero[fila][columna+1];
            if (aba && metatablero[fila+1][columna] != MINA) ++metatablero[fila+1][columna];
            if (arr && metatablero[fila-1][columna] != MINA) ++metatablero[fila-1][columna];

            if (arr && der && metatablero[fila-1][columna+1] != MINA) ++metatablero[fila-1][columna+1];
            if (arr && izq && metatablero[fila-1][columna-1] != MINA) ++metatablero[fila-1][columna-1];
            if (aba && izq && metatablero[fila+1][columna-1] != MINA) ++metatablero[fila+1][columna-1];
            if (aba && der && metatablero[fila+1][columna+1] != MINA) ++metatablero[fila+1][columna+1];
        }
    }

    static void imprimirTablero(char[][] imprimible) {
        int j = 1;
        System.out.println();

        // Primera fila del tablero
        System.out.print("  "); // Por el tamaño del número
        for (String letra : columnasValidas) System.out.print(" " + letra);
        System.out.println();

        // Resto del tablero
        for (char[] fila : imprimible) {
            System.out.printf("%2s", j);
            for (char valor : fila) System.out.print(valor == '0' ? "  " : " " + valor);
            System.out.println();
            j++;
        }
    }

    static void mandarTablero(DataOutputStream salida) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream escritor = new DataOutputStream(baos);

        for (char[] fila : tablero)
            for (char valor : fila)
                escritor.writeChar(valor);

        escritor.flush();
        salida.write(baos.toByteArray());
    }

    static void actualizarTablero(int fila, int columna) {
        if (tablero[fila][columna] != PLACEHOLDER) return;
        tablero[fila][columna] = metatablero[fila][columna];

        if (tablero[fila][columna] == '0') {
            // Hay que expandir los 0's
            boolean izq = columna != 0;
            boolean der = columna != tablero[0].length-1;
            boolean arr = fila != 0;
            boolean aba = fila != tablero.length-1;

            if (arr && tablero[fila-1][columna] == PLACEHOLDER) actualizarTablero(fila-1, columna);
            if (aba && tablero[fila+1][columna] == PLACEHOLDER) actualizarTablero(fila+1, columna);
            if (der && tablero[fila][columna+1] == PLACEHOLDER) actualizarTablero(fila, columna+1);
            if (izq && tablero[fila][columna-1] == PLACEHOLDER) actualizarTablero(fila, columna-1);
        }
    }
}

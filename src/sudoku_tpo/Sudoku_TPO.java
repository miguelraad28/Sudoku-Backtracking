import java.io.File;
import java.util.Scanner;

public class Sudoku_TPO {

    // Contador de llamadas recursivas
    private static int llamadasRecursivas = 0;

    // Para la visualización en tiempo real
    private static int lineasTablero = 0;
    private static boolean enProceso = true;
    // true: Visual de resolución por backtracking en consola,
    // pero hay un delay de 210ms entre actualizaciones para que sea perceptible
    // y hay un delay de 160ms cuando se hace como tal el backtracking para ver
    // como se deshace la rama que no lleva a solución.as
    // false: No se muestra el progreso en consola.
    private static boolean mostrarProgresoEnConsola = false;
    // Nombre del archivo
    private static String nombreArchivo = "sudoku.txt";
    // Aplicar con heurística MRV
    private static boolean aplicarHeuristicaMRV = true;

    public static int[][] cargarDesdeArchivo() {
        try (Scanner sc = new Scanner(new File("src/" + nombreArchivo))) {
            // Contar filas
            int filas = 0;
            while (sc.hasNextLine()) {
                if (sc.nextLine().trim().length() > 0) {
                    filas++;
                } else {
                    throw new RuntimeException("Archivo inválido. El archivo tiene fila(s) vacía(s).");
                }
            }
            sc.close();

            // Validar que sea un cuadrado perfecto
            int raiz = (int) Math.sqrt(filas);
            if (raiz * raiz != filas) {
                System.out.println("Error: El tamaño " + filas + "x" + filas + " no es válido para Sudoku.");
                System.out.println("Los tamaños válidos son: 4x4, 9x9, 16x16, 25x25, 36x36, etc.");
                return null;
            }

            System.out.println("Filas: " + filas + " (bloques de " + raiz + "x" + raiz + ")");

            // Releer y cargar
            Scanner sc2 = new Scanner(new File("src/" + nombreArchivo));

            int[][] tablero = new int[filas][filas];

            int celdasLlenas = 0;
            for (int i = 0; i < filas; i++) {
                for (int j = 0; j < filas; j++) {
                    tablero[i][j] = sc2.nextInt();
                    if (tablero[i][j] != 0)
                        celdasLlenas++;
                }
            }

            sc2.close();

            System.out.println("Cantidad de celdas LLENAS: " + celdasLlenas);

            return tablero;
        } catch (Exception e) {
            System.out.println("Error al cargar el archivo: " + e.getMessage());
            return null;
        }
    }

    public static boolean resolverSudoku(int[][] tablero) {
        llamadasRecursivas++; // Incrementar contador

        if (mostrarProgresoEnConsola) {
            try {
                Thread.sleep(210);
            } catch (InterruptedException e) {
            }
            actualizarTableroVisual(tablero);
        }

        int N = tablero.length;
        int fila = -1, col = -1;
        // Búsqueda de la siguiente celda vacía (sin heurística)
        if (!aplicarHeuristicaMRV) {
            boolean vacioEncontrado = false;
            for (int i = 0; i < N && !vacioEncontrado; i++) { // O(n^2)
                for (int j = 0; j < N && !vacioEncontrado; j++) {
                    if (tablero[i][j] == 0) {
                        fila = i;
                        col = j;
                        vacioEncontrado = true;
                    }
                }
            }

            if (!vacioEncontrado) {
                if (mostrarProgresoEnConsola) {
                    actualizarTableroVisual(tablero);
                }
                return true; // No hay vacíos - solucionado
            }
        } else {
            // Búsqueda de la siguiente celda vacía (heurística)
            int minOpciones = Integer.MAX_VALUE;

            // Buscar celda vacía más restringida (heurística)
            for (int i = 0; i < N; i++) { // O(n^2)
                for (int j = 0; j < N; j++) { // O(n)
                    if (tablero[i][j] == 0) {
                        int opciones = contarOpcionesValidas(tablero, i, j); // O(n^2)
                        // Si tiene menos opciones que la mejor encontrada, la seleccionamos
                        if (opciones < minOpciones) {
                            minOpciones = opciones;
                            fila = i;
                            col = j;
                            // Si solo tiene 1 opción, es la mejor (más restringida)
                            if (opciones == 1) {
                                break;
                            }
                        }
                    }
                }
                // Optimización: si encontramos una celda con 1 opción, podemos salir
                if (minOpciones == 1) {
                    break;
                }
            }
        }
        // FIN BUSQUEDA HEURÍSTICA

        if (fila == -1 || col == -1) {
            if (mostrarProgresoEnConsola) {
                // Tablero completo, actualizar visualización final
                actualizarTableroVisual(tablero);
            }
            return true; // No hay vacíos - solucionado
        }

        // Recorrer opciones de números 1-N
        for (int num = 1; num <= N; num++) {

            // esValido es mi poda básica
            if (esValido(tablero, fila, col, num)) {
                tablero[fila][col] = num;

                if (resolverSudoku(tablero))
                    return true;

                if (mostrarProgresoEnConsola) {
                    try {
                        Thread.sleep(160);
                    } catch (InterruptedException e) {
                    }
                    actualizarTableroVisual(tablero);
                }

                tablero[fila][col] = 0; // reinicio celda
            }
        }

        return false;
    }

    public static boolean esValido(int[][] tablero, int fila, int col, int num) {
        int N = tablero.length;
        int tamanioBloque = (int) Math.sqrt(N);

        // Subcuadro
        int inicioFilaSubcuadro = fila - fila % tamanioBloque;
        int inicioColSubcuadro = col - col % tamanioBloque;

        for (int i = inicioFilaSubcuadro; i < inicioFilaSubcuadro + tamanioBloque; i++) // O(sqrt(n))
            for (int j = inicioColSubcuadro; j < inicioColSubcuadro + tamanioBloque; j++) // O(sqrt(n)) =>
                                                                                          // sqrt(n)*sqrt(n) = sqrt(n^2)
                                                                                          // = O(n)
                if (tablero[i][j] == num)
                    // Dentro del subbloque ya está colocado ese número
                    return false;

        // Fila y columna de la posición actual
        for (int i = 0; i < N; i++) // O(n)
            if (tablero[fila][i] == num || tablero[i][col] == num)
                return false;

        return true;
    }

    // Cuenta las opciones válidas que tiene una celda
    public static int contarOpcionesValidas(int[][] tablero, int fila, int col) { // O(n^2)
        int N = tablero.length;
        int contador = 0;

        // Probar cada número de 1 a N y contar cuántos son válidos
        for (int num = 1; num <= N; num++) { // O(n)
            if (esValido(tablero, fila, col, num)) { // O(n)
                contador++;
            }
        }

        return contador;
    }

    public static void imprimirTablero(int[][] tablero) {
        int N = tablero.length;
        int tamanioBloque = (int) Math.sqrt(N);
        boolean usarFormatoAncho = N > 9; // Usar formato de 2 dígitos cuando N > 9

        for (int i = 0; i < N; i++) {
            if (i % tamanioBloque == 0 && i != 0) {
                // Calcular ancho del separador según el formato
                int anchoSeparador = usarFormatoAncho ? N * 3 : N * 2;
                System.out.println("-".repeat(anchoSeparador));
            }
            for (int j = 0; j < N; j++) {
                if (j % tamanioBloque == 0 && j != 0)
                    System.out.print("| ");

                // Formato según tamaño del Sudoku
                if (usarFormatoAncho) {
                    // Formato de 2 dígitos para Sudokus grandes
                    if (tablero[i][j] == 0) {
                        System.out.print(".. ");
                    } else {
                        System.out.print(String.format("%2d ", tablero[i][j]));
                    }
                } else {
                    // Formato normal para Sudokus pequeños (9x9 o menos)
                    System.out.print(tablero[i][j] == 0 ? ". " : tablero[i][j] + " ");
                }
            }
            System.out.println();
        }
    }

    // Imprime el tablero actualizado en tiempo real
    private static void actualizarTableroVisual(int[][] tablero) {
        if (!enProceso)
            return;

        int N = tablero.length;
        int tamanioBloque = (int) Math.sqrt(N);
        int celdasLlenas = contarCeldasLlenas(tablero);
        int totalCeldas = N * N;

        // Calcular cuántas líneas ocupa el tablero
        int lineasTableroActual = N + (tamanioBloque - 1); // filas + separadores

        // Mover cursor hacia arriba para sobrescribir
        for (int i = 0; i < lineasTablero + 1; i++) { // +1 por la línea de progreso
            System.out.print("\033[A"); // Código ANSI para mover cursor arriba
        }

        // Encabezado con progreso (sobrescribir línea anterior)
        System.out.print("\033[K"); // Limpiar línea actual
        System.out.print(String.format("Progreso: %d/%d celdas | Llamadas: %d",
                celdasLlenas, totalCeldas, llamadasRecursivas));
        System.out.println();

        // Formato según tamaño del Sudoku
        boolean usarFormatoAncho = N > 9;

        // Imprimir tablero sobrescribiendo el anterior
        for (int i = 0; i < N; i++) {
            if (i % tamanioBloque == 0 && i != 0) {
                System.out.print("\033[K"); // Limpiar linea antes de imprimir separador
                int anchoSeparador = usarFormatoAncho ? N * 3 : N * 2;
                System.out.println("-".repeat(anchoSeparador));
            }
            System.out.print("\033[K"); // Limpia la linea antes de re-imprimir
            for (int j = 0; j < N; j++) {
                if (j % tamanioBloque == 0 && j != 0)
                    System.out.print("| ");

                // Formato segun tamaño del Sudoku
                if (usarFormatoAncho) {
                    // Formato de 2 dígitos para Sudokus grandes
                    if (tablero[i][j] == 0) {
                        System.out.print(".. ");
                    } else {
                        System.out.print(String.format("%2d ", tablero[i][j]));
                    }
                } else {
                    // Espacios vacíos "." para sudokus N <= 9
                    System.out.print(tablero[i][j] == 0 ? ". " : tablero[i][j] + " ");
                }
            }
            System.out.println();
        }

        // Actualizar contador de líneas
        lineasTablero = lineasTableroActual;

        System.out.flush(); // Forzar salida inmediata
    }

    private static int contarCeldasLlenas(int[][] tablero) {
        int contador = 0;
        for (int[] fila : tablero) {
            for (int valor : fila) {
                if (valor != 0)
                    contador++;
            }
        }
        return contador;
    }

    public static void main(String[] args) {
        System.out.println("\n_________ RESOLUCIÓN DE SUDOKU _________");

        // La función de cargar el archivo forma parte de mi función auxiliar
        // para verificar parcialmente la validez del tablero
        int[][] tablero = cargarDesdeArchivo();

        if (tablero == null) {
            System.out.println("No se encontró el archivo " + nombreArchivo);
        } else {

            System.out.println("\nTablero inicial:");
            imprimirTablero(tablero);

            if (mostrarProgresoEnConsola) {

                System.out.println("\nResolviendo...");
                System.out.println();

                // Imprimir encabezado inicial para la visualización
                int totalCeldas = tablero.length * tablero.length;
                int celdasIniciales = contarCeldasLlenas(tablero);
                System.out.println(String.format("Progreso: %d/%d celdas | Llamadas: 0",
                        celdasIniciales, totalCeldas));
            }

            // Calcular líneas del tablero inicial para la visualización
            int tamanioBloque = (int) Math.sqrt(tablero.length);
            lineasTablero = tablero.length + (tamanioBloque - 1); // filas + separadores horizontales

            long inicio = System.nanoTime();

            boolean exito = resolverSudoku(tablero);

            long fin = System.nanoTime();

            // Desactivar actualización para mostrar resultado final
            enProceso = false;

            if (exito) {
                double tiempoMs = (fin - inicio) / 1_000_000.0;
                System.out.println("\nSudoku resuelto correctamente:");
                imprimirTablero(tablero);
                System.out.println("\nEstadísticas:");
                System.out.println("Llamadas recursivas: " + llamadasRecursivas);
                System.out.println("Tiempo: " + tiempoMs + " ms");
            } else {
                System.out.println("\nNo se encontró solución válida.");
            }
        }
    }
}

# Resolvedor de Sudoku con Backtracking

## Descripción

Este programa resuelve sudokus de cualquier tamaño válido (4x4, 9x9, 16x16, 25x25, etc.) usando backtracking. El tablero se carga desde un archivo de texto que debe estar en la carpeta src/. El programa cuenta las llamadas recursivas y mide el tiempo de ejecución.

## Variables de clase importantes

### nombreArchivo (línea 19)
Cambia este valor para cargar un archivo diferente. Por ejemplo:
- nombreArchivo = "sudoku.txt"
- nombreArchivo = "ej_16x16.txt"
- nombreArchivo = "ej_25x25.txt"

El archivo debe estar en la carpeta src/ y debe tener el formato correcto (números separados por espacios, con 0 para celdas vacías).

### mostrarProgresoEnConsola (línea 17)
- true: Muestra el progreso del backtracking en tiempo real en la consola (con delays para que se vea el proceso)
- false: No muestra el progreso, solo el resultado final

### aplicarHeuristicaMRV (línea XX)
- `true`: Usa la heurística MRV (*Minimum Remaining Values*), que selecciona primero la celda vacía más restringida (con menos opciones válidas). Esto puede reducir el número de llamadas recursivas.
- `false`: Hace la búsqueda de manera secuencial (de izquierda a derecha, arriba a abajo).

Puedes cambiar el comportamiento del resolvedor modificando el valor de esta variable en el código, por ejemplo:

private static boolean aplicarHeuristicaMRV = true; // Usa MRV (por defecto)
private static boolean aplicarHeuristicaMRV = false; // Desactiva MRV

Deja `aplicarHeuristicaMRV` en `true` para usar la heurística MRV, o ponlo en `false` para hacer la búsqueda tradicional secuencial.


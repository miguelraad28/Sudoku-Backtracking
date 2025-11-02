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

## Usar o no la heurística MRV (Minimum Remaining Values)
### Primero ramificar la celda más restringida

Actualmente el código está configurado para usar la heurística MRV, que selecciona la celda vacía con menos opciones válidas primero. Esto reduce el número de llamadas recursivas.

Para usar SIN heurística (búsqueda secuencial):
1. Comenta las líneas 101-127
2. Descomenta las líneas 80-99

Para usar CON heurística MRV (configuración actual):
1. Deja descomentadas las líneas 101-127
2. Deja comentadas las líneas 80-99


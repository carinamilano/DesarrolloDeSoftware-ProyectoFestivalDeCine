package modelo.comun;

// Entrega números correlativos para los códigos de funciones, entradas y abonos
public class Numerador {
    private int proximo = 1;

    public int siguiente() {
        return proximo++;
    }

    // Al cargar datos guardados, los códigos nuevos siguen después del último que se usó
    public void continuarDespuesDe(int numeroUsado) {
        proximo = Math.max(proximo, numeroUsado + 1);
    }
}

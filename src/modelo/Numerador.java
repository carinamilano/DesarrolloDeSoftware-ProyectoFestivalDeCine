package modelo;

// Entrega números correlativos para los códigos de entradas y abonos
public class Numerador {
    private int proximo = 1;

    public int siguiente() {
        return proximo++;
    }
}

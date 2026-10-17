package modelo.funciones;

import excepciones.DatoInvalidoException;

// Objeto de valor: una butaca de la sala. Antes era un int y el 0 significaba "sin butaca";
// ahora "sin butaca" es Optional.empty() y no hay números mágicos
public record Butaca(int numero) implements Comparable<Butaca> {

    public Butaca {
        if (numero < 1) {
            throw new DatoInvalidoException("El número de butaca debe ser mayor a 0.");
        }
    }

    @Override
    public int compareTo(Butaca otra) {
        return Integer.compare(numero, otra.numero);
    }

    @Override
    public String toString() {
        return String.valueOf(numero);
    }
}

package modelo.precios;

import excepciones.DatoInvalidoException;

import java.math.BigDecimal;
import java.math.RoundingMode;

// Objeto de valor para los precios (antes eran double, con los errores de redondeo de punto flotante).
// Inmutable: cada operación devuelve un Dinero nuevo
public final class Dinero implements Comparable<Dinero> {
    private static final int DECIMALES = 2;

    public static final Dinero CERO = new Dinero(BigDecimal.ZERO);

    private final BigDecimal monto;

    private Dinero(BigDecimal monto) {
        if (monto.signum() < 0) {
            throw new DatoInvalidoException("Un precio no puede ser negativo.");
        }
        this.monto = monto.setScale(DECIMALES, RoundingMode.HALF_EVEN);
    }

    public static Dinero de(long monto) {
        return new Dinero(BigDecimal.valueOf(monto));
    }

    public static Dinero de(String monto) {
        return new Dinero(new BigDecimal(monto.trim()));
    }

    public Dinero conDescuento(Porcentaje porcentaje) {
        return new Dinero(porcentaje.descontarDe(monto));
    }

    public BigDecimal getMonto() {
        return monto;
    }

    @Override
    public int compareTo(Dinero otro) {
        return monto.compareTo(otro.monto);
    }

    @Override
    public boolean equals(Object otro) {
        return otro instanceof Dinero dinero && monto.equals(dinero.monto);
    }

    @Override
    public int hashCode() {
        return monto.hashCode();
    }

    @Override
    public String toString() {
        return "$ " + monto.toPlainString();
    }
}

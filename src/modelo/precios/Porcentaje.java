package modelo.precios;

import excepciones.DatoInvalidoException;

import java.math.BigDecimal;

// Objeto de valor: un porcentaje de descuento entre 0 y 100.
// Antes el cálculo "precio - precio * porcentaje / 100" estaba repetido en TipoTarifa y DescuentoCompraAnticipada
public record Porcentaje(int valor) {
    private static final BigDecimal CIEN = BigDecimal.valueOf(100);

    public Porcentaje {
        if (valor < 0 || valor > 100) {
            throw new DatoInvalidoException("El porcentaje debe estar entre 0 y 100.");
        }
    }

    BigDecimal descontarDe(BigDecimal monto) {
        return monto.subtract(monto.multiply(BigDecimal.valueOf(valor)).divide(CIEN));
    }

    public boolean esCero() {
        return valor == 0;
    }

    @Override
    public String toString() {
        return valor + "%";
    }
}

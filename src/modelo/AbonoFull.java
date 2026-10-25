package modelo;

import java.time.LocalDate;

public class AbonoFull extends Abono {

    public AbonoFull(String codigo, Espectador espectador, TipoTarifa tarifa, double precio, LocalDate fechaCompra) {
        super(codigo, espectador, tarifa, precio, fechaCompra);
    }

    // CORRECCION S3: le pregunta el tipo a la función en lugar de usar instanceof.
    @Override
    public boolean cubre(Funcion funcion) {
        return funcion.getTipo() == TipoFuncion.PRESENCIAL;
    }

    @Override
    public TipoAbono getTipo() {
        return TipoAbono.FULL;
    }

    @Override
    public String getCobertura() {
        return "Todas las funciones presenciales";
    }
}

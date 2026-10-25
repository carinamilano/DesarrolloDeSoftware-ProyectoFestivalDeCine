package modelo;

import java.time.LocalDate;

public class AbonoFull extends Abono {

    public AbonoFull(String codigo, Espectador espectador, TipoTarifa tarifa, double precio, LocalDate fechaCompra) {
        super(codigo, espectador, tarifa, precio, fechaCompra);
    }

    @Override
    public boolean cubre(Funcion funcion) {
        return funcion instanceof FuncionPresencial;
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

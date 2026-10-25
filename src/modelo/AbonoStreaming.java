package modelo;

import java.time.LocalDate;

public class AbonoStreaming extends Abono {

    public AbonoStreaming(String codigo, Espectador espectador, TipoTarifa tarifa, double precio, LocalDate fechaCompra) {
        super(codigo, espectador, tarifa, precio, fechaCompra);
    }

    @Override
    public boolean cubre(Funcion funcion) {
        return funcion instanceof FuncionStreaming;
    }

    @Override
    public TipoAbono getTipo() {
        return TipoAbono.STREAMING;
    }

    @Override
    public String getCobertura() {
        return "Todas las funciones streaming";
    }
}

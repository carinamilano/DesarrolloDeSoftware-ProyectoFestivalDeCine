package servicios.precios;

import modelo.funciones.Funcion;
import modelo.precios.Dinero;
import modelo.precios.Porcentaje;
import modelo.precios.TipoTarifa;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// 10% adicional si la entrada se compra con 7 días o más de anticipación
public class DescuentoCompraAnticipada implements ReglaDescuento {
    public static final int DIAS_ANTICIPACION = 7;
    public static final Porcentaje DESCUENTO = new Porcentaje(10);

    private final Clock reloj;

    public DescuentoCompraAnticipada(Clock reloj) {
        this.reloj = reloj;
    }

    @Override
    public Dinero aplicar(Dinero precio, Funcion funcion, TipoTarifa tarifa) {
        long diasHastaFuncion = ChronoUnit.DAYS.between(LocalDate.now(reloj), funcion.getFecha());
        if (diasHastaFuncion < DIAS_ANTICIPACION) return precio;
        return precio.conDescuento(DESCUENTO);
    }
}

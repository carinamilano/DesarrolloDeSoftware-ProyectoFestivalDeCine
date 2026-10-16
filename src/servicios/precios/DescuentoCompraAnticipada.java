package servicios.precios;

import modelo.Funcion;
import modelo.TipoTarifa;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// 10% adicional si la entrada se compra con 7 días o más de anticipación
public class DescuentoCompraAnticipada implements ReglaDescuento {
    public static final int DIAS_ANTICIPACION = 7;
    public static final int PORCENTAJE = 10;

    @Override
    public double aplicar(double precio, Funcion funcion, TipoTarifa tarifa) {
        long diasHastaFuncion = ChronoUnit.DAYS.between(LocalDate.now(), funcion.getFecha());
        if (diasHastaFuncion < DIAS_ANTICIPACION) return precio;
        return precio - precio * PORCENTAJE / 100;
    }
}

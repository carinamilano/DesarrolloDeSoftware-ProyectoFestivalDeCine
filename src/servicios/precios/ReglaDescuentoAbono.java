package servicios.precios;

import modelo.abonos.TipoAbono;
import modelo.precios.Dinero;
import modelo.precios.TipoTarifa;

// Strategy para los descuentos de abonos: los abonos ahora también pasan por el Tarifario
public interface ReglaDescuentoAbono {

    Dinero aplicar(Dinero precio, TipoAbono tipo, TipoTarifa tarifa);
}

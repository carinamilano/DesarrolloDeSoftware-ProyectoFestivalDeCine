package servicios.precios;

import modelo.abonos.TipoAbono;
import modelo.funciones.Funcion;
import modelo.precios.Dinero;
import modelo.precios.TipoTarifa;

// General sin descuento, Estudiante 30% y Jubilado 40%. Vale para entradas y abonos
public class DescuentoPorTarifa implements ReglaDescuento, ReglaDescuentoAbono {

    @Override
    public Dinero aplicar(Dinero precio, Funcion funcion, TipoTarifa tarifa) {
        return tarifa.aplicarA(precio);
    }

    @Override
    public Dinero aplicar(Dinero precio, TipoAbono tipo, TipoTarifa tarifa) {
        return tarifa.aplicarA(precio);
    }
}

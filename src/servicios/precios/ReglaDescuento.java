package servicios.precios;

import modelo.funciones.Funcion;
import modelo.precios.Dinero;
import modelo.precios.TipoTarifa;

// Strategy: cada regla de descuento de entradas es intercambiable. Agregar un descuento nuevo
// es escribir otra clase, sin modificar el Tarifario (OCP)
public interface ReglaDescuento {

    Dinero aplicar(Dinero precio, Funcion funcion, TipoTarifa tarifa);
}

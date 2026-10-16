package servicios.precios;

import modelo.Funcion;
import modelo.TipoTarifa;

// Strategy: cada regla de descuento es intercambiable. Agregar un descuento nuevo
// es escribir otra clase, sin modificar el Tarifario (OCP)
public interface ReglaDescuento {

    double aplicar(double precio, Funcion funcion, TipoTarifa tarifa);
}

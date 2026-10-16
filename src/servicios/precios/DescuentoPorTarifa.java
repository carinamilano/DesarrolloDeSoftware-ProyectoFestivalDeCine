package servicios.precios;

import modelo.Funcion;
import modelo.TipoTarifa;

// General sin descuento, Estudiante 30% y Jubilado 40%
public class DescuentoPorTarifa implements ReglaDescuento {

    @Override
    public double aplicar(double precio, Funcion funcion, TipoTarifa tarifa) {
        return tarifa.aplicarA(precio);
    }
}

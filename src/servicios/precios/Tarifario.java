package servicios.precios;

import modelo.abonos.TipoAbono;
import modelo.funciones.Funcion;
import modelo.precios.Dinero;
import modelo.precios.TipoTarifa;

import java.time.Clock;
import java.util.List;

// Contexto del Strategy: aplica las reglas de descuento sin preguntar de qué tipo son.
// Es el único lugar que calcula precios: el de las entradas y también el de los abonos
// (antes los abonos aplicaban la tarifa por su cuenta, sin pasar por acá)
public class Tarifario {
    private final List<ReglaDescuento> descuentosEntrada;
    private final List<ReglaDescuentoAbono> descuentosAbono;

    public Tarifario(Clock reloj) {
        this(List.of(new DescuentoPorTarifa(), new DescuentoCompraAnticipada(reloj)),
                List.of(new DescuentoPorTarifa()));
    }

    public Tarifario(List<ReglaDescuento> descuentosEntrada, List<ReglaDescuentoAbono> descuentosAbono) {
        this.descuentosEntrada = List.copyOf(descuentosEntrada);
        this.descuentosAbono = List.copyOf(descuentosAbono);
    }

    // El precio base lo conoce cada función (FuncionPresencial / FuncionStreaming)
    public Dinero calcularPrecioEntrada(Funcion funcion, TipoTarifa tarifa) {
        Dinero precio = funcion.getPrecioBase();
        for (ReglaDescuento descuento : descuentosEntrada) {
            precio = descuento.aplicar(precio, funcion, tarifa);
        }
        return precio;
    }

    public Dinero calcularPrecioAbono(TipoAbono tipo, TipoTarifa tarifa) {
        Dinero precio = tipo.getPrecioBase();
        for (ReglaDescuentoAbono descuento : descuentosAbono) {
            precio = descuento.aplicar(precio, tipo, tarifa);
        }
        return precio;
    }
}

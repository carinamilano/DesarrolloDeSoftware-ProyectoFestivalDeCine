package servicios.precios;

import modelo.Funcion;
import modelo.TipoTarifa;

import java.util.List;

// Contexto del Strategy: aplica las reglas de descuento sin preguntar de qué tipo son.
// El precio base lo conoce cada función (FuncionPresencial / FuncionStreaming)
public class Tarifario {
    private final List<ReglaDescuento> descuentosEntrada;

    public Tarifario() {
        this(List.of(new DescuentoPorTarifa(), new DescuentoCompraAnticipada()));
    }

    public Tarifario(List<ReglaDescuento> descuentosEntrada) {
        this.descuentosEntrada = List.copyOf(descuentosEntrada);
    }

    public double calcularPrecioEntrada(Funcion funcion, TipoTarifa tarifa) {
        double precio = funcion.getPrecioBase();
        for (ReglaDescuento descuento : descuentosEntrada) {
            precio = descuento.aplicar(precio, funcion, tarifa);
        }
        return precio;
    }
}

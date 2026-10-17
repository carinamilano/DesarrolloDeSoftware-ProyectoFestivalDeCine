package servicios;

import modelo.comun.Numerador;
import modelo.funciones.Butaca;
import modelo.funciones.Entrada;
import modelo.funciones.Funcion;
import modelo.funciones.SolicitudEntrada;
import modelo.personas.Espectador;
import modelo.precios.Dinero;
import modelo.precios.TipoTarifa;
import repositorio.Repositorio;
import servicios.precios.Tarifario;

import java.util.List;
import java.util.Optional;

// Venta de entradas sueltas. La validación (que la función no haya comenzado, butaca, capacidad)
// y la creación de la entrada las hace la propia función (Experto + Creator); el precio, el Tarifario
public class VentaServicio {
    private final Repositorio<Entrada> entradas;
    private final Tarifario tarifario;
    private final Numerador numerador;

    public VentaServicio(Repositorio<Entrada> entradas, Tarifario tarifario, Numerador numerador) {
        this.entradas = entradas;
        this.tarifario = tarifario;
        this.numerador = numerador;
    }

    // La butaca es opcional: las funciones streaming no tienen
    public Entrada venderEntrada(Espectador espectador, Funcion funcion, TipoTarifa tarifa, Optional<Butaca> butaca) {
        Dinero precio = tarifario.calcularPrecioEntrada(funcion, tarifa);
        return emitir(funcion, SolicitudEntrada.venta(espectador, precio, butaca));
    }

    public Dinero cotizar(Funcion funcion, TipoTarifa tarifa) {
        return tarifario.calcularPrecioEntrada(funcion, tarifa);
    }

    // También lo usa AbonoServicio para emitir las reservas
    Entrada emitir(Funcion funcion, SolicitudEntrada solicitud) {
        Entrada entrada = funcion.emitirEntrada(numerador, solicitud);
        entradas.agregar(entrada);
        return entrada;
    }

    void darDeBaja(Entrada entrada) {
        entradas.quitar(entrada);
    }

    public Entrada buscarEntrada(String codigo) {
        return entradas.obtener(entrada -> entrada.getCodigo().equalsIgnoreCase(codigo), "Entrada", "codigo: " + codigo);
    }

    public List<Entrada> getEntradas() {
        return entradas.listar();
    }
}

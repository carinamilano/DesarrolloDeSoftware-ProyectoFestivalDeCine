package servicios;

import modelo.*;
import repositorio.Repositorio;
import servicios.precios.Tarifario;

import java.util.List;

// Venta de entradas sueltas. La validación de butaca/capacidad y la creación de la entrada
// las hace la propia función (Experto + Creator); el precio, el Tarifario
public class VentaServicio {
    private final Repositorio<Entrada> entradas;
    private final PersonaServicio personas;
    private final Tarifario tarifario;
    private final Numerador numerador;

    public VentaServicio(Repositorio<Entrada> entradas, PersonaServicio personas, Tarifario tarifario, Numerador numerador) {
        this.entradas = entradas;
        this.personas = personas;
        this.tarifario = tarifario;
        this.numerador = numerador;
    }

    public Entrada venderEntrada(String nombreEspectador, Funcion funcion, TipoTarifa tarifa, int numeroButaca) {
        Espectador espectador = personas.buscarEspectador(nombreEspectador);
        double precio = tarifario.calcularPrecioEntrada(funcion, tarifa);
        return emitir(funcion, SolicitudEntrada.venta(espectador, precio, numeroButaca));
    }

    // Para funciones streaming, que no tienen butaca
    public Entrada venderEntrada(String nombreEspectador, Funcion funcion, TipoTarifa tarifa) {
        return venderEntrada(nombreEspectador, funcion, tarifa, SolicitudEntrada.SIN_BUTACA);
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

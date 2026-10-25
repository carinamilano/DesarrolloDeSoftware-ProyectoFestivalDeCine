package servicios;

import excepciones.EntidadDuplicadaException;
import excepciones.EntidadNoEncontradaException;
import excepciones.FuncionNoDisponibleException;
import excepciones.ReglaNegocioException;
import modelo.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// CORRECCION S1: la venta de entradas tiene su propio servicio (antes estaba en EntradaServicio).
public class VentaServicio {

    public static final int DIAS_COMPRA_ANTICIPADA = 7;
    public static final int PORCENTAJE_COMPRA_ANTICIPADA = 10;

    // CORRECCION S3: se guardan también por tipo, así no hace falta instanceof para recorrerlas.
    private List<Entrada> entradas;
    private List<EntradaPresencial> entradasPresenciales;
    private List<EntradaStreaming> entradasStreaming;
    private Contador contador;

    public VentaServicio() {
        this.entradas = new ArrayList<>();
        this.entradasPresenciales = new ArrayList<>();
        this.entradasStreaming = new ArrayList<>();
        this.contador = new Contador();
    }

    // CORRECCION S3: el precio base lo responde la función; antes se preguntaba su tipo con instanceof.
    public double calcularPrecioEntrada(Funcion funcion, TipoTarifa tarifa) {
        double precio = tarifa.aplicarA(funcion.getPrecioBase());
        long diasHastaFuncion = ChronoUnit.DAYS.between(LocalDate.now(), funcion.getFecha());
        if (diasHastaFuncion >= DIAS_COMPRA_ANTICIPADA) {
            precio = precio - precio * PORCENTAJE_COMPRA_ANTICIPADA / 100;
        }
        return precio;
    }

    // CORRECCION I10: recibe el Espectador elegido; antes recibía el nombre y lo volvía a buscar.
    // CORRECCION S14: se quitaron las sobrecargas sin tarifa, que solo usaba InicializadorDatos.
    public EntradaPresencial venderEntradaPresencial(Espectador espectador, FuncionPresencial funcion,
                                                     int numeroButaca, TipoTarifa tarifa) {
        double precio = calcularPrecioEntrada(funcion, tarifa);
        return emitirPresencial(espectador, funcion, numeroButaca, precio, null);
    }

    public EntradaStreaming venderEntradaStreaming(Espectador espectador, FuncionStreaming funcion, TipoTarifa tarifa) {
        double precio = calcularPrecioEntrada(funcion, tarifa);
        return emitirStreaming(espectador, funcion, precio, null);
    }

    /* CORRECCION I1: paso común a la venta y a la reserva con abono; no se emiten entradas
    de funciones que ya comenzaron. Lo usa también AbonoServicio.*/
    EntradaPresencial emitirPresencial(Espectador espectador, FuncionPresencial funcion,
                                       int numeroButaca, double precio, Abono abono) {
        validarQueNoComenzo(funcion);
        return crearPresencial(contador.siguiente("EP-"), funcion, espectador, numeroButaca, precio, abono);
    }

    EntradaStreaming emitirStreaming(Espectador espectador, FuncionStreaming funcion, double precio, Abono abono) {
        validarQueNoComenzo(funcion);
        return crearStreaming(contador.siguiente("ES-"), funcion, espectador, precio, abono);
    }

    private void validarQueNoComenzo(Funcion funcion) {
        if (funcion.yaComenzo()) {
            throw new FuncionNoDisponibleException("la función de " + funcion.getPelicula().getTitulo()
                    + " del " + funcion.getFecha() + " a las " + funcion.getHorario() + " ya comenzó.");
        }
    }

    // CORRECCION I9: la butaca y la capacidad las valida la función al ocupar el lugar.
    private EntradaPresencial crearPresencial(String codigo, FuncionPresencial funcion, Espectador espectador,
                                              int numeroButaca, double precio, Abono abono) {
        funcion.ocuparButaca(numeroButaca);
        EntradaPresencial entrada;
        try {
            entrada = new EntradaPresencial(codigo, funcion, espectador, numeroButaca, precio, abono);
        } catch (RuntimeException e) {
            funcion.liberarButaca(numeroButaca);
            throw e;
        }
        registrar(entrada);
        entradasPresenciales.add(entrada);
        return entrada;
    }

    private EntradaStreaming crearStreaming(String codigo, FuncionStreaming funcion, Espectador espectador,
                                            double precio, Abono abono) {
        funcion.agregarEspectador();
        EntradaStreaming entrada;
        try {
            entrada = new EntradaStreaming(codigo, funcion, espectador, precio, abono);
        } catch (RuntimeException e) {
            funcion.quitarEspectador();
            throw e;
        }
        registrar(entrada);
        entradasStreaming.add(entrada);
        return entrada;
    }

    // CORRECCION I14: Entrada.vincular la agrega al espectador y, si es reserva, al abono.
    private void registrar(Entrada entrada) {
        entrada.vincular();
        entradas.add(entrada);
    }

    public EntradaPresencial restaurarEntradaPresencial(String codigo, FuncionPresencial funcion, Espectador espectador,
                                                        int numeroButaca, double precio, Abono abono) {
        validarCodigoLibre(codigo);
        contador.registrarUsado(codigo);
        return crearPresencial(codigo, funcion, espectador, numeroButaca, precio, abono);
    }

    public EntradaStreaming restaurarEntradaStreaming(String codigo, FuncionStreaming funcion, Espectador espectador,
                                                      double precio, Abono abono) {
        validarCodigoLibre(codigo);
        contador.registrarUsado(codigo);
        return crearStreaming(codigo, funcion, espectador, precio, abono);
    }

    private void validarCodigoLibre(String codigo) {
        for (Entrada e : entradas) {
            if (e.getCodigo().equalsIgnoreCase(codigo)) {
                throw new EntidadDuplicadaException("Ya existe la entrada " + codigo + ".");
            }
        }
    }

    // CORRECCION I2: el control de "ya comenzó" usa fecha y hora.
    // CORRECCION S3: entrada.liberarLugar() reemplaza los instanceof y casts.
    public void cancelarReserva(Entrada entrada) {
        if (!entrada.esReservaDeAbono()) {
            throw new ReglaNegocioException("Solo se pueden cancelar reservas hechas con abono.");
        }
        if (entrada.getFuncion().yaComenzo()) {
            throw new ReglaNegocioException("No se puede cancelar una reserva de una función que ya comenzó.");
        }
        entrada.liberarLugar();
        entrada.desvincular();
        entradas.remove(entrada);
        entradasPresenciales.remove(entrada);
        entradasStreaming.remove(entrada);
    }

    public Entrada buscarEntrada(String codigo) {
        for (Entrada e : entradas) {
            if (e.getCodigo().equalsIgnoreCase(codigo)) return e;
        }
        throw new EntidadNoEncontradaException("Entrada", "codigo: " + codigo);
    }

    // CORRECCION I13: las listas no se pueden modificar desde afuera.
    public List<Entrada> getEntradas() {
        return Collections.unmodifiableList(entradas);
    }
    public List<EntradaPresencial> getEntradasPresenciales() {
        return Collections.unmodifiableList(entradasPresenciales);
    }
    public List<EntradaStreaming> getEntradasStreaming() {
        return Collections.unmodifiableList(entradasStreaming);
    }
}

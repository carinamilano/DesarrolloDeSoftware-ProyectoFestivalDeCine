package servicios;

import excepciones.AbonoInvalidoException;
import excepciones.EntidadDuplicadaException;
import excepciones.EntidadNoEncontradaException;
import modelo.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// CORRECCION S1: los abonos tienen su propio servicio (antes estaban en EntradaServicio).
public class AbonoServicio {
    private FuncionServicio funcionServicio;
    private VentaServicio ventaServicio;
    private List<Abono> abonos;
    private Contador contador;

    public AbonoServicio(FuncionServicio funcionServicio, VentaServicio ventaServicio) {
        this.funcionServicio = funcionServicio;
        this.ventaServicio = ventaServicio;
        this.abonos = new ArrayList<>();
        this.contador = new Contador();
    }

    // CORRECCION S4: recibe el enum TipoAbono, que trae su precio base; antes recibía un texto.
    public double calcularPrecioAbono(TipoAbono tipo, TipoTarifa tarifa) {
        return tarifa.aplicarA(tipo.getPrecioBase());
    }

    // ========== COMPRA ==========

    // CORRECCION I10: recibe el Espectador elegido, no su nombre.
    public AbonoTresDias comprarAbonoTresDias(Espectador espectador, TipoTarifa tarifa, List<LocalDate> dias) {
        // CORRECCION I9: que sean 3 días distintos lo valida AbonoTresDias.

        if (dias != null) {
            for (LocalDate d : dias) {
                if (d != null && d.isBefore(LocalDate.now())) {
                    throw new AbonoInvalidoException("el día " + d + " ya pasó.");
                }
                if (d != null && !funcionServicio.hayFuncionesPresencialesEl(d)) {
                    throw new AbonoInvalidoException("no hay funciones presenciales el " + d + ".");
                }
            }
        }
        double precio = calcularPrecioAbono(TipoAbono.TRES_DIAS, tarifa);
        AbonoTresDias abono = new AbonoTresDias(contador.siguiente("AB-"), espectador, tarifa, precio, LocalDate.now(), dias);
        registrar(abono);
        return abono;
    }

    public AbonoFull comprarAbonoFull(Espectador espectador, TipoTarifa tarifa) {
        double precio = calcularPrecioAbono(TipoAbono.FULL, tarifa);
        AbonoFull abono = new AbonoFull(contador.siguiente("AB-"), espectador, tarifa, precio, LocalDate.now());
        registrar(abono);
        return abono;
    }

    public AbonoStreaming comprarAbonoStreaming(Espectador espectador, TipoTarifa tarifa) {
        double precio = calcularPrecioAbono(TipoAbono.STREAMING, tarifa);
        AbonoStreaming abono = new AbonoStreaming(contador.siguiente("AB-"), espectador, tarifa, precio, LocalDate.now());
        registrar(abono);
        return abono;
    }

    // CORRECCION I14: Abono.vincular lo agrega a su espectador.
    private void registrar(Abono abono) {
        abono.vincular();
        abonos.add(abono);
    }

    public Abono restaurarAbono(String codigo, TipoAbono tipo, Espectador espectador, TipoTarifa tarifa,
                                double precio, LocalDate fechaCompra, List<LocalDate> dias) {
        for (Abono a : abonos) {
            if (a.getCodigo().equalsIgnoreCase(codigo)) {
                throw new EntidadDuplicadaException("Ya existe el abono " + codigo + ".");
            }
        }
        Abono abono;
        switch (tipo) {
            case TRES_DIAS -> abono = new AbonoTresDias(codigo, espectador, tarifa, precio, fechaCompra, dias);
            case FULL -> abono = new AbonoFull(codigo, espectador, tarifa, precio, fechaCompra);
            default -> abono = new AbonoStreaming(codigo, espectador, tarifa, precio, fechaCompra);
        }
        contador.registrarUsado(codigo);
        registrar(abono);
        return abono;
    }

    public EntradaPresencial reservarPresencial(Abono abono, FuncionPresencial funcion, int numeroButaca) {
        validarReserva(abono, funcion);
        return ventaServicio.emitirPresencial(abono.getEspectador(), funcion, numeroButaca, 0, abono);
    }

    public EntradaStreaming reservarStreaming(Abono abono, FuncionStreaming funcion) {
        validarReserva(abono, funcion);
        return ventaServicio.emitirStreaming(abono.getEspectador(), funcion, 0, abono);
    }

    /* CORRECCION S3: la interfaz elige la función en un combo general; acá se busca la versión de su tipo
    por código (sin instanceof ni casts) y se llama a la reserva que corresponde.*/
    public Entrada reservar(Abono abono, Funcion funcion, int numeroButaca) {
        if (funcion.requiereButaca()) {
            return reservarPresencial(abono, funcionServicio.buscarFuncionPresencial(funcion.getCodigo()), numeroButaca);
        }
        return reservarStreaming(abono, funcionServicio.buscarFuncionStreaming(funcion.getCodigo()));
    }

    private void validarReserva(Abono abono, Funcion funcion) {
        // CORRECCION I2: compara fecha y hora; antes una función de hoy que ya empezó se podía reservar.
        if (funcion.yaComenzo()) {
            throw new AbonoInvalidoException("la función ya comenzó.");
        }
        if (!abono.cubre(funcion)) {
            throw new AbonoInvalidoException("el " + abono.getTipo() + " no cubre la función del " + funcion.getFecha() + ".");
        }
        // CORRECCION I5: rechaza reservas que se superponen (con duración), no solo las de la misma hora.
        if (abono.tieneReservaSuperpuestaCon(funcion)) {
            throw new AbonoInvalidoException("ya tiene una reserva que se superpone con la función del "
                    + funcion.getFecha() + " a las " + funcion.getHorario() + ".");
        }
    }

    // CORRECCION S7/I2: antes este filtro estaba en PanelAbonos y miraba solo la fecha.
    public List<Funcion> funcionesReservables(Abono abono) {
        List<Funcion> reservables = new ArrayList<>();
        for (Funcion f : funcionServicio.getFunciones()) {
            if (!f.yaComenzo() && abono.cubre(f)) {
                reservables.add(f);
            }
        }
        return reservables;
    }

    public void cancelarReserva(Entrada reserva) {
        ventaServicio.cancelarReserva(reserva);
    }

    public Abono buscarAbono(String codigo) {
        for (Abono a : abonos) {
            if (a.getCodigo().equalsIgnoreCase(codigo)) return a;
        }
        throw new EntidadNoEncontradaException("Abono", "codigo: " + codigo);
    }

    // CORRECCION I13: la lista no se puede modificar desde afuera.
    public List<Abono> getAbonos() {
        return Collections.unmodifiableList(abonos);
    }
}

package servicios;

import excepciones.AbonoInvalidoException;
import modelo.*;
import repositorio.Repositorio;

import java.time.LocalDate;
import java.util.List;

// Venta de abonos y reservas con abono (antes era otra de las responsabilidades de EntradaServicio)
public class AbonoServicio {
    private final Repositorio<Abono> abonos;
    private final PersonaServicio personas;
    private final FuncionServicio funciones;
    private final VentaServicio ventas;
    private final Numerador numerador;

    public AbonoServicio(Repositorio<Abono> abonos, PersonaServicio personas, FuncionServicio funciones,
                         VentaServicio ventas, Numerador numerador) {
        this.abonos = abonos;
        this.personas = personas;
        this.funciones = funciones;
        this.ventas = ventas;
        this.numerador = numerador;
    }

    // ========== COMPRA ==========

    public AbonoTresDias comprarAbonoTresDias(String nombreEspectador, TipoTarifa tarifa, List<LocalDate> dias) {
        AbonoTresDias.validarDias(dias);
        validarQueHayFuncionesPresenciales(dias);
        Espectador espectador = personas.buscarEspectador(nombreEspectador);
        return registrar(new AbonoTresDias(nuevoCodigo(), espectador, tarifa, dias));
    }

    public AbonoFull comprarAbonoFull(String nombreEspectador, TipoTarifa tarifa) {
        Espectador espectador = personas.buscarEspectador(nombreEspectador);
        return registrar(new AbonoFull(nuevoCodigo(), espectador, tarifa));
    }

    public AbonoStreaming comprarAbonoStreaming(String nombreEspectador, TipoTarifa tarifa) {
        Espectador espectador = personas.buscarEspectador(nombreEspectador);
        return registrar(new AbonoStreaming(nuevoCodigo(), espectador, tarifa));
    }

    private void validarQueHayFuncionesPresenciales(List<LocalDate> dias) {
        for (LocalDate dia : dias) {
            if (!funciones.hayFuncionesPresencialesEl(dia)) {
                throw new AbonoInvalidoException("no hay funciones presenciales el " + dia + ".");
            }
        }
    }

    private <A extends Abono> A registrar(A abono) {
        abono.getEspectador().agregarAbono(abono);
        abonos.agregar(abono);
        return abono;
    }

    private String nuevoCodigo() {
        return "AB-" + numerador.siguiente();
    }

    // ========== RESERVAS ==========

    // Con un abono se reserva lugar sin volver a pagar; el abono valida que la cubra (Experto)
    public Entrada reservar(String codigoAbono, Funcion funcion, int numeroButaca) {
        Abono abono = buscarAbono(codigoAbono);
        abono.validarReserva(funcion);
        return ventas.emitir(funcion, SolicitudEntrada.reserva(abono, numeroButaca));
    }

    // Para funciones streaming, que no tienen butaca
    public Entrada reservar(String codigoAbono, Funcion funcion) {
        return reservar(codigoAbono, funcion, SolicitudEntrada.SIN_BUTACA);
    }

    public void cancelarReserva(String codigoEntrada) {
        Entrada reserva = ventas.buscarEntrada(codigoEntrada);
        reserva.cancelar();
        ventas.darDeBaja(reserva);
    }

    // Funciones que cubre el abono y que todavía no pasaron
    public List<Funcion> funcionesReservables(Abono abono) {
        return funciones.getFunciones().stream()
                .filter(abono::cubre)
                .filter(funcion -> !funcion.yaPaso())
                .toList();
    }

    public Abono buscarAbono(String codigo) {
        return abonos.obtener(abono -> abono.getCodigo().equalsIgnoreCase(codigo), "Abono", "codigo: " + codigo);
    }

    public List<Abono> getAbonos() {
        return abonos.listar();
    }
}

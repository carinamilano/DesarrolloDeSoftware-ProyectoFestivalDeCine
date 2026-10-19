package servicios;

import excepciones.AbonoInvalidoException;
import modelo.abonos.Abono;
import modelo.abonos.AbonoFull;
import modelo.abonos.AbonoStreaming;
import modelo.abonos.AbonoTresDias;
import modelo.abonos.TipoAbono;
import modelo.comun.Numerador;
import modelo.comun.Validar;
import modelo.funciones.Butaca;
import modelo.funciones.Entrada;
import modelo.funciones.Funcion;
import modelo.funciones.SolicitudEntrada;
import modelo.personas.Espectador;
import modelo.precios.Dinero;
import modelo.precios.TipoTarifa;
import repositorio.Repositorio;
import servicios.precios.Tarifario;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// Venta de abonos y reservas con abono
public class AbonoServicio {
    private final Repositorio<Abono> abonos;
    private final FuncionServicio funciones;
    private final VentaServicio ventas;
    private final Tarifario tarifario;
    private final Numerador numerador;
    private final Clock reloj;

    public AbonoServicio(Repositorio<Abono> abonos, FuncionServicio funciones, VentaServicio ventas,
                         Tarifario tarifario, Numerador numerador, Clock reloj) {
        this.abonos = abonos;
        this.funciones = funciones;
        this.ventas = ventas;
        this.tarifario = tarifario;
        this.numerador = numerador;
        this.reloj = reloj;
    }

    // ========== COMPRA ==========

    // El precio de los abonos también lo calcula el Tarifario (antes lo hacía TipoAbono por su cuenta)
    public Dinero cotizar(TipoAbono tipo, TipoTarifa tarifa) {
        return tarifario.calcularPrecioAbono(tipo, tarifa);
    }

    public AbonoTresDias comprarAbonoTresDias(Espectador espectador, TipoTarifa tarifa, List<LocalDate> dias) {
        AbonoTresDias.validarDias(dias, LocalDate.now(reloj));
        validarQueHayFuncionesPresenciales(dias);
        return registrar(AbonoTresDias.emitir(nuevoCodigo(), espectador, tarifa,
                cotizar(TipoAbono.TRES_DIAS, tarifa), dias, reloj));
    }

    public AbonoFull comprarAbonoFull(Espectador espectador, TipoTarifa tarifa) {
        return registrar(AbonoFull.emitir(nuevoCodigo(), espectador, tarifa, cotizar(TipoAbono.FULL, tarifa)));
    }

    public AbonoStreaming comprarAbonoStreaming(Espectador espectador, TipoTarifa tarifa) {
        return registrar(AbonoStreaming.emitir(nuevoCodigo(), espectador, tarifa, cotizar(TipoAbono.STREAMING, tarifa)));
    }

    private void validarQueHayFuncionesPresenciales(List<LocalDate> dias) {
        for (LocalDate dia : dias) {
            if (!funciones.hayFuncionesPresencialesEl(dia)) {
                throw new AbonoInvalidoException("no hay funciones presenciales el " + dia + ".");
            }
        }
    }

    // El abono ya se registró solo en su espectador al emitirse; acá solo se guarda en el repositorio
    private <A extends Abono> A registrar(A abono) {
        abonos.agregar(abono);
        return abono;
    }

    private String nuevoCodigo() {
        return "AB-" + numerador.siguiente();
    }

    // ========== RESERVAS ==========

    // Con un abono se reserva lugar sin volver a pagar; el abono valida que la cubra (Experto).
    // La butaca es opcional: las funciones streaming no tienen
    public Entrada reservar(Abono abono, Funcion funcion, Optional<Butaca> butaca) {
        Validar.presente(abono, "El abono es obligatorio.").validarReserva(funcion);
        return ventas.emitir(funcion, SolicitudEntrada.reserva(abono, butaca));
    }

    public void cancelarReserva(Entrada reserva) {
        reserva.cancelar();
        ventas.darDeBaja(reserva);
    }

    // Funciones que cubre el abono y que todavía no comenzaron
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

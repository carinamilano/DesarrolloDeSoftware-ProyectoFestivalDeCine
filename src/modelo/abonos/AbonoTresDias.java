package modelo.abonos;

import excepciones.AbonoInvalidoException;
import modelo.comun.Validar;
import modelo.funciones.FuncionPresencial;
import modelo.personas.Espectador;
import modelo.precios.Dinero;
import modelo.precios.TipoTarifa;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

public class AbonoTresDias extends Abono {
    public static final int CANTIDAD_DIAS = 3;

    private final List<LocalDate> dias;
    private final Clock reloj;

    private AbonoTresDias(String codigo, Espectador espectador, TipoTarifa tarifa, Dinero precio,
                          List<LocalDate> dias, Clock reloj) {
        super(codigo, espectador, tarifa, TipoAbono.TRES_DIAS, precio);
        validarCantidad(dias);
        this.dias = dias.stream().sorted().toList();
        this.reloj = Validar.presente(reloj, "El reloj es obligatorio.");
    }

    // Venta de un abono nuevo: los días no pueden haber pasado
    public static AbonoTresDias emitir(String codigo, Espectador espectador, TipoTarifa tarifa, Dinero precio,
                                       List<LocalDate> dias, Clock reloj) {
        validarDias(dias, LocalDate.now(reloj));
        return vincular(new AbonoTresDias(codigo, espectador, tarifa, precio, dias, reloj));
    }

    // Para la persistencia: un abono vendido antes, cuyos días ya pueden haber pasado
    public static AbonoTresDias restaurar(String codigo, Espectador espectador, TipoTarifa tarifa, Dinero precio,
                                          List<LocalDate> dias, Clock reloj) {
        return vincular(new AbonoTresDias(codigo, espectador, tarifa, precio, dias, reloj));
    }

    // Experto: el abono sabe qué días son válidos para él (exactamente 3, distintos y que no pasaron)
    public static void validarDias(List<LocalDate> dias, LocalDate hoy) {
        validarCantidad(dias);
        for (LocalDate dia : dias) {
            if (dia.isBefore(hoy)) {
                throw new AbonoInvalidoException("el día " + dia + " ya pasó.");
            }
        }
    }

    private static void validarCantidad(List<LocalDate> dias) {
        if (dias == null || dias.size() != CANTIDAD_DIAS) {
            throw new AbonoInvalidoException("debe elegir exactamente 3 días.");
        }
        if (dias.stream().distinct().count() != CANTIDAD_DIAS) {
            throw new AbonoInvalidoException("los 3 días deben ser distintos.");
        }
    }

    @Override
    public boolean cubrePresencial(FuncionPresencial funcion) {
        return dias.contains(funcion.getFecha());
    }

    // Días del abono que todavía no pasaron
    public long getDiasRestantes() {
        LocalDate hoy = LocalDate.now(reloj);
        return dias.stream().filter(dia -> !dia.isBefore(hoy)).count();
    }

    @Override
    public List<LocalDate> getDiasElegidos() {
        return dias;
    }

    @Override
    public String getCobertura() {
        return "Presencial: " + dias + " (quedan " + getDiasRestantes() + " días)";
    }
}

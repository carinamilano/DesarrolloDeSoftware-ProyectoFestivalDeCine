package modelo;

import excepciones.AbonoInvalidoException;

import java.time.LocalDate;
import java.util.List;

public class AbonoTresDias extends Abono {
    public static final int CANTIDAD_DIAS = 3;

    private final List<LocalDate> dias;

    public AbonoTresDias(String codigo, Espectador espectador, TipoTarifa tarifa, List<LocalDate> dias) {
        super(codigo, espectador, tarifa, TipoAbono.TRES_DIAS);
        validarDias(dias);
        this.dias = dias.stream().sorted().toList();
    }

    // Experto: el abono sabe qué días son válidos para él (exactamente 3, distintos y que no pasaron)
    public static void validarDias(List<LocalDate> dias) {
        if (dias == null || dias.size() != CANTIDAD_DIAS) {
            throw new AbonoInvalidoException("debe elegir exactamente 3 días.");
        }
        if (dias.stream().distinct().count() != CANTIDAD_DIAS) {
            throw new AbonoInvalidoException("los 3 días deben ser distintos.");
        }
        for (LocalDate dia : dias) {
            if (dia.isBefore(LocalDate.now())) {
                throw new AbonoInvalidoException("el día " + dia + " ya pasó.");
            }
        }
    }

    @Override
    protected boolean cubrePresencial(FuncionPresencial funcion) {
        return dias.contains(funcion.getFecha());
    }

    // Días del abono que todavía no pasaron
    public long getDiasRestantes() {
        LocalDate hoy = LocalDate.now();
        return dias.stream().filter(dia -> !dia.isBefore(hoy)).count();
    }

    @Override
    public String getCobertura() {
        return "Presencial: " + dias + " (quedan " + getDiasRestantes() + " días)";
    }
}

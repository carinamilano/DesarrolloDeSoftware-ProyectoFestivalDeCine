package modelo;

import excepciones.AbonoInvalidoException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AbonoTresDias extends Abono {
    public static final int CANTIDAD_DIAS = 3;

    private List<LocalDate> dias;

    public AbonoTresDias(String codigo, Espectador espectador, TipoTarifa tarifa, double precio,
                         LocalDate fechaCompra, List<LocalDate> dias) {
        super(codigo, espectador, tarifa, precio, fechaCompra);
        /* CORRECCION I9: los tres dias distintos se validan acá (antes lo hacía EntradaServicio)
         y se guarda una copia ordenada, así la lista no se puede cambiar desde afuera*/
        this.dias = validarDias(dias);
    }

    private static List<LocalDate> validarDias(List<LocalDate> dias) {
        if (dias == null || dias.size() != CANTIDAD_DIAS) {
            throw new AbonoInvalidoException("debe elegir exactamente 3 días.");
        }
        List<LocalDate> elegidos = new ArrayList<>();
        for (LocalDate d : dias) {
            if (d == null) {
                throw new AbonoInvalidoException("hay un día sin fecha.");
            }
            if (elegidos.contains(d)) {
                throw new AbonoInvalidoException("los 3 días deben ser distintos.");
            }
            elegidos.add(d);
        }
        Collections.sort(elegidos);
        return elegidos;
    }

    @Override
    public boolean cubre(Funcion funcion) {
        return funcion instanceof FuncionPresencial && dias.contains(funcion.getFecha());
    }

    public int getDiasRestantes() {
        int restantes = 0;
        for (LocalDate d : dias) {
            if (!d.isBefore(LocalDate.now())) restantes++;
        }
        return restantes;
    }

    @Override
    public TipoAbono getTipo() {
        return TipoAbono.TRES_DIAS;
    }

    @Override
    public String getCobertura() {
        return "Presencial: " + dias + " (quedan " + getDiasRestantes() + " días)";
    }

    @Override
    public List<LocalDate> getDiasElegidos() {
        return Collections.unmodifiableList(dias);
    }

    public List<LocalDate> getDias() {
        return Collections.unmodifiableList(dias);
    }
}

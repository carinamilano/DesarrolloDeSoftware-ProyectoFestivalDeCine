package modelo;

import java.time.LocalDate;
import java.util.List;

public class AbonoTresDias extends Abono {
    private List<LocalDate> dias;

    public AbonoTresDias(String codigo, Espectador espectador, TipoTarifa tarifa, double precio, List<LocalDate> dias) {
        super(codigo, espectador, tarifa, precio);
        this.dias = dias;
    }

    @Override
    public boolean cubre(Funcion funcion) {
        return funcion instanceof FuncionPresencial && dias.contains(funcion.getFecha());
    }

    // Días del abono que todavía no pasaron
    public int getDiasRestantes() {
        int restantes = 0;
        for (LocalDate d : dias) {
            if (!d.isBefore(LocalDate.now())) restantes++;
        }
        return restantes;
    }

    @Override
    public String getTipo() {
        return "Abono 3 días";
    }

    @Override
    public String getCobertura() {
        return "Presencial: " + dias + " (quedan " + getDiasRestantes() + " días)";
    }

    public List<LocalDate> getDias() {
        return dias;
    }
}

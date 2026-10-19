package modelo.abonos;

import modelo.funciones.FuncionPresencial;
import modelo.personas.Espectador;
import modelo.precios.Dinero;
import modelo.precios.TipoTarifa;

public class AbonoFull extends Abono {

    private AbonoFull(String codigo, Espectador espectador, TipoTarifa tarifa, Dinero precio) {
        super(codigo, espectador, tarifa, TipoAbono.FULL, precio);
    }

    // Crea el abono y lo registra en su espectador
    public static AbonoFull emitir(String codigo, Espectador espectador, TipoTarifa tarifa, Dinero precio) {
        return vincular(new AbonoFull(codigo, espectador, tarifa, precio));
    }

    @Override
    public boolean cubrePresencial(FuncionPresencial funcion) {
        return true;
    }

    @Override
    public String getCobertura() {
        return "Todas las funciones presenciales";
    }
}

package modelo.abonos;

import modelo.funciones.FuncionStreaming;
import modelo.personas.Espectador;
import modelo.precios.Dinero;
import modelo.precios.TipoTarifa;

public class AbonoStreaming extends Abono {

    private AbonoStreaming(String codigo, Espectador espectador, TipoTarifa tarifa, Dinero precio) {
        super(codigo, espectador, tarifa, TipoAbono.STREAMING, precio);
    }

    // Crea el abono y lo registra en su espectador
    public static AbonoStreaming emitir(String codigo, Espectador espectador, TipoTarifa tarifa, Dinero precio) {
        return vincular(new AbonoStreaming(codigo, espectador, tarifa, precio));
    }

    @Override
    public boolean cubreStreaming(FuncionStreaming funcion) {
        return true;
    }

    @Override
    public String getCobertura() {
        return "Todas las funciones streaming";
    }
}

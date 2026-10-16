package modelo;

public class AbonoStreaming extends Abono {

    public AbonoStreaming(String codigo, Espectador espectador, TipoTarifa tarifa) {
        super(codigo, espectador, tarifa, TipoAbono.STREAMING);
    }

    @Override
    protected boolean cubreStreaming(FuncionStreaming funcion) {
        return true;
    }

    @Override
    public String getCobertura() {
        return "Todas las funciones streaming";
    }
}

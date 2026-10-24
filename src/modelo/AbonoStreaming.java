package modelo;

public class AbonoStreaming extends Abono {

    public AbonoStreaming(String codigo, Espectador espectador, TipoTarifa tarifa, double precio) {
        super(codigo, espectador, tarifa, precio);
    }

    @Override
    public boolean cubre(Funcion funcion) {
        return funcion instanceof FuncionStreaming;
    }

    @Override
    public String getTipo() {
        return "Abono Streaming";
    }

    @Override
    public String getCobertura() {
        return "Todas las funciones streaming";
    }
}

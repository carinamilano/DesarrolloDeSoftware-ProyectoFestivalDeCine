package modelo;

public class AbonoFull extends Abono {

    public AbonoFull(String codigo, Espectador espectador, TipoTarifa tarifa) {
        super(codigo, espectador, tarifa, TipoAbono.FULL);
    }

    @Override
    protected boolean cubrePresencial(FuncionPresencial funcion) {
        return true;
    }

    @Override
    public String getCobertura() {
        return "Todas las funciones presenciales";
    }
}

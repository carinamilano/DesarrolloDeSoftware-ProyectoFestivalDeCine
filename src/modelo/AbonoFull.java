package modelo;

public class AbonoFull extends Abono {

    public AbonoFull(String codigo, Espectador espectador, TipoTarifa tarifa, double precio) {
        super(codigo, espectador, tarifa, precio);
    }

    @Override
    public boolean cubre(Funcion funcion) {
        return funcion instanceof FuncionPresencial;
    }

    @Override
    public String getTipo() {
        return "Abono Full";
    }

    @Override
    public String getCobertura() {
        return "Todas las funciones presenciales";
    }
}

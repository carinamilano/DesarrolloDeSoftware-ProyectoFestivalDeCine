package modelo;

public class EntradaPresencial extends Entrada {
    private int butaca;

    public EntradaPresencial(String codigo, Funcion funcion, Espectador espectador, int butaca, double precio, Abono abono) {
        super(codigo, funcion, espectador, precio, abono);
        this.butaca = butaca;
    }

    public int getButaca() { return butaca; }

    @Override
    public String toString() {
        return super.toString() + " - Butaca: " + butaca;
    }
}

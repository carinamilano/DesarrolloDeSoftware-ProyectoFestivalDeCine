package modelo;

public class EntradaPresencial extends Entrada {
    private FuncionPresencial funcionPresencial;
    private int butaca;

    public EntradaPresencial(String codigo, FuncionPresencial funcion, Espectador espectador, int butaca, double precio, Abono abono) {
        super(codigo, funcion, espectador, precio, abono);
        this.funcionPresencial = funcion;
        this.butaca = butaca;
    }

    public int getButaca() { return butaca; }

    // CORRECCION S3: reemplaza los instanceof de PanelVentaEntradas y PanelAbonos.
    @Override
    public String getDetalle() {
        return "Butaca: " + butaca;
    }

    // CORRECCION S3: reemplaza el instanceof de cancelarReserva.
    @Override
    public void liberarLugar() {
        funcionPresencial.liberarButaca(butaca);
    }

    @Override
    public String toString() {
        return super.toString() + " - Butaca: " + butaca;
    }
}

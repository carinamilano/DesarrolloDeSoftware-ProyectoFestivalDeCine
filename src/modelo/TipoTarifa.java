package modelo;

public enum TipoTarifa {
    GENERAL(0),
    ESTUDIANTE(30),
    JUBILADO(40);

    private int porcentajeDescuento;

    TipoTarifa(int porcentajeDescuento) {
        this.porcentajeDescuento = porcentajeDescuento;
    }

    public int getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    // CORRECCION S1: el descuento de la tarifa se calcula acá; lo usan la venta de entradas y la de abonos.
    public double aplicarA(double precio) {
        return precio - precio * porcentajeDescuento / 100;
    }
}

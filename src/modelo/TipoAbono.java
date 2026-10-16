package modelo;

// Reemplaza los Strings "TRES_DIAS", "FULL" y "STREAMING" que viajaban entre la UI y el servicio
public enum TipoAbono {
    TRES_DIAS("Abono 3 días", 20000),
    FULL("Abono Full", 45000),
    STREAMING("Abono Streaming", 25000);

    private final String etiqueta;
    private final double precioBase;

    TipoAbono(String etiqueta, double precioBase) {
        this.etiqueta = etiqueta;
        this.precioBase = precioBase;
    }

    public double precioCon(TipoTarifa tarifa) {
        return tarifa.aplicarA(precioBase);
    }

    public double getPrecioBase() {
        return precioBase;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}

package modelo;

public enum TipoTarifa {
    GENERAL("General", 0),
    ESTUDIANTE("Estudiante", 30),
    JUBILADO("Jubilado", 40);

    private final String descripcion;
    private final int porcentajeDescuento;

    TipoTarifa(String descripcion, int porcentajeDescuento) {
        this.descripcion = descripcion;
        this.porcentajeDescuento = porcentajeDescuento;
    }

    // Experto: la tarifa conoce su descuento, así que ella misma lo aplica
    public double aplicarA(double precio) {
        return precio - precio * porcentajeDescuento / 100;
    }

    public int getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    @Override
    public String toString() {
        return porcentajeDescuento == 0 ? descripcion : descripcion + " (-" + porcentajeDescuento + "%)";
    }
}

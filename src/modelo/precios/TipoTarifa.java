package modelo.precios;

public enum TipoTarifa {
    GENERAL("General", new Porcentaje(0)),
    ESTUDIANTE("Estudiante", new Porcentaje(30)),
    JUBILADO("Jubilado", new Porcentaje(40));

    private final String descripcion;
    private final Porcentaje descuento;

    TipoTarifa(String descripcion, Porcentaje descuento) {
        this.descripcion = descripcion;
        this.descuento = descuento;
    }

    // Experto: la tarifa conoce su descuento, así que ella misma lo aplica
    public Dinero aplicarA(Dinero precio) {
        return precio.conDescuento(descuento);
    }

    public Porcentaje getDescuento() {
        return descuento;
    }

    @Override
    public String toString() {
        return descuento.esCero() ? descripcion : descripcion + " (-" + descuento + ")";
    }
}

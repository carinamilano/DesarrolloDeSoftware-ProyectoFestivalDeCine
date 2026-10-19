package modelo.abonos;

import modelo.precios.Dinero;

// Reemplaza los Strings "TRES_DIAS", "FULL" y "STREAMING" que viajaban entre la UI y el servicio.
// Conoce el precio base; el precio final (con la tarifa) lo calcula el Tarifario
public enum TipoAbono {
    TRES_DIAS("Abono 3 días", Dinero.de(20000)),
    FULL("Abono Full", Dinero.de(45000)),
    STREAMING("Abono Streaming", Dinero.de(25000));

    private final String etiqueta;
    private final Dinero precioBase;

    TipoAbono(String etiqueta, Dinero precioBase) {
        this.etiqueta = etiqueta;
        this.precioBase = precioBase;
    }

    public Dinero getPrecioBase() {
        return precioBase;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}

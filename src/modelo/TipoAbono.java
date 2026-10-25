package modelo;

/* CORRECCIÓN S4: reemplaza los textos "TRES_DIAS", "FULL" y "STREAMING" que estaban repetidos
 en EntradaServicio y PanelAbonos. Cada tipo trae su etiqueta y su precio base.*/
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

    public String getEtiqueta() {
        return etiqueta;
    }

    public double getPrecioBase() {
        return precioBase;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}

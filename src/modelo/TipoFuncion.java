package modelo;

// CORRECCION S4: el tipo de función deja de ser texto ("Presencial", "Streaming", "PRESENCIAL"...).
public enum TipoFuncion {
    PRESENCIAL("Presencial"),
    STREAMING("Streaming");

    private final String etiqueta;

    TipoFuncion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}

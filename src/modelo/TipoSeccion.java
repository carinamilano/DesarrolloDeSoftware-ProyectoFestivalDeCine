package modelo;

public enum TipoSeccion {
    COMPETENCIA_OFICIAL("Competencia oficial"),
    CINE_INDEPENDIENTE("Cine independiente"),
    CORTOMETRAJE("Cortometraje"),
    NUEVOS_TALENTOS("Nuevos talentos");

    private final String descripcion;

    TipoSeccion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}

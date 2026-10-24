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
}

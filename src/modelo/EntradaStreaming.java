package modelo;

public class EntradaStreaming extends Entrada {
    private FuncionStreaming funcionStreaming;
    private String enlaceAcceso;

    public EntradaStreaming(String codigo, FuncionStreaming funcion, Espectador espectador, double precio, Abono abono) {
        super(codigo, funcion, espectador, precio, abono);
        this.funcionStreaming = funcion;
        this.enlaceAcceso = funcion.getEnlace();
    }

    public String getEnlaceAcceso() { return enlaceAcceso; }

    // CORRECCION S3: reemplaza los instanceof de PanelVentaEntradas y PanelAbonos.
    @Override
    public String getDetalle() {
        return "Enlace: " + enlaceAcceso;
    }

    // CORRECCION S3: reemplaza el instanceof de cancelarReserva.
    @Override
    public void liberarLugar() {
        funcionStreaming.quitarEspectador();
    }

    @Override
    public String toString() {
        return super.toString() + " - Enlace: " + enlaceAcceso;
    }
}

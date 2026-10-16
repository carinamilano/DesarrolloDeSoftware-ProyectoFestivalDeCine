package modelo;

public class EntradaStreaming extends Entrada {
    private final FuncionStreaming funcion;

    // La crea FuncionStreaming.emitirEntrada (Creator)
    EntradaStreaming(String codigo, FuncionStreaming funcion, SolicitudEntrada solicitud) {
        super(codigo, solicitud);
        this.funcion = funcion;
    }

    @Override
    public FuncionStreaming getFuncion() {
        return funcion;
    }

    @Override
    protected void liberarLugar() {
        funcion.desconectarEspectador();
    }

    @Override
    public String getDetalle() {
        return "Enlace: " + getEnlaceAcceso();
    }

    // El enlace de acceso es el de la función: ya no se guarda una copia
    public String getEnlaceAcceso() { return funcion.getEnlace(); }
}

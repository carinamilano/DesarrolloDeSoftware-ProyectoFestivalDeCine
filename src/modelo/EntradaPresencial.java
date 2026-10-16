package modelo;

public class EntradaPresencial extends Entrada {
    private final FuncionPresencial funcion;
    private final int butaca;

    // La crea FuncionPresencial.emitirEntrada (Creator)
    EntradaPresencial(String codigo, FuncionPresencial funcion, SolicitudEntrada solicitud) {
        super(codigo, solicitud);
        this.funcion = funcion;
        this.butaca = solicitud.numeroButaca();
    }

    @Override
    public FuncionPresencial getFuncion() {
        return funcion;
    }

    @Override
    protected void liberarLugar() {
        funcion.liberarButaca(butaca);
    }

    @Override
    public String getDetalle() {
        return "Butaca: " + butaca;
    }

    public int getButaca() { return butaca; }
}

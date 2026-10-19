package modelo.funciones;

import java.util.Optional;

public class EntradaPresencial extends Entrada {
    private final FuncionPresencial funcion;
    private final Butaca butaca;

    // La crea FuncionPresencial.emitirEntrada (Creator), después de validar y ocupar la butaca
    EntradaPresencial(String codigo, FuncionPresencial funcion, SolicitudEntrada solicitud) {
        super(codigo, solicitud);
        this.funcion = funcion;
        this.butaca = solicitud.butaca().orElseThrow();
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

    @Override
    public Optional<Butaca> getButacaAsignada() {
        return Optional.of(butaca);
    }

    public Butaca getButaca() { return butaca; }
}

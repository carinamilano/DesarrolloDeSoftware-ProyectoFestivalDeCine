package excepciones;

public class EntidadNoEncontradaException extends FestivalException {
    public EntidadNoEncontradaException(String entidad, String detalle) {
        super(entidad + " no encontrado/a: " + detalle);
    }
}
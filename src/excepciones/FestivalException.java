package excepciones;

/* CORRECCION S6: ya no se lanza directamente. Cada error usa una subclase especifica:
DatoInvalidoException, EntidadDuplicadaException, EntidadNoEncontradaException o ReglaNegocioException.*/
public abstract class FestivalException extends RuntimeException {
    protected FestivalException(String mensaje) {
        super(mensaje);
    }
}

package excepciones;

// CORRECCION S6: falta un dato obligatorio o un valor está fuera de rango.
public class DatoInvalidoException extends FestivalException {
    public DatoInvalidoException(String mensaje) {
        super(mensaje);
    }
}

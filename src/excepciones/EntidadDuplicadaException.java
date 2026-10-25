package excepciones;

// CORRECCION S6/I8: se intenta registrar algo que ya existe (email, titulo, sala, evaluación...).
public class EntidadDuplicadaException extends FestivalException {
    public EntidadDuplicadaException(String mensaje) {
        super(mensaje);
    }
}

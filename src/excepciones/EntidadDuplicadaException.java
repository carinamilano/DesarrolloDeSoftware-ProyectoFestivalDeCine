package excepciones;

// Se intenta registrar algo que ya existe (persona con el mismo email, película con el mismo título,
// una segunda evaluación del mismo jurado, etc.)
public class EntidadDuplicadaException extends FestivalException {
    public EntidadDuplicadaException(String mensaje) {
        super(mensaje);
    }
}

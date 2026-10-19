package excepciones;

// La operación no está permitida por una regla del festival. Las reglas más frecuentes
// tienen su propia subclase (butaca ocupada, abono inválido, voto inválido, etc.)
public class ReglaNegocioException extends FestivalException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}

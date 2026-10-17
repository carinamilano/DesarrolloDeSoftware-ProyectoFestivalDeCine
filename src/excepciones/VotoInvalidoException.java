package excepciones;

public class VotoInvalidoException extends ReglaNegocioException {
    public VotoInvalidoException(String mensaje) {
        super("Voto inválido: " + mensaje);
    }
}

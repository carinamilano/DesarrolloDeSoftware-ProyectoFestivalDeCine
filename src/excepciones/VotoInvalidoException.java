package excepciones;

public class VotoInvalidoException extends FestivalException {
    public VotoInvalidoException(String mensaje) {
        super("Voto inválido: " + mensaje);
    }
}

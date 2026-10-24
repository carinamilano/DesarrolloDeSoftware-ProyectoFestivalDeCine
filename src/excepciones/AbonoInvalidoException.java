package excepciones;

public class AbonoInvalidoException extends FestivalException {
    public AbonoInvalidoException(String mensaje) {
        super("Abono inválido: " + mensaje);
    }
}

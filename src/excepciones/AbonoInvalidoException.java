package excepciones;

public class AbonoInvalidoException extends ReglaNegocioException {
    public AbonoInvalidoException(String mensaje) {
        super("Abono inválido: " + mensaje);
    }
}

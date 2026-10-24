package excepciones;

public class ButacaOcupadaException extends FestivalException {
    public ButacaOcupadaException(int numeroButaca) {
        super("La butaca " + numeroButaca + " ya está ocupada.");
    }
}
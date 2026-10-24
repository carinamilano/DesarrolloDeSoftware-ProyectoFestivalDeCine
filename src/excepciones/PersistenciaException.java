package excepciones;

public class PersistenciaException extends Exception {
    public PersistenciaException(String mensaje) {
        super(mensaje);
    }
}
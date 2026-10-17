package excepciones;

// Base de todas las excepciones del dominio. Es abstracta: cada regla lanza una excepción específica
// (antes muchas reglas lanzaban FestivalException genérica). La UI atrapa esta clase y no RuntimeException
public abstract class FestivalException extends RuntimeException {
    protected FestivalException(String mensaje) {
        super(mensaje);
    }
}

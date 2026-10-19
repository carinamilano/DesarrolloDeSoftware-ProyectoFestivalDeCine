package excepciones;

// Un dato obligatorio que falta o un valor fuera de rango (puntaje, capacidad, año, butaca...)
public class DatoInvalidoException extends FestivalException {
    public DatoInvalidoException(String mensaje) {
        super(mensaje);
    }
}

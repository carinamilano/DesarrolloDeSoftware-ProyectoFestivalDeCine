package excepciones;

public class CapacidadExcedidaException extends FestivalException {
    public CapacidadExcedidaException(String nombreFuncion) {
        super("La función '" + nombreFuncion + "' no tiene capacidad disponible.");
    }
}
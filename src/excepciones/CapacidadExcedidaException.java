package excepciones;

public class CapacidadExcedidaException extends ReglaNegocioException {
    public CapacidadExcedidaException(String nombreFuncion) {
        super("La función '" + nombreFuncion + "' no tiene capacidad disponible.");
    }
}

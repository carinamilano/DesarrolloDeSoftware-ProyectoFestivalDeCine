package excepciones;

public class FuncionNoDisponibleException extends FestivalException {
    public FuncionNoDisponibleException(String detalle) {
        super("Función no disponible: " + detalle);
    }
}
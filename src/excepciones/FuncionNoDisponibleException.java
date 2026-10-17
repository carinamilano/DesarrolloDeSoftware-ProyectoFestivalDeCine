package excepciones;

public class FuncionNoDisponibleException extends ReglaNegocioException {
    public FuncionNoDisponibleException(String detalle) {
        super("Función no disponible: " + detalle);
    }
}

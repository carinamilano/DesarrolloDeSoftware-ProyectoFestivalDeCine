package excepciones;

/* CORRECCION S6: una regla del festival no permite la operación.
Las excepciones de abonos, butacas, capacidad, funciones y votos heredan de esta.*/
public class ReglaNegocioException extends FestivalException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}

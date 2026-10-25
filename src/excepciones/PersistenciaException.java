package excepciones;

import java.util.ArrayList;
import java.util.List;

public class PersistenciaException extends Exception {
    private final List<String> errores;

    public PersistenciaException(String mensaje) {
        super(mensaje);
        this.errores = new ArrayList<>();
    }

    // CORRECCION P10: conserva la causa original del error
    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.errores = new ArrayList<>();
    }

    // CORRECCION P10/P2: trae la lista de errores encontrados, con el archivo y la línea de cada uno.
    public PersistenciaException(String mensaje, List<String> errores) {
        super(mensaje);
        this.errores = new ArrayList<>(errores);
    }

    public List<String> getErrores() {
        return new ArrayList<>(errores);
    }
}

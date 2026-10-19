package excepciones;

import java.util.List;

// Checked: leer o escribir archivos puede fallar y quien llama tiene que decidir qué hacer.
// Puede traer la lista de todos los problemas encontrados (la carga ya no se corta en el primero)
public class PersistenciaException extends Exception {
    private final List<String> errores;

    public PersistenciaException(String mensaje) {
        this(mensaje, List.of(mensaje));
    }

    public PersistenciaException(String mensaje, List<String> errores) {
        super(mensaje);
        this.errores = List.copyOf(errores);
    }

    public List<String> getErrores() {
        return errores;
    }
}

package persistencia;

import excepciones.PersistenciaException;

// Abstracción de la que dependen Main y VentanaPrincipal (DIP / Variaciones Protegidas):
// para guardar en una base de datos alcanza con otra implementación de esta interfaz
public interface Persistencia {

    // Carga todo lo que pueda. Si hubo registros inválidos, los informa juntos en la excepción
    void cargar() throws PersistenciaException;

    // Copia los datos a guardar. Se llama desde el hilo de la UI; el Guardado que devuelve
    // ya no lee el modelo, así que se puede escribir desde otro hilo sin problemas de concurrencia
    Guardado prepararGuardado();

    default void guardar() throws PersistenciaException {
        prepararGuardado().escribir();
    }

    @FunctionalInterface
    interface Guardado {
        void escribir() throws PersistenciaException;
    }
}

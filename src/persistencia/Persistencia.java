package persistencia;

import excepciones.PersistenciaException;

// Abstracción de la que dependen Main y VentanaPrincipal (DIP / Variaciones Protegidas):
// para guardar en una base de datos alcanza con otra implementación de esta interfaz
public interface Persistencia {

    void cargar() throws PersistenciaException;

    void guardar() throws PersistenciaException;
}

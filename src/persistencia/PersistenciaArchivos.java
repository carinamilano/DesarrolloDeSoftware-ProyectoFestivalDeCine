package persistencia;

import excepciones.PersistenciaException;
import repositorio.Catalogo;

import java.time.Clock;
import java.util.List;

// Persiste en archivos de texto (formato CSV) todos los datos del sistema.
// La lectura la hace CargaDeArchivos y la escritura InstantaneaDeArchivos
public class PersistenciaArchivos implements Persistencia {
    private final Catalogo catalogo;
    private final Clock reloj;

    public PersistenciaArchivos(Catalogo catalogo, Clock reloj) {
        this.catalogo = catalogo;
        this.reloj = reloj;
    }

    @Override
    public void cargar() throws PersistenciaException {
        List<String> errores = new CargaDeArchivos(catalogo, reloj).cargar();
        if (!errores.isEmpty()) {
            throw new PersistenciaException("Se cargaron los datos, pero " + errores.size()
                    + " registro(s) tenían problemas y se omitieron.", errores);
        }
    }

    @Override
    public Guardado prepararGuardado() {
        return InstantaneaDeArchivos.tomar(catalogo);
    }
}

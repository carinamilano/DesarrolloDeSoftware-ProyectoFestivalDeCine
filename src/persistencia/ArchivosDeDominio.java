package persistencia;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/* CORRECCION S17: Persistencia era una Large Class (429 líneas, 36 métodos, conocía a los 7 servicios).
Se dividió por dominio: cada subclase carga y guarda solo sus archivos y conoce solo los servicios que usa.
Esta clase reúne lo que tienen en común: leer un archivo y anotar los errores de cada línea. */
abstract class ArchivosDeDominio {

    private final Path carpeta;
    private final List<String> erroresDeCarga;

    protected ArchivosDeDominio(Path carpeta, List<String> erroresDeCarga) {
        this.carpeta = carpeta;
        this.erroresDeCarga = erroresDeCarga;
    }

    abstract void cargar();

    abstract void agregarLineas(Map<String, List<String>> archivos);

    /* CORRECCION S17: el recorrido con try/catch, que estaba copiado en los 15 métodos de carga, queda en
    un solo lugar. Si una línea falla se anota con su archivo y número de línea y se sigue (P2/P3). */
    protected void cargarCada(String archivo, Consumer<Registro> accion) {
        for (Registro r : leer(archivo)) {
            try {
                accion.accept(r);
            } catch (RuntimeException e) {
                erroresDeCarga.add(r.ubicacion() + ": " + e.getMessage());
            }
        }
    }

    private List<Registro> leer(String archivo) {
        List<Registro> registros = new ArrayList<>();
        Path ruta = carpeta.resolve(archivo);
        if (!Files.exists(ruta)) return registros;
        List<String> lineas;
        try {
            lineas = ArchivoTexto.leerLineas(ruta);
        } catch (IOException e) {
            erroresDeCarga.add(archivo + ": no se pudo leer el archivo (" + e.getMessage() + ").");
            return registros;
        }
        for (int i = 0; i < lineas.size(); i++) {
            if (lineas.get(i).isBlank()) continue;
            try {
                registros.add(new Registro(archivo, i + 1, lineas.get(i)));
            } catch (RuntimeException e) {
                erroresDeCarga.add(archivo + ", línea " + (i + 1) + ": " + e.getMessage());
            }
        }
        return registros;
    }
}

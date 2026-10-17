package persistencia;

import excepciones.PersistenciaException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

// Único lugar que abre archivos y maneja IOException
final class ArchivoTexto {
    private final Path ruta;
    private final String entidad;

    ArchivoTexto(String nombreArchivo, String entidad) {
        this.ruta = Path.of(nombreArchivo);
        this.entidad = entidad;
    }

    List<Registro> leerRegistros() throws PersistenciaException {
        List<Registro> registros = new ArrayList<>();
        if (!Files.exists(ruta)) return registros;
        try (BufferedReader lector = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            String linea;
            int numeroLinea = 0;
            while ((linea = lector.readLine()) != null) {
                numeroLinea++;
                if (!linea.isBlank()) registros.add(new Registro(entidad, numeroLinea, linea));
            }
        } catch (IOException ex) {
            throw new PersistenciaException("Error al cargar " + entidad + ": " + ex.getMessage());
        }
        return registros;
    }

    // Escribe en un archivo temporal y después lo reemplaza: si algo falla a mitad de camino,
    // el archivo anterior queda intacto
    void escribirRegistros(List<List<String>> registros) throws PersistenciaException {
        Path temporal = ruta.resolveSibling(ruta.getFileName() + ".tmp");
        try {
            try (BufferedWriter escritor = Files.newBufferedWriter(temporal, StandardCharsets.UTF_8)) {
                for (List<String> campos : registros) {
                    escritor.write(FormatoCsv.unir(campos));
                    escritor.newLine();
                }
            }
            reemplazar(temporal, ruta);
        } catch (IOException ex) {
            throw new PersistenciaException("Error al guardar " + entidad + ": " + ex.getMessage());
        }
    }

    private static void reemplazar(Path origen, Path destino) throws IOException {
        try {
            Files.move(origen, destino, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException ex) {
            Files.move(origen, destino, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}

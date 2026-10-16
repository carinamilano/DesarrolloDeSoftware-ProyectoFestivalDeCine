package persistencia;

import excepciones.PersistenciaException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

// Único lugar que abre archivos y maneja IOException (antes el try/catch estaba copiado 14 veces)
class ArchivoTexto {
    private static final String SEPARADOR = ",";

    private final Path ruta;
    private final String entidad;

    ArchivoTexto(String nombreArchivo, String entidad) {
        this.ruta = Path.of(nombreArchivo);
        this.entidad = entidad;
    }

    List<String[]> leerRegistros() throws PersistenciaException {
        List<String[]> registros = new ArrayList<>();
        if (!Files.exists(ruta)) return registros;
        try (BufferedReader lector = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (!linea.isBlank()) registros.add(linea.split(SEPARADOR));
            }
        } catch (IOException ex) {
            throw new PersistenciaException("Error al cargar " + entidad + ".");
        }
        return registros;
    }

    void escribirRegistros(List<List<String>> registros) throws PersistenciaException {
        try (BufferedWriter escritor = Files.newBufferedWriter(ruta, StandardCharsets.UTF_8)) {
            for (List<String> campos : registros) {
                escritor.write(String.join(SEPARADOR, campos));
                escritor.newLine();
            }
        } catch (IOException ex) {
            throw new PersistenciaException("Error al guardar " + entidad + ".");
        }
    }

    String getEntidad() {
        return entidad;
    }
}

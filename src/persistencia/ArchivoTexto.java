package persistencia;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

// CORRECCION P12: único lugar que lee y escribe archivos. Antes cada uno de los 14 métodos lo repetía.
final class ArchivoTexto {
    private static final String MARCA_UTF8 = "﻿";

    private ArchivoTexto() {
    }

    // CORRECCION P11: el charset se declara siempre (UTF-8), no depende de la configuración de la máquina.
    static List<String> leerLineas(Path ruta) throws IOException {
        List<String> lineas = Files.readAllLines(ruta, StandardCharsets.UTF_8);
        if (!lineas.isEmpty() && lineas.get(0).startsWith(MARCA_UTF8)) {
            lineas.set(0, lineas.get(0).substring(1));
        }
        return lineas;
    }

    /* CORRECCION P8: se escribe primero en un archivo .tmp y después se reemplaza el original.
     Si algo falla a mitad de camino, el archivo anterior queda intacto (antes quedaba cortado).*/
    static void escribirLineas(Path ruta, List<String> lineas) throws IOException {
        Files.createDirectories(ruta.toAbsolutePath().getParent());
        Path temporal = ruta.resolveSibling(ruta.getFileName() + ".tmp");
        Files.write(temporal, lineas, StandardCharsets.UTF_8);
        try {
            Files.move(temporal, ruta, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporal, ruta, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}

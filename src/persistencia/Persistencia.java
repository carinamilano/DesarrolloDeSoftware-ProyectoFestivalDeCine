package persistencia;

import excepciones.PersistenciaException;
import servicios.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* CORRECCION S15: antes esta clase cargaba y guardaba los 15 archivos ella sola (429 líneas, 36 métodos).
Ahora coordina: cada dominio (personas, películas, festival, ventas y evaluaciones) se encarga de sus archivos
y esta clase los recorre en orden, maneja la carpeta y junta los errores. */
public class Persistencia {

    // CORRECCION P11: los archivos van en una carpeta fija ("datos") y no sueltos en la carpeta de trabajo.
    public static final String CARPETA_DATOS = "datos";

    private final Path carpeta;
    private final List<String> erroresDeCarga;
    /* CORRECCION P5: se guardan los 14 tipos de datos. Antes solo se guardaban los 7 primeros
    y lo que se cargaba de ediciones, funciones, entradas, abonos y votos se perdía al cerrar.
    Las referencias van por clave: email para personas, título para películas y código para
    funciones, entradas y abonos (CORRECCION P6: antes director y jurado iban por nombre).
    El orden importa: cada dominio usa datos de los anteriores (las películas necesitan a sus directores,
    las funciones a sus secciones y los votos a las entradas). */
    private final List<ArchivosDeDominio> dominios;

    public Persistencia(String carpeta, PersonaServicio personaServicio, PeliculaServicio peliculaServicio, FestivalServicio festivalServicio, FuncionServicio funcionServicio, VentaServicio ventaServicio, AbonoServicio abonoServicio, EvaluacionServicio evaluacionServicio) {
        this.carpeta = Paths.get(carpeta);
        this.erroresDeCarga = new ArrayList<>();
        this.dominios = List.of(
                new PersistenciaPersonas(this.carpeta, erroresDeCarga, personaServicio),
                new PersistenciaPeliculas(this.carpeta, erroresDeCarga, peliculaServicio, personaServicio),
                new PersistenciaFestival(this.carpeta, erroresDeCarga, festivalServicio, peliculaServicio),
                new PersistenciaVentas(this.carpeta, erroresDeCarga, funcionServicio, ventaServicio, abonoServicio,
                        personaServicio, peliculaServicio, festivalServicio),
                new PersistenciaEvaluaciones(this.carpeta, erroresDeCarga, evaluacionServicio, personaServicio,
                        peliculaServicio));
    }

    public Path getCarpeta() {
        return carpeta.toAbsolutePath();
    }

    /* CORRECCION P2/P3: una línea con errores (o que apunta a un dato que no existe) se anota con su
     archivo y número de línea y se sigue con la siguiente. Antes una línea mal escrita impedía abrir
     la aplicación y una película sin director se descartaba sin avisar.
     Los datos se crean con los mismos métodos que usa la aplicación, así se vuelven a validar las reglas.*/
    public void cargar() throws PersistenciaException {
        erroresDeCarga.clear();
        for (ArchivosDeDominio dominio : dominios) {
            dominio.cargar();
        }
        if (!erroresDeCarga.isEmpty()) {
            throw new PersistenciaException("Se encontraron " + erroresDeCarga.size()
                    + " errores al cargar los datos. Esas líneas no se cargaron.", erroresDeCarga);
        }
    }

    // CORRECCION P3: la ventana lo consulta para avisar, antes de guardar, que las líneas con errores se van a perder.
    public boolean huboErroresDeCarga() {
        return !erroresDeCarga.isEmpty();
    }

    /* CORRECCION P9: arma el texto de todos los archivos. Se llama en el hilo de la interfaz y el hilo de
    guardado solo escribe estas líneas; antes el hilo recorría las listas mientras la UI las podía cambiar.*/
    public Map<String, List<String>> prepararGuardado() {
        Map<String, List<String>> archivos = new LinkedHashMap<>();
        for (ArchivosDeDominio dominio : dominios) {
            dominio.agregarLineas(archivos);
        }
        return archivos;
    }

    /* CORRECCION P8/P10: cada archivo se escribe de forma segura (ver ArchivoTexto) y, si alguno falla,
    se informa cuál y por qué, sin cortar el guardado de los demás.*/
    public void escribir(Map<String, List<String>> archivos) throws PersistenciaException {
        List<String> errores = new ArrayList<>();
        for (Map.Entry<String, List<String>> archivo : archivos.entrySet()) {
            try {
                ArchivoTexto.escribirLineas(carpeta.resolve(archivo.getKey()), archivo.getValue());
            } catch (IOException e) {
                errores.add(archivo.getKey() + ": " + e.getMessage());
            }
        }
        if (!errores.isEmpty()) {
            throw new PersistenciaException("No se pudieron guardar " + errores.size() + " archivos.", errores);
        }
    }
}

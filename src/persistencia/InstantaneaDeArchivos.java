package persistencia;

import excepciones.PersistenciaException;
import modelo.abonos.Abono;
import modelo.festival.Edicion;
import modelo.festival.Seccion;
import modelo.funciones.Butaca;
import modelo.funciones.Entrada;
import modelo.peliculas.Pelicula;
import modelo.personas.Persona;
import modelo.precios.Dinero;
import repositorio.Catalogo;
import repositorio.Repositorio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static persistencia.ArchivoDatos.*;

// Copia, como listas de textos, de todo lo que hay que guardar. Se arma en el hilo de la UI (que es
// el único que modifica el modelo) y después se escribe en el hilo de guardado. Antes el hilo de
// guardado recorría las listas del modelo mientras la UI las podía estar modificando
final class InstantaneaDeArchivos implements Persistencia.Guardado {
    private final Map<ArchivoDatos, List<List<String>>> contenido = new EnumMap<>(ArchivoDatos.class);

    private InstantaneaDeArchivos() {
    }

    static InstantaneaDeArchivos tomar(Catalogo catalogo) {
        InstantaneaDeArchivos instantanea = new InstantaneaDeArchivos();
        instantanea.tomarSalasYPersonas(catalogo);
        instantanea.tomarPeliculas(catalogo.getPeliculas().listar());
        instantanea.tomarEdiciones(catalogo.getFestival().getEdiciones());
        instantanea.tomarFuncionesYVentas(catalogo);
        return instantanea;
    }

    private void tomarSalasYPersonas(Catalogo catalogo) {
        contenido.put(SALAS, catalogo.getSalas().listar().stream()
                .map(sala -> List.of(sala.getNombre(), String.valueOf(sala.getCantidadButacas())))
                .toList());
        contenido.put(DIRECTORES, personas(catalogo.getDirectores()));
        contenido.put(ACTORES, personas(catalogo.getActores()));
        contenido.put(JURADOS, personas(catalogo.getJurados()));
        contenido.put(ESPECTADORES, personas(catalogo.getEspectadores()));
    }

    private static List<List<String>> personas(Repositorio<? extends Persona> personas) {
        return personas.listar().stream()
                .map(persona -> List.of(persona.getNombre(), persona.getEmail(), persona.getNacionalidad()))
                .toList();
    }

    private void tomarPeliculas(List<Pelicula> peliculas) {
        contenido.put(PELICULAS, peliculas.stream()
                .map(pelicula -> List.of(pelicula.getTitulo(), pelicula.getGenero().getDescripcion(),
                        String.valueOf(pelicula.getDuracion()), pelicula.getDirector().getEmail()))
                .toList());
        contenido.put(REPARTO, peliculas.stream()
                .flatMap(pelicula -> pelicula.getActores().stream()
                        .map(actor -> List.of(pelicula.getTitulo(), actor.getEmail())))
                .toList());
        contenido.put(EVALUACIONES, peliculas.stream()
                .flatMap(pelicula -> pelicula.getEvaluaciones().stream())
                .map(evaluacion -> List.of(evaluacion.getJurado().getEmail(), evaluacion.getTituloPelicula(),
                        String.valueOf(evaluacion.getPuntaje())))
                .toList());
        contenido.put(VOTOS, peliculas.stream()
                .flatMap(pelicula -> pelicula.getVotosPublico().stream())
                .map(voto -> List.of(voto.getEspectador().getEmail(), voto.getPelicula().getTitulo(),
                        String.valueOf(voto.getPuntaje())))
                .toList());
    }

    private void tomarEdiciones(List<Edicion> ediciones) {
        List<List<String>> filasEdiciones = new ArrayList<>();
        List<List<String>> filasSecciones = new ArrayList<>();
        List<List<String>> filasPeliculas = new ArrayList<>();
        for (Edicion edicion : ediciones) {
            String anio = String.valueOf(edicion.getAnio());
            filasEdiciones.add(List.of(anio, edicion.getCiudad(),
                    edicion.getFechaInicio().toString(), edicion.getFechaFin().toString()));
            for (Seccion seccion : edicion.getSecciones()) {
                String tipo = seccion.getTipo().name();
                filasSecciones.add(List.of(anio, tipo, seccion.getPremio().getNombre(), seccion.getPremio().getDescripcion()));
                seccion.getPeliculas().forEach(pelicula -> filasPeliculas.add(List.of(anio, tipo, pelicula.getTitulo())));
            }
        }
        contenido.put(EDICIONES, filasEdiciones);
        contenido.put(SECCIONES, filasSecciones);
        contenido.put(PELICULAS_POR_SECCION, filasPeliculas);
    }

    private void tomarFuncionesYVentas(Catalogo catalogo) {
        contenido.put(FUNCIONES, Stream.concat(
                catalogo.getFuncionesPresenciales().listar().stream()
                        .map(funcion -> List.of(funcion.getCodigo(), PRESENCIAL, funcion.getTituloPelicula(),
                                funcion.getFechaHora().toString(), funcion.getSala().getNombre())),
                catalogo.getFuncionesStreaming().listar().stream()
                        .map(funcion -> List.of(funcion.getCodigo(), STREAMING, funcion.getTituloPelicula(),
                                funcion.getFechaHora().toString(), funcion.getEnlace(),
                                String.valueOf(funcion.getCapacidadVirtual()))))
                .toList());
        contenido.put(ABONOS, catalogo.getAbonos().listar().stream().map(InstantaneaDeArchivos::abono).toList());
        contenido.put(ENTRADAS, catalogo.getEntradas().listar().stream().map(InstantaneaDeArchivos::entrada).toList());
    }

    private static List<String> abono(Abono abono) {
        List<String> campos = new ArrayList<>(List.of(abono.getCodigo(), abono.getTipo().name(),
                abono.getEspectador().getEmail(), abono.getTarifa().name(), monto(abono.getPrecio())));
        if (!abono.getDiasElegidos().isEmpty()) {
            campos.add(abono.getDiasElegidos().stream().map(LocalDate::toString)
                    .collect(Collectors.joining(Registro.SEPARADOR_LISTA)));
        }
        return campos;
    }

    private static List<String> entrada(Entrada entrada) {
        return List.of(entrada.getCodigo(), entrada.getFuncion().getCodigo(), entrada.getEspectador().getEmail(),
                monto(entrada.getPrecio()),
                entrada.getButacaAsignada().map(Butaca::toString).orElse(""),
                entrada.getAbono().map(Abono::getCodigo).orElse(""));
    }

    private static String monto(Dinero dinero) {
        return dinero.getMonto().toPlainString();
    }

    // Escribe todos los archivos; si alguno falla sigue con los demás e informa todos los errores juntos
    @Override
    public void escribir() throws PersistenciaException {
        List<String> errores = new ArrayList<>();
        for (Map.Entry<ArchivoDatos, List<List<String>>> archivo : contenido.entrySet()) {
            try {
                archivo.getKey().archivo().escribirRegistros(archivo.getValue());
            } catch (PersistenciaException ex) {
                errores.add(ex.getMessage());
            }
        }
        if (!errores.isEmpty()) {
            throw new PersistenciaException("No se pudieron guardar todos los datos: " + String.join(" ", errores), errores);
        }
    }
}

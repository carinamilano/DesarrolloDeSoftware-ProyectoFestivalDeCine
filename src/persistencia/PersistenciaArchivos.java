package persistencia;

import excepciones.PersistenciaException;
import modelo.*;
import repositorio.Catalogo;
import repositorio.Repositorio;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

// Persiste en archivos de texto: salas, directores, actores, jurados, espectadores, películas y evaluaciones
public class PersistenciaArchivos implements Persistencia {

    // Posición de cada campo dentro de una línea (antes eran índices mágicos partes[n])
    private static final int PERSONA_NOMBRE = 0, PERSONA_EMAIL = 1, PERSONA_NACIONALIDAD = 2;
    private static final int SALA_NOMBRE = 0, SALA_CAPACIDAD = 1;
    private static final int PELICULA_TITULO = 0, PELICULA_GENERO = 1, PELICULA_DURACION = 2, PELICULA_DIRECTOR = 3;
    private static final int EVALUACION_JURADO = 0, EVALUACION_PELICULA = 1, EVALUACION_PUNTAJE = 2;

    private final Catalogo catalogo;
    private final ArchivoTexto archivoSalas = new ArchivoTexto("salas.txt", "salas");
    private final ArchivoTexto archivoDirectores = new ArchivoTexto("directores.txt", "directores");
    private final ArchivoTexto archivoActores = new ArchivoTexto("actores.txt", "actores");
    private final ArchivoTexto archivoJurados = new ArchivoTexto("jurados.txt", "jurados");
    private final ArchivoTexto archivoEspectadores = new ArchivoTexto("espectadores.txt", "espectadores");
    private final ArchivoTexto archivoPeliculas = new ArchivoTexto("peliculas.txt", "peliculas");
    private final ArchivoTexto archivoEvaluaciones = new ArchivoTexto("evaluaciones.txt", "evaluaciones");

    public PersistenciaArchivos(Catalogo catalogo) {
        this.catalogo = catalogo;
    }

    @FunctionalInterface
    private interface FabricaPersona<T extends Persona> {
        T crear(String nombre, String email, String nacionalidad);
    }

    // ========== CARGAR ==========

    @Override
    public void cargar() throws PersistenciaException {
        cargarSalas();
        cargarPersonas(archivoDirectores, catalogo.getDirectores(), Director::new);
        cargarPersonas(archivoActores, catalogo.getActores(), Actor::new);
        cargarPersonas(archivoJurados, catalogo.getJurados(), Jurado::new);
        cargarPersonas(archivoEspectadores, catalogo.getEspectadores(), Espectador::new);
        cargarPeliculas();
        cargarEvaluaciones();
    }

    private void cargarSalas() throws PersistenciaException {
        for (String[] campos : archivoSalas.leerRegistros()) {
            Sala sala = convertir(archivoSalas, campos, () ->
                    new Sala(campos[SALA_NOMBRE], Integer.parseInt(campos[SALA_CAPACIDAD])));
            catalogo.getSalas().agregar(sala);
        }
    }

    // Un solo método para las cuatro clases de Persona (antes eran cuatro métodos clonados)
    private <T extends Persona> void cargarPersonas(ArchivoTexto archivo, Repositorio<T> destino,
                                                    FabricaPersona<T> fabrica) throws PersistenciaException {
        for (String[] campos : archivo.leerRegistros()) {
            T persona = convertir(archivo, campos, () ->
                    fabrica.crear(campos[PERSONA_NOMBRE], campos[PERSONA_EMAIL], campos[PERSONA_NACIONALIDAD]));
            destino.agregar(persona);
        }
    }

    private void cargarPeliculas() throws PersistenciaException {
        for (String[] campos : archivoPeliculas.leerRegistros()) {
            Optional<Director> director = buscarPersona(catalogo.getDirectores(), campos[PELICULA_DIRECTOR]);
            if (director.isEmpty()) continue;
            Pelicula pelicula = convertir(archivoPeliculas, campos, () -> new Pelicula(
                    campos[PELICULA_TITULO],
                    Genero.desde(campos[PELICULA_GENERO]),
                    Integer.parseInt(campos[PELICULA_DURACION]),
                    director.get()));
            catalogo.getPeliculas().agregar(pelicula);
        }
    }

    private void cargarEvaluaciones() throws PersistenciaException {
        for (String[] campos : archivoEvaluaciones.leerRegistros()) {
            Optional<Jurado> jurado = buscarPersona(catalogo.getJurados(), campos[EVALUACION_JURADO]);
            Optional<Pelicula> pelicula = catalogo.getPeliculas().buscar(p -> p.seTitula(campos[EVALUACION_PELICULA]));
            if (jurado.isEmpty() || pelicula.isEmpty()) continue;
            convertir(archivoEvaluaciones, campos, () ->
                    pelicula.get().recibirEvaluacion(jurado.get(), Double.parseDouble(campos[EVALUACION_PUNTAJE])));
        }
    }

    private <T extends Persona> Optional<T> buscarPersona(Repositorio<T> repositorio, String nombre) {
        return repositorio.buscar(persona -> persona.seLlama(nombre));
    }

    // Una línea mal formada se informa como PersistenciaException en vez de cerrar el programa
    private <T> T convertir(ArchivoTexto archivo, String[] campos, Supplier<T> conversion) throws PersistenciaException {
        try {
            return conversion.get();
        } catch (RuntimeException ex) {
            throw new PersistenciaException("Registro inválido en " + archivo.getEntidad() + ": "
                    + String.join(",", campos));
        }
    }

    // ========== GUARDAR ==========

    @Override
    public void guardar() throws PersistenciaException {
        archivoSalas.escribirRegistros(catalogo.getSalas().listar().stream()
                .map(sala -> List.of(sala.getNombre(), String.valueOf(sala.getCantidadButacas())))
                .toList());
        guardarPersonas(archivoDirectores, catalogo.getDirectores());
        guardarPersonas(archivoActores, catalogo.getActores());
        guardarPersonas(archivoJurados, catalogo.getJurados());
        guardarPersonas(archivoEspectadores, catalogo.getEspectadores());
        archivoPeliculas.escribirRegistros(catalogo.getPeliculas().listar().stream()
                .map(pelicula -> List.of(pelicula.getTitulo(), pelicula.getGenero().getDescripcion(),
                        String.valueOf(pelicula.getDuracion()), pelicula.getNombreDirector()))
                .toList());
        archivoEvaluaciones.escribirRegistros(catalogo.getPeliculas().listar().stream()
                .flatMap(pelicula -> pelicula.getEvaluaciones().stream())
                .map(evaluacion -> List.of(evaluacion.getNombreJurado(), evaluacion.getTituloPelicula(),
                        String.valueOf(evaluacion.getPuntaje())))
                .toList());
    }

    private void guardarPersonas(ArchivoTexto archivo, Repositorio<? extends Persona> personas) throws PersistenciaException {
        archivo.escribirRegistros(personas.listar().stream()
                .map(persona -> List.of(persona.getNombre(), persona.getEmail(), persona.getNacionalidad()))
                .toList());
    }
}

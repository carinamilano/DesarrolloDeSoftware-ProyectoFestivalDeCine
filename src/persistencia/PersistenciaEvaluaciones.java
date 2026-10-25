package persistencia;

import modelo.Evaluacion;
import modelo.VotoPublico;
import servicios.EvaluacionServicio;
import servicios.PeliculaServicio;
import servicios.PersonaServicio;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// CORRECCION S17: evaluaciones del jurado y votos del público (antes en Persistencia).
class PersistenciaEvaluaciones extends ArchivosDeDominio {

    private static final String EVALUACIONES = "evaluaciones.txt";
    private static final String VOTOS = "votos.txt";
    private final EvaluacionServicio evaluacionServicio;
    private final PersonaServicio personaServicio;
    private final PeliculaServicio peliculaServicio;

    PersistenciaEvaluaciones(Path carpeta, List<String> erroresDeCarga, EvaluacionServicio evaluacionServicio,
                             PersonaServicio personaServicio, PeliculaServicio peliculaServicio) {
        super(carpeta, erroresDeCarga);
        this.evaluacionServicio = evaluacionServicio;
        this.personaServicio = personaServicio;
        this.peliculaServicio = peliculaServicio;
    }

    /* CORRECCION I3/I9/P13: las evaluaciones pasan por la misma validación que en la aplicación,
    así se rechazan las repetidas y los puntajes fuera de rango.
    Los votos se cargan después de las entradas: para votar, el espectador tiene que tener entrada. */
    @Override
    void cargar() {
        cargarCada(EVALUACIONES, r -> evaluacionServicio.registrarEvaluacion(personaServicio.buscarJurado(r.texto(0)),
                peliculaServicio.buscarPelicula(r.texto(1)), r.decimal(2)));
        cargarCada(VOTOS, r -> evaluacionServicio.registrarVotoPublico(personaServicio.buscarEspectador(r.texto(0)),
                peliculaServicio.buscarPelicula(r.texto(1)), r.entero(2)));
    }

    @Override
    void agregarLineas(Map<String, List<String>> archivos) {
        archivos.put(EVALUACIONES, lineasEvaluaciones());
        archivos.put(VOTOS, lineasVotos());
    }

    private List<String> lineasEvaluaciones() {
        List<String> lineas = new ArrayList<>();
        for (Evaluacion e : evaluacionServicio.getEvaluaciones()) {
            lineas.add(FormatoLinea.unir(e.getJurado().getEmail(), e.getPelicula().getTitulo(), e.getPuntaje()));
        }
        return lineas;
    }

    private List<String> lineasVotos() {
        List<String> lineas = new ArrayList<>();
        for (VotoPublico v : evaluacionServicio.getVotosPublico()) {
            lineas.add(FormatoLinea.unir(v.getEspectador().getEmail(), v.getPelicula().getTitulo(), v.getPuntaje()));
        }
        return lineas;
    }
}

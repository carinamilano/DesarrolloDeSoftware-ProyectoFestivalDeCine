package servicios;

import modelo.comun.Validar;
import modelo.festival.Edicion;
import modelo.peliculas.Evaluacion;
import modelo.peliculas.Pelicula;
import modelo.peliculas.VotoPublico;
import modelo.personas.Espectador;
import modelo.personas.Jurado;

import java.util.List;

// Evaluaciones del jurado y voto del público. Las reglas (rangos de puntaje, una sola vez,
// tener entrada) viven en Pelicula, Evaluacion y VotoPublico; acá solo se coordina
public class EvaluacionServicio {
    private final PeliculaServicio peliculas;

    public EvaluacionServicio(PeliculaServicio peliculas) {
        this.peliculas = peliculas;
    }

    public Evaluacion registrarEvaluacion(Jurado jurado, Pelicula pelicula, double puntaje) {
        return Validar.presente(pelicula, "La película es obligatoria.").recibirEvaluacion(jurado, puntaje);
    }

    public VotoPublico registrarVotoPublico(Espectador espectador, Pelicula pelicula, int puntaje) {
        return Validar.presente(pelicula, "La película es obligatoria.").recibirVoto(espectador, puntaje);
    }

    // Películas de la edición que el espectador vio (tiene entrada) y todavía no votó
    public List<Pelicula> peliculasParaVotar(Espectador espectador, Edicion edicion) {
        return espectador.getPeliculasVistas().stream()
                .filter(edicion::incluye)
                .filter(pelicula -> !pelicula.fueVotadaPor(espectador))
                .toList();
    }

    public List<Evaluacion> getEvaluaciones() {
        return peliculas.getPeliculas().stream().flatMap(pelicula -> pelicula.getEvaluaciones().stream()).toList();
    }

    public List<VotoPublico> getVotosPublico() {
        return peliculas.getPeliculas().stream().flatMap(pelicula -> pelicula.getVotosPublico().stream()).toList();
    }
}

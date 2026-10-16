package servicios;

import modelo.*;

import java.util.List;

// Evaluaciones del jurado y voto del público. Las reglas (rangos de puntaje, una sola vez,
// tener entrada) viven en Pelicula, Evaluacion y VotoPublico; acá solo se coordina
public class EvaluacionServicio {
    private final PersonaServicio personas;
    private final PeliculaServicio peliculas;

    public EvaluacionServicio(PersonaServicio personas, PeliculaServicio peliculas) {
        this.personas = personas;
        this.peliculas = peliculas;
    }

    public Evaluacion registrarEvaluacion(String nombreJurado, String tituloPelicula, double puntaje) {
        Jurado jurado = personas.buscarJurado(nombreJurado);
        return peliculas.buscarPelicula(tituloPelicula).recibirEvaluacion(jurado, puntaje);
    }

    public VotoPublico registrarVotoPublico(String nombreEspectador, String tituloPelicula, int puntaje) {
        Espectador espectador = personas.buscarEspectador(nombreEspectador);
        return peliculas.buscarPelicula(tituloPelicula).recibirVoto(espectador, puntaje);
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

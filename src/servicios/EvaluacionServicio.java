package servicios;

import modelo.*;

import java.util.ArrayList;
import java.util.List;

// CORRECCION S2: evaluaciones del jurado y votos del público tienen su propio servicio
public class EvaluacionServicio {
    private PeliculaServicio peliculaServicio;

    public EvaluacionServicio(PeliculaServicio peliculaServicio) {
        this.peliculaServicio = peliculaServicio;
    }

    // CORRECCION I3/I9: Pelicula controla que el jurado no la haya evaluado y Evaluacion valida el puntaje.
    // CORRECCION I10: recibe el Jurado elegido, no su nombre.
    public Evaluacion registrarEvaluacion(Jurado jurado, Pelicula pelicula, double puntaje) {
        return pelicula.registrarEvaluacion(jurado, puntaje);
    }

    public VotoPublico registrarVotoPublico(Espectador espectador, Pelicula pelicula, int puntaje) {
        return pelicula.registrarVoto(espectador, puntaje);
    }

    // CORRECCION S7: antes este filtro estaba en PanelVotoPublico.
    public List<Pelicula> peliculasParaVotar(Espectador espectador, Edicion edicion) {
        List<Pelicula> paraVotar = new ArrayList<>();
        for (Entrada entrada : espectador.getEntradas()) {
            Pelicula p = entrada.getFuncion().getPelicula();
            if (edicion.incluye(p) && !paraVotar.contains(p) && !p.getCalificaciones().fueVotadaPor(espectador)) {
                paraVotar.add(p);
            }
        }
        return paraVotar;
    }

    public List<Evaluacion> getEvaluaciones() {
        List<Evaluacion> evaluaciones = new ArrayList<>();
        for (Pelicula p : peliculaServicio.getPeliculas()) {
            evaluaciones.addAll(p.getCalificaciones().getEvaluaciones());
        }
        return evaluaciones;
    }

    public List<VotoPublico> getVotosPublico() {
        List<VotoPublico> votos = new ArrayList<>();
        for (Pelicula p : peliculaServicio.getPeliculas()) {
            votos.addAll(p.getCalificaciones().getVotosPublico());
        }
        return votos;
    }
}

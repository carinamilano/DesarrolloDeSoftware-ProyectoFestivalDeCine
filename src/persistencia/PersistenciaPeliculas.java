package persistencia;

import modelo.Actor;
import modelo.Genero;
import modelo.Pelicula;
import servicios.PeliculaServicio;
import servicios.PersonaServicio;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// CORRECCION S17: películas y su reparto (antes en Persistencia).
class PersistenciaPeliculas extends ArchivosDeDominio {

    private static final String PELICULAS = "peliculas.txt";
    private static final String REPARTO = "reparto.txt";
    private final PeliculaServicio peliculaServicio;
    private final PersonaServicio personaServicio;

    PersistenciaPeliculas(Path carpeta, List<String> erroresDeCarga, PeliculaServicio peliculaServicio,
                          PersonaServicio personaServicio) {
        super(carpeta, erroresDeCarga);
        this.peliculaServicio = peliculaServicio;
        this.personaServicio = personaServicio;
    }

    @Override
    void cargar() {
        cargarCada(PELICULAS, r -> peliculaServicio.registrarPelicula(r.texto(0), r.enumerado(1, Genero.class),
                r.entero(2), personaServicio.buscarDirector(r.texto(3))));
        cargarCada(REPARTO, r -> {
            Pelicula pelicula = peliculaServicio.buscarPelicula(r.texto(0));
            peliculaServicio.asociarActorAPelicula(personaServicio.buscarActor(r.texto(1)), pelicula);
        });
    }

    @Override
    void agregarLineas(Map<String, List<String>> archivos) {
        archivos.put(PELICULAS, lineasPeliculas());
        archivos.put(REPARTO, lineasReparto());
    }

    private List<String> lineasPeliculas() {
        List<String> lineas = new ArrayList<>();
        for (Pelicula p : peliculaServicio.getPeliculas()) {
            lineas.add(FormatoLinea.unir(p.getTitulo(), p.getGenero().name(), p.getDuracion(), p.getDirector().getEmail()));
        }
        return lineas;
    }

    private List<String> lineasReparto() {
        List<String> lineas = new ArrayList<>();
        for (Pelicula p : peliculaServicio.getPeliculas()) {
            for (Actor a : p.getActores()) {
                lineas.add(FormatoLinea.unir(p.getTitulo(), a.getEmail()));
            }
        }
        return lineas;
    }
}

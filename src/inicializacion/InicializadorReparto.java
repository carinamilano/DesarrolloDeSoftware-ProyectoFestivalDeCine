package inicializacion;

import modelo.peliculas.Pelicula;
import servicios.PeliculaServicio;

class InicializadorReparto implements Inicializador {
    private final PeliculaServicio peliculaServicio;
    private final PersonasDemo personas;

    InicializadorReparto(PeliculaServicio peliculaServicio, PersonasDemo personas) {
        this.peliculaServicio = peliculaServicio;
        this.personas = personas;
    }

    @Override
    public void inicializar() {
        if (peliculaServicio.getPeliculas().isEmpty()) return;
        // Si el reparto ya se cargó de los archivos no se vuelve a asociar
        if (peliculaServicio.getPeliculas().stream().anyMatch(pelicula -> !pelicula.getActores().isEmpty())) return;

        asociar("The Irishman", "Robert De Niro", "Al Pacino");
        asociar("Tar", "Cate Blanchett");
        asociar("Dune", "Timothee Chalamet");
        asociar("Barbie", "Margot Robbie", "Ryan Gosling");
        asociar("La La Land", "Emma Stone", "Ryan Gosling");
        asociar("Oppenheimer", "Joaquin Phoenix");
        asociar("Roma", "Meryl Streep");
        asociar("The Grand Budapest Hotel", "Leonardo DiCaprio");
    }

    private void asociar(String tituloPelicula, String... actores) {
        Pelicula pelicula = peliculaServicio.buscarPelicula(tituloPelicula);
        for (String actor : actores) {
            peliculaServicio.asociarActorAPelicula(personas.actor(actor), pelicula);
        }
    }
}

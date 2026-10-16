package inicializacion;

import servicios.PeliculaServicio;

class InicializadorReparto implements Inicializador {
    private final PeliculaServicio peliculaServicio;

    InicializadorReparto(PeliculaServicio peliculaServicio) {
        this.peliculaServicio = peliculaServicio;
    }

    @Override
    public void inicializar() {
        if (peliculaServicio.getPeliculas().isEmpty()) return;

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
        for (String actor : actores) {
            peliculaServicio.asociarActorAPelicula(actor, tituloPelicula);
        }
    }
}

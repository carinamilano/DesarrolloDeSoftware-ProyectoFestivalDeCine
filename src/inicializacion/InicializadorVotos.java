package inicializacion;

import servicios.EvaluacionServicio;
import servicios.PeliculaServicio;
import servicios.SistemaFestival;
import servicios.VentaServicio;

// 18 votos del público (cada espectador vota películas para las que tiene entrada)
class InicializadorVotos implements Inicializador {
    private final EvaluacionServicio evaluacionServicio;
    private final VentaServicio ventaServicio;
    private final PeliculaServicio peliculaServicio;
    private final PersonasDemo personas;

    InicializadorVotos(SistemaFestival sistema, PersonasDemo personas) {
        this.evaluacionServicio = sistema.getEvaluacionServicio();
        this.ventaServicio = sistema.getVentaServicio();
        this.peliculaServicio = sistema.getPeliculaServicio();
        this.personas = personas;
    }

    @Override
    public void inicializar() {
        if (!evaluacionServicio.getVotosPublico().isEmpty()) return;
        if (ventaServicio.getEntradas().isEmpty()) return;

        votar("Ana Lopez", "The Irishman", 4);
        votar("Carlos Garcia", "The Irishman", 5);
        votar("Maria Perez", "The Irishman", 4);
        votar("Diego Fernandez", "Roma", 5);
        votar("Sofia Gonzalez", "Roma", 5);
        votar("Pablo Sanchez", "Roma", 4);
        votar("Valentina Torres", "Tar", 3);
        votar("Matias Ramirez", "Tar", 4);
        votar("Maria Perez", "Oppenheimer", 5);
        votar("Juan Rodriguez", "Oppenheimer", 4);
        votar("Laura Martinez", "Oppenheimer", 5);
        votar("Valentina Torres", "Parasite", 5);
        votar("Carlos Garcia", "Parasite", 4);
        votar("Diego Fernandez", "Volver", 3);
        votar("Ana Lopez", "Barbie", 4);
        votar("Carlos Garcia", "Barbie", 3);
        votar("Diego Fernandez", "Dune", 5);
        votar("Sofia Gonzalez", "Dune", 4);
    }

    private void votar(String espectador, String titulo, int puntaje) {
        evaluacionServicio.registrarVotoPublico(personas.espectador(espectador), peliculaServicio.buscarPelicula(titulo), puntaje);
    }
}

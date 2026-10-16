package inicializacion;

import servicios.EvaluacionServicio;
import servicios.VentaServicio;

// 18 votos del público (cada espectador vota películas para las que tiene entrada)
class InicializadorVotos implements Inicializador {
    private final EvaluacionServicio evaluacionServicio;
    private final VentaServicio ventaServicio;

    InicializadorVotos(EvaluacionServicio evaluacionServicio, VentaServicio ventaServicio) {
        this.evaluacionServicio = evaluacionServicio;
        this.ventaServicio = ventaServicio;
    }

    @Override
    public void inicializar() {
        if (!evaluacionServicio.getVotosPublico().isEmpty()) return;
        if (ventaServicio.getEntradas().isEmpty()) return;

        evaluacionServicio.registrarVotoPublico("Ana Lopez", "The Irishman", 4);
        evaluacionServicio.registrarVotoPublico("Carlos Garcia", "The Irishman", 5);
        evaluacionServicio.registrarVotoPublico("Maria Perez", "The Irishman", 4);
        evaluacionServicio.registrarVotoPublico("Diego Fernandez", "Roma", 5);
        evaluacionServicio.registrarVotoPublico("Sofia Gonzalez", "Roma", 5);
        evaluacionServicio.registrarVotoPublico("Pablo Sanchez", "Roma", 4);
        evaluacionServicio.registrarVotoPublico("Valentina Torres", "Tar", 3);
        evaluacionServicio.registrarVotoPublico("Matias Ramirez", "Tar", 4);
        evaluacionServicio.registrarVotoPublico("Maria Perez", "Oppenheimer", 5);
        evaluacionServicio.registrarVotoPublico("Juan Rodriguez", "Oppenheimer", 4);
        evaluacionServicio.registrarVotoPublico("Laura Martinez", "Oppenheimer", 5);
        evaluacionServicio.registrarVotoPublico("Valentina Torres", "Parasite", 5);
        evaluacionServicio.registrarVotoPublico("Carlos Garcia", "Parasite", 4);
        evaluacionServicio.registrarVotoPublico("Diego Fernandez", "Volver", 3);
        evaluacionServicio.registrarVotoPublico("Ana Lopez", "Barbie", 4);
        evaluacionServicio.registrarVotoPublico("Carlos Garcia", "Barbie", 3);
        evaluacionServicio.registrarVotoPublico("Diego Fernandez", "Dune", 5);
        evaluacionServicio.registrarVotoPublico("Sofia Gonzalez", "Dune", 4);
    }
}

package inicializacion;

import servicios.FestivalServicio;
import servicios.FuncionServicio;
import servicios.PeliculaServicio;
import servicios.SistemaFestival;

// 14 funciones presenciales y 6 streaming, a partir de 7 días desde hoy
class InicializadorFunciones implements Inicializador {
    private final FuncionServicio funcionServicio;
    private final PeliculaServicio peliculaServicio;
    private final FestivalServicio festivalServicio;
    private final CalendarioDemo calendario;

    InicializadorFunciones(SistemaFestival sistema, CalendarioDemo calendario) {
        this.funcionServicio = sistema.getFuncionServicio();
        this.peliculaServicio = sistema.getPeliculaServicio();
        this.festivalServicio = sistema.getFestivalServicio();
        this.calendario = calendario;
    }

    @Override
    public void inicializar() {
        if (!funcionServicio.getFunciones().isEmpty()) return;
        if (peliculaServicio.getPeliculas().isEmpty()) return;
        if (festivalServicio.getSalas().isEmpty()) return;

        presencial("The Irishman", "Sala 1", 7, 15);
        presencial("The Irishman", "Sala 2", 7, 20);
        presencial("Roma", "Sala 2", 8, 16);
        presencial("Roma", "Sala 3", 8, 21);
        presencial("Tar", "Sala 3", 9, 17);
        presencial("Tar", "Sala 4", 9, 21);
        presencial("Oppenheimer", "Sala 1", 10, 14);
        presencial("Oppenheimer", "Sala 5", 10, 19);
        presencial("Parasite", "Sala 2", 11, 16);
        presencial("Parasite", "Sala 4", 11, 20);
        presencial("Volver", "Sala 3", 12, 18);
        presencial("Volver", "Sala 5", 12, 21);
        presencial("The Grand Budapest Hotel", "Sala 1", 13, 17);
        presencial("The Grand Budapest Hotel", "Sala 4", 13, 20);

        streaming("Barbie", "https://stream.festival.com/barbie1", 500, 14, 18);
        streaming("Barbie", "https://stream.festival.com/barbie2", 500, 14, 21);
        streaming("Dune", "https://stream.festival.com/dune1", 300, 15, 17);
        streaming("Dune", "https://stream.festival.com/dune2", 300, 15, 21);
        streaming("La La Land", "https://stream.festival.com/lalaland1", 400, 16, 18);
        streaming("La La Land", "https://stream.festival.com/lalaland2", 400, 16, 21);
    }

    private void presencial(String titulo, String sala, int diasDesdeHoy, int hora) {
        funcionServicio.programarFuncionPresencial(peliculaServicio.buscarPelicula(titulo),
                festivalServicio.buscarSala(sala), calendario.momento(diasDesdeHoy, hora));
    }

    private void streaming(String titulo, String enlace, int capacidad, int diasDesdeHoy, int hora) {
        funcionServicio.programarFuncionStreaming(peliculaServicio.buscarPelicula(titulo),
                enlace, capacidad, calendario.momento(diasDesdeHoy, hora));
    }
}

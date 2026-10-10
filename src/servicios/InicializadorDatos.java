package servicios;

import modelo.*;

import java.time.LocalDate;
import java.time.LocalTime;

public class InicializadorDatos {
    private FestivalServicio festivalServicio;
    private EntradaServicio entradaServicio;
    private PeliculaServicio peliculaServicio;
    private LocalDate fechaBase;

    public InicializadorDatos(FestivalServicio festivalServicio, PeliculaServicio peliculaServicio, EntradaServicio entradaServicio) {
        this.festivalServicio = festivalServicio;
        this.peliculaServicio = peliculaServicio;
        this.entradaServicio = entradaServicio;
    }

    public void inicializar() {
        fechaBase = LocalDate.now();
        inicializarEdiciones();
        inicializarAsociaciones();
        inicializarReparto();
        inicializarFunciones();
        inicializarEntradas();
        inicializarAbonos();
        inicializarVotosPublico();
    }

    private void inicializarEdiciones() {
        if (!festivalServicio.getEdiciones().isEmpty()) return;

        festivalServicio.registrarEdicion(2024, "Madrid",
                LocalDate.of(2024, 5, 1), LocalDate.of(2024, 5, 15));
        festivalServicio.registrarSeccion(2024, TipoSeccion.COMPETENCIA_OFICIAL,
                new Premio("Palma de Oro 2024", "Premio a la mejor pelicula"));
        festivalServicio.registrarSeccion(2024, TipoSeccion.CINE_INDEPENDIENTE,
                new Premio("Premio Independiente 2024", "Premio al mejor cine independiente"));
        festivalServicio.registrarSeccion(2024, TipoSeccion.CORTOMETRAJE,
                new Premio("Premio Cortometraje 2024", "Premio al mejor cortometraje"));
        festivalServicio.registrarSeccion(2024, TipoSeccion.NUEVOS_TALENTOS,
                new Premio("Premio Revelacion 2024", "Premio al nuevo talento"));

        festivalServicio.registrarEdicion(2025, "Paris",
                LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 20));
        festivalServicio.registrarSeccion(2025, TipoSeccion.COMPETENCIA_OFICIAL,
                new Premio("Palma de Oro 2025", "Premio a la mejor pelicula"));
        festivalServicio.registrarSeccion(2025, TipoSeccion.CINE_INDEPENDIENTE,
                new Premio("Premio Independiente 2025", "Premio al mejor cine independiente"));
        festivalServicio.registrarSeccion(2025, TipoSeccion.CORTOMETRAJE,
                new Premio("Premio Cortometraje 2025", "Premio al mejor cortometraje"));
        festivalServicio.registrarSeccion(2025, TipoSeccion.NUEVOS_TALENTOS,
                new Premio("Premio Revelacion 2025", "Premio al nuevo talento"));

        festivalServicio.registrarEdicion(2026, "Buenos Aires",
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 31));
        festivalServicio.registrarSeccion(2026, TipoSeccion.COMPETENCIA_OFICIAL,
                new Premio("Palma de Oro 2026", "Premio a la mejor pelicula"));
        festivalServicio.registrarSeccion(2026, TipoSeccion.CINE_INDEPENDIENTE,
                new Premio("Premio Independiente 2026", "Premio al mejor cine independiente"));
        festivalServicio.registrarSeccion(2026, TipoSeccion.CORTOMETRAJE,
                new Premio("Premio Cortometraje 2026", "Premio al mejor cortometraje"));
        festivalServicio.registrarSeccion(2026, TipoSeccion.NUEVOS_TALENTOS,
                new Premio("Premio Revelacion 2026", "Premio al nuevo talento"));
    }

    private void inicializarAsociaciones() {
        if (peliculaServicio.getPeliculas().isEmpty()) return;

        Pelicula irishman = peliculaServicio.buscarPelicula("The Irishman");
        Pelicula roma = peliculaServicio.buscarPelicula("Roma");
        Pelicula tar = peliculaServicio.buscarPelicula("Tar");
        Pelicula oppenheimer = peliculaServicio.buscarPelicula("Oppenheimer");
        Pelicula parasite = peliculaServicio.buscarPelicula("Parasite");
        Pelicula barbie = peliculaServicio.buscarPelicula("Barbie");
        Pelicula dune = peliculaServicio.buscarPelicula("Dune");
        Pelicula laLaLand = peliculaServicio.buscarPelicula("La La Land");
        Pelicula volver = peliculaServicio.buscarPelicula("Volver");
        Pelicula grandBudapest = peliculaServicio.buscarPelicula("The Grand Budapest Hotel");

        // Edicion 2026
        festivalServicio.asociarPeliculaASeccion(2026, TipoSeccion.COMPETENCIA_OFICIAL, oppenheimer);
        festivalServicio.asociarPeliculaASeccion(2026, TipoSeccion.COMPETENCIA_OFICIAL, parasite);
        festivalServicio.asociarPeliculaASeccion(2026, TipoSeccion.COMPETENCIA_OFICIAL, tar);
        festivalServicio.asociarPeliculaASeccion(2026, TipoSeccion.COMPETENCIA_OFICIAL, roma);
        festivalServicio.asociarPeliculaASeccion(2026, TipoSeccion.CINE_INDEPENDIENTE, barbie);
        festivalServicio.asociarPeliculaASeccion(2026, TipoSeccion.CINE_INDEPENDIENTE, volver);
        festivalServicio.asociarPeliculaASeccion(2026, TipoSeccion.CINE_INDEPENDIENTE, grandBudapest);
        festivalServicio.asociarPeliculaASeccion(2026, TipoSeccion.CORTOMETRAJE, irishman);
        festivalServicio.asociarPeliculaASeccion(2026, TipoSeccion.CORTOMETRAJE, laLaLand);
        festivalServicio.asociarPeliculaASeccion(2026, TipoSeccion.NUEVOS_TALENTOS, dune);
        festivalServicio.asociarPeliculaASeccion(2026, TipoSeccion.NUEVOS_TALENTOS, barbie);

        // Edicion 2025
        festivalServicio.asociarPeliculaASeccion(2025, TipoSeccion.COMPETENCIA_OFICIAL, parasite);
        festivalServicio.asociarPeliculaASeccion(2025, TipoSeccion.COMPETENCIA_OFICIAL, oppenheimer);
        festivalServicio.asociarPeliculaASeccion(2025, TipoSeccion.CINE_INDEPENDIENTE, barbie);
        festivalServicio.asociarPeliculaASeccion(2025, TipoSeccion.CINE_INDEPENDIENTE, volver);
        festivalServicio.asociarPeliculaASeccion(2025, TipoSeccion.CORTOMETRAJE, tar);
        festivalServicio.asociarPeliculaASeccion(2025, TipoSeccion.CORTOMETRAJE, irishman);
        festivalServicio.asociarPeliculaASeccion(2025, TipoSeccion.NUEVOS_TALENTOS, dune);
        festivalServicio.asociarPeliculaASeccion(2025, TipoSeccion.NUEVOS_TALENTOS, laLaLand);

        // Edicion 2024
        festivalServicio.asociarPeliculaASeccion(2024, TipoSeccion.COMPETENCIA_OFICIAL, irishman);
        festivalServicio.asociarPeliculaASeccion(2024, TipoSeccion.COMPETENCIA_OFICIAL, roma);
        festivalServicio.asociarPeliculaASeccion(2024, TipoSeccion.CINE_INDEPENDIENTE, volver);
        festivalServicio.asociarPeliculaASeccion(2024, TipoSeccion.CINE_INDEPENDIENTE, grandBudapest);
        festivalServicio.asociarPeliculaASeccion(2024, TipoSeccion.CORTOMETRAJE, tar);
        festivalServicio.asociarPeliculaASeccion(2024, TipoSeccion.CORTOMETRAJE, laLaLand);
        festivalServicio.asociarPeliculaASeccion(2024, TipoSeccion.NUEVOS_TALENTOS, dune);
        festivalServicio.asociarPeliculaASeccion(2024, TipoSeccion.NUEVOS_TALENTOS, barbie);
    }
    private void inicializarReparto() {
        if (peliculaServicio.getPeliculas().isEmpty()) return;

        peliculaServicio.asociarActorAPelicula("Robert De Niro", "The Irishman");
        peliculaServicio.asociarActorAPelicula("Al Pacino", "The Irishman");
        peliculaServicio.asociarActorAPelicula("Cate Blanchett", "Tar");
        peliculaServicio.asociarActorAPelicula("Timothee Chalamet", "Dune");
        peliculaServicio.asociarActorAPelicula("Margot Robbie", "Barbie");
        peliculaServicio.asociarActorAPelicula("Ryan Gosling", "Barbie");
        peliculaServicio.asociarActorAPelicula("Emma Stone", "La La Land");
        peliculaServicio.asociarActorAPelicula("Ryan Gosling", "La La Land");
        peliculaServicio.asociarActorAPelicula("Joaquin Phoenix", "Oppenheimer");
        peliculaServicio.asociarActorAPelicula("Meryl Streep", "Roma");
        peliculaServicio.asociarActorAPelicula("Leonardo DiCaprio", "The Grand Budapest Hotel");
    }
    private void inicializarFunciones() {
        if (!entradaServicio.getFunciones().isEmpty()) return;
        if (peliculaServicio.getPeliculas().isEmpty()) return;
        if (festivalServicio.getSalas().isEmpty()) return;

        Pelicula irishman = peliculaServicio.buscarPelicula("The Irishman");
        Pelicula roma = peliculaServicio.buscarPelicula("Roma");
        Pelicula tar = peliculaServicio.buscarPelicula("Tar");
        Pelicula oppenheimer = peliculaServicio.buscarPelicula("Oppenheimer");
        Pelicula parasite = peliculaServicio.buscarPelicula("Parasite");
        Pelicula barbie = peliculaServicio.buscarPelicula("Barbie");
        Pelicula dune = peliculaServicio.buscarPelicula("Dune");
        Pelicula laLaLand = peliculaServicio.buscarPelicula("La La Land");
        Pelicula volver = peliculaServicio.buscarPelicula("Volver");
        Pelicula grandBudapest = peliculaServicio.buscarPelicula("The Grand Budapest Hotel");

        Sala sala1 = festivalServicio.buscarSala("Sala 1");
        Sala sala2 = festivalServicio.buscarSala("Sala 2");
        Sala sala3 = festivalServicio.buscarSala("Sala 3");
        Sala sala4 = festivalServicio.buscarSala("Sala 4");
        Sala sala5 = festivalServicio.buscarSala("Sala 5");

        entradaServicio.programarFuncionPresencial(irishman, sala1,
                fechaBase.plusDays(7), LocalTime.of(15, 0));
        entradaServicio.programarFuncionPresencial(irishman, sala2,
                fechaBase.plusDays(7), LocalTime.of(20, 0));
        entradaServicio.programarFuncionPresencial(roma, sala2,
                fechaBase.plusDays(8), LocalTime.of(16, 0));
        entradaServicio.programarFuncionPresencial(roma, sala3,
                fechaBase.plusDays(8), LocalTime.of(21, 0));
        entradaServicio.programarFuncionPresencial(tar, sala3,
                fechaBase.plusDays(9), LocalTime.of(17, 0));
        entradaServicio.programarFuncionPresencial(tar, sala4,
                fechaBase.plusDays(9), LocalTime.of(21, 0));
        entradaServicio.programarFuncionPresencial(oppenheimer, sala1,
                fechaBase.plusDays(10), LocalTime.of(14, 0));
        entradaServicio.programarFuncionPresencial(oppenheimer, sala5,
                fechaBase.plusDays(10), LocalTime.of(19, 0));
        entradaServicio.programarFuncionPresencial(parasite, sala2,
                fechaBase.plusDays(11), LocalTime.of(16, 0));
        entradaServicio.programarFuncionPresencial(parasite, sala4,
                fechaBase.plusDays(11), LocalTime.of(20, 0));
        entradaServicio.programarFuncionPresencial(volver, sala3,
                fechaBase.plusDays(12), LocalTime.of(18, 0));
        entradaServicio.programarFuncionPresencial(volver, sala5,
                fechaBase.plusDays(12), LocalTime.of(21, 0));
        entradaServicio.programarFuncionPresencial(grandBudapest, sala1,
                fechaBase.plusDays(13), LocalTime.of(17, 0));
        entradaServicio.programarFuncionPresencial(grandBudapest, sala4,
                fechaBase.plusDays(13), LocalTime.of(20, 0));

        entradaServicio.programarFuncionStreaming(barbie,
                "https://stream.festival.com/barbie1", 500,
                fechaBase.plusDays(14), LocalTime.of(18, 0));
        entradaServicio.programarFuncionStreaming(barbie,
                "https://stream.festival.com/barbie2", 500,
                fechaBase.plusDays(14), LocalTime.of(21, 0));
        entradaServicio.programarFuncionStreaming(dune,
                "https://stream.festival.com/dune1", 300,
                fechaBase.plusDays(15), LocalTime.of(17, 0));
        entradaServicio.programarFuncionStreaming(dune,
                "https://stream.festival.com/dune2", 300,
                fechaBase.plusDays(15), LocalTime.of(21, 0));
        entradaServicio.programarFuncionStreaming(laLaLand,
                "https://stream.festival.com/lalaland1", 400,
                fechaBase.plusDays(16), LocalTime.of(18, 0));
        entradaServicio.programarFuncionStreaming(laLaLand,
                "https://stream.festival.com/lalaland2", 400,
                fechaBase.plusDays(16), LocalTime.of(21, 0));
    }

    private void inicializarEntradas() {
        if (!entradaServicio.getEntradas().isEmpty()) return;
        if (entradaServicio.getFunciones().isEmpty()) return;
        if (entradaServicio.getEspectadores().isEmpty()) return;

        FuncionPresencial irishman15 = (FuncionPresencial) entradaServicio.buscarFuncion("The Irishman", fechaBase.plusDays(7));
        FuncionPresencial irishman20 = (FuncionPresencial) entradaServicio.buscarFuncion("The Irishman", fechaBase.plusDays(7));
        FuncionPresencial roma16 = (FuncionPresencial) entradaServicio.buscarFuncion("Roma", fechaBase.plusDays(8));
        FuncionPresencial tar17 = (FuncionPresencial) entradaServicio.buscarFuncion("Tar", fechaBase.plusDays(9));
        FuncionPresencial oppenheimer14 = (FuncionPresencial) entradaServicio.buscarFuncion("Oppenheimer", fechaBase.plusDays(10));
        FuncionPresencial parasite16 = (FuncionPresencial) entradaServicio.buscarFuncion("Parasite", fechaBase.plusDays(11));
        FuncionPresencial parasite20 = (FuncionPresencial) entradaServicio.buscarFuncion("Parasite", fechaBase.plusDays(11));
        FuncionPresencial volver18 = (FuncionPresencial) entradaServicio.buscarFuncion("Volver", fechaBase.plusDays(12));
        FuncionPresencial grandBudapest17 = (FuncionPresencial) entradaServicio.buscarFuncion("The Grand Budapest Hotel", fechaBase.plusDays(13));
        FuncionStreaming barbie18 = (FuncionStreaming) entradaServicio.buscarFuncion("Barbie", fechaBase.plusDays(14));
        FuncionStreaming dune17 = (FuncionStreaming) entradaServicio.buscarFuncion("Dune", fechaBase.plusDays(15));
        FuncionStreaming laLaLand18 = (FuncionStreaming) entradaServicio.buscarFuncion("La La Land", fechaBase.plusDays(16));

        // The Irishman 15:00
        entradaServicio.venderEntradaPresencial("Ana Lopez", irishman15, 1);
        entradaServicio.venderEntradaPresencial("Carlos Garcia", irishman15, 2);
        entradaServicio.venderEntradaPresencial("Maria Perez", irishman15, 3);
        entradaServicio.venderEntradaPresencial("Juan Rodriguez", irishman15, 4);
        entradaServicio.venderEntradaPresencial("Laura Martinez", irishman15, 5);

        // Roma 16:00
        entradaServicio.venderEntradaPresencial("Diego Fernandez", roma16, 1);
        entradaServicio.venderEntradaPresencial("Sofia Gonzalez", roma16, 2);
        entradaServicio.venderEntradaPresencial("Pablo Sanchez", roma16, 3);

        // Tar 17:00
        entradaServicio.venderEntradaPresencial("Valentina Torres", tar17, 1);
        entradaServicio.venderEntradaPresencial("Matias Ramirez", tar17, 2);
        entradaServicio.venderEntradaPresencial("Ana Lopez", tar17, 3);
        entradaServicio.venderEntradaPresencial("Carlos Garcia", tar17, 4);

        // Oppenheimer 14:00
        entradaServicio.venderEntradaPresencial("Maria Perez", oppenheimer14, 1);
        entradaServicio.venderEntradaPresencial("Juan Rodriguez", oppenheimer14, 2);
        entradaServicio.venderEntradaPresencial("Laura Martinez", oppenheimer14, 3);
        entradaServicio.venderEntradaPresencial("Diego Fernandez", oppenheimer14, 4);
        entradaServicio.venderEntradaPresencial("Sofia Gonzalez", oppenheimer14, 5);
        entradaServicio.venderEntradaPresencial("Pablo Sanchez", oppenheimer14, 6);

        // Parasite 16:00
        entradaServicio.venderEntradaPresencial("Valentina Torres", parasite16, 1);
        entradaServicio.venderEntradaPresencial("Matias Ramirez", parasite16, 2);
        entradaServicio.venderEntradaPresencial("Ana Lopez", parasite16, 3);

        // Parasite 20:00
        entradaServicio.venderEntradaPresencial("Carlos Garcia", parasite20, 5);
        entradaServicio.venderEntradaPresencial("Maria Perez", parasite20, 6);
        entradaServicio.venderEntradaPresencial("Juan Rodriguez", parasite20, 7);
        entradaServicio.venderEntradaPresencial("Laura Martinez", parasite20, 8);

        // Volver 18:00
        entradaServicio.venderEntradaPresencial("Diego Fernandez", volver18, 1);
        entradaServicio.venderEntradaPresencial("Sofia Gonzalez", volver18, 2);

        // Grand Budapest Hotel 17:00
        entradaServicio.venderEntradaPresencial("Pablo Sanchez", grandBudapest17, 1);
        entradaServicio.venderEntradaPresencial("Valentina Torres", grandBudapest17, 2);
        entradaServicio.venderEntradaPresencial("Matias Ramirez", grandBudapest17, 3);

        // Barbie streaming 18:00
        entradaServicio.venderEntradaStreaming("Ana Lopez", barbie18);
        entradaServicio.venderEntradaStreaming("Carlos Garcia", barbie18);
        entradaServicio.venderEntradaStreaming("Maria Perez", barbie18);
        entradaServicio.venderEntradaStreaming("Juan Rodriguez", barbie18);
        entradaServicio.venderEntradaStreaming("Laura Martinez", barbie18);

        // Dune streaming 17:00
        entradaServicio.venderEntradaStreaming("Diego Fernandez", dune17);
        entradaServicio.venderEntradaStreaming("Sofia Gonzalez", dune17);
        entradaServicio.venderEntradaStreaming("Pablo Sanchez", dune17);

        // La La Land streaming 18:00
        entradaServicio.venderEntradaStreaming("Valentina Torres", laLaLand18);
        entradaServicio.venderEntradaStreaming("Matias Ramirez", laLaLand18);
        entradaServicio.venderEntradaStreaming("Ana Lopez", laLaLand18);
        entradaServicio.venderEntradaStreaming("Carlos Garcia", laLaLand18);
    }

    private void inicializarAbonos() {
        if (!entradaServicio.getAbonos().isEmpty()) return;
        if (entradaServicio.getFunciones().isEmpty()) return;
        if (entradaServicio.getEspectadores().isEmpty()) return;

        // Abono Full con tarifa de estudiante
        Abono full = entradaServicio.comprarAbonoFull("Ana Lopez", TipoTarifa.ESTUDIANTE);
        entradaServicio.reservarPresencialConAbono(full.getCodigo(),
                buscarFuncionPresencial("Roma", fechaBase.plusDays(8), LocalTime.of(21, 0)), 10);
        entradaServicio.reservarPresencialConAbono(full.getCodigo(),
                buscarFuncionPresencial("Oppenheimer", fechaBase.plusDays(10), LocalTime.of(19, 0)), 10);

        // Abono 3 dias
        Abono tresDias = entradaServicio.comprarAbonoTresDias("Carlos Garcia", TipoTarifa.GENERAL,
                java.util.List.of(fechaBase.plusDays(7), fechaBase.plusDays(8), fechaBase.plusDays(9)));
        entradaServicio.reservarPresencialConAbono(tresDias.getCodigo(),
                buscarFuncionPresencial("The Irishman", fechaBase.plusDays(7), LocalTime.of(20, 0)), 10);
        entradaServicio.reservarPresencialConAbono(tresDias.getCodigo(),
                buscarFuncionPresencial("Roma", fechaBase.plusDays(8), LocalTime.of(16, 0)), 10);
        entradaServicio.reservarPresencialConAbono(tresDias.getCodigo(),
                buscarFuncionPresencial("Tar", fechaBase.plusDays(9), LocalTime.of(21, 0)), 10);

        // Abono Streaming con tarifa de jubilado
        Abono streaming = entradaServicio.comprarAbonoStreaming("Valentina Torres", TipoTarifa.JUBILADO);
        entradaServicio.reservarStreamingConAbono(streaming.getCodigo(),
                (FuncionStreaming) buscarFuncion("Dune", fechaBase.plusDays(15), LocalTime.of(21, 0)));
    }

    private void inicializarVotosPublico() {
        if (!peliculaServicio.getVotosPublico().isEmpty()) return;
        if (entradaServicio.getEntradas().isEmpty()) return;

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

    private void votar(String nombreEspectador, String tituloPelicula, int puntaje) {
        Espectador espectador = entradaServicio.buscarEspectador(nombreEspectador);
        peliculaServicio.registrarVotoPublico(espectador, tituloPelicula, puntaje);
    }

    private FuncionPresencial buscarFuncionPresencial(String titulo, LocalDate fecha, LocalTime horario) {
        return (FuncionPresencial) buscarFuncion(titulo, fecha, horario);
    }

    private Funcion buscarFuncion(String titulo, LocalDate fecha, LocalTime horario) {
        for (Funcion f : entradaServicio.getFunciones()) {
            if (f.getPelicula().getTitulo().equalsIgnoreCase(titulo)
                    && f.getFecha().equals(fecha) && f.getHorario().equals(horario)) {
                return f;
            }
        }
        throw new excepciones.EntidadNoEncontradaException("Funcion", titulo + " " + fecha + " " + horario);
    }
}

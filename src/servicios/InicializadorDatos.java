package servicios;

import modelo.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class InicializadorDatos {
    private PersonaServicio personaServicio;
    private PeliculaServicio peliculaServicio;
    private FestivalServicio festivalServicio;
    private FuncionServicio funcionServicio;
    private VentaServicio ventaServicio;
    private AbonoServicio abonoServicio;
    private EvaluacionServicio evaluacionServicio;
    private LocalDate fechaBase;

    /* CORRECCION B1/S14: las funciones que se programan se guardan acá y se usan directamente.
    Antes se volvían a buscar por título y fecha, y irishman20/parasite20 terminaban siendo la
    primera función del día (las 4 entradas de "Parasite 20:00" quedaban en la de las 16:00).*/
    private FuncionPresencial irishman15, irishman20, roma16, roma21, tar17, tar21, oppenheimer14, oppenheimer19, parasite16, parasite20, volver18, grandBudapest17;
    private FuncionStreaming barbie18, dune17, dune21, laLaLand18;

    public InicializadorDatos(PersonaServicio personaServicio, PeliculaServicio peliculaServicio, FestivalServicio festivalServicio, FuncionServicio funcionServicio, VentaServicio ventaServicio, AbonoServicio abonoServicio, EvaluacionServicio evaluacionServicio) {
        this.personaServicio = personaServicio;
        this.peliculaServicio = peliculaServicio;
        this.festivalServicio = festivalServicio;
        this.funcionServicio = funcionServicio;
        this.ventaServicio = ventaServicio;
        this.abonoServicio = abonoServicio;
        this.evaluacionServicio = evaluacionServicio;
    }

    /* CORRECCION P1: la demo corre solo la primera vez, cuando todavía no hay ediciones guardadas.
    Antes corría en cada inicio y, si el usuario había borrado o renombrado un dato que usa,
    la aplicación no arrancaba. Devuelve true si cargó los datos de ejemplo.*/
    public boolean inicializar() {
        if (!festivalServicio.getEdiciones().isEmpty()) return false;
        if (peliculaServicio.getPeliculas().isEmpty() || festivalServicio.getSalas().isEmpty()
                || personaServicio.getEspectadores().isEmpty()) return false;

        /* CORRECCION I6: la edición actual se arma a partir de hoy, así las funciones de la demo caen
        dentro de ella. Si faltan menos de 30 días para fin de año, la demo usa el año siguiente.*/
        LocalDate hoy = LocalDate.now();
        fechaBase = hoy.plusDays(30).getYear() == hoy.getYear() ? hoy : LocalDate.of(hoy.getYear() + 1, 1, 1);

        inicializarEdiciones();
        inicializarAsociaciones();
        inicializarReparto();
        inicializarFunciones();
        inicializarEntradas();
        inicializarAbonos();
        inicializarVotosPublico();
        return true;
    }

    private void inicializarEdiciones() {
        int anioActual = fechaBase.getYear();
        int anio1 = anioActual - 2;
        int anio2 = anioActual - 1;

        Edicion madrid = festivalServicio.registrarEdicion(anio1, "Madrid",
                LocalDate.of(anio1, 5, 1), LocalDate.of(anio1, 5, 15));
        agregarSecciones(madrid);

        Edicion paris = festivalServicio.registrarEdicion(anio2, "Paris",
                LocalDate.of(anio2, 6, 1), LocalDate.of(anio2, 6, 20));
        agregarSecciones(paris);

        Edicion buenosAires = festivalServicio.registrarEdicion(anioActual, "Buenos Aires",
                fechaBase, fechaBase.plusDays(30));
        agregarSecciones(buenosAires);
    }

    private void agregarSecciones(Edicion edicion) {
        int anio = edicion.getAnio();
        festivalServicio.registrarSeccion(edicion, TipoSeccion.COMPETENCIA_OFICIAL,
                "Palma de Oro " + anio, "Premio a la mejor pelicula");
        festivalServicio.registrarSeccion(edicion, TipoSeccion.CINE_INDEPENDIENTE,
                "Premio Independiente " + anio, "Premio al mejor cine independiente");
        festivalServicio.registrarSeccion(edicion, TipoSeccion.CORTOMETRAJE,
                "Premio Cortometraje " + anio, "Premio al mejor cortometraje");
        festivalServicio.registrarSeccion(edicion, TipoSeccion.NUEVOS_TALENTOS,
                "Premio Revelacion " + anio, "Premio al nuevo talento");
    }
    
    // CORRECCION I6: las películas con funciones en la demo compiten en la edición actual.
    // CORRECCION P13: ningún largometraje queda en Cortometraje (antes estaban Gravity, Whiplash y La La Land).
    private void inicializarAsociaciones() {
        List<Edicion> ediciones = festivalServicio.getEdiciones();
        Edicion madrid = ediciones.get(0);
        Edicion paris = ediciones.get(1);
        Edicion buenosAires = ediciones.get(2);
        asociar(buenosAires, TipoSeccion.COMPETENCIA_OFICIAL, "Oppenheimer");
        asociar(buenosAires, TipoSeccion.COMPETENCIA_OFICIAL, "Parasite");
        asociar(buenosAires, TipoSeccion.COMPETENCIA_OFICIAL, "The Irishman");
        asociar(buenosAires, TipoSeccion.CINE_INDEPENDIENTE, "Volver");
        asociar(buenosAires, TipoSeccion.CINE_INDEPENDIENTE, "Barbie");
        asociar(buenosAires, TipoSeccion.CINE_INDEPENDIENTE, "Roma");
        asociar(buenosAires, TipoSeccion.CINE_INDEPENDIENTE, "The Grand Budapest Hotel");
        asociar(buenosAires, TipoSeccion.NUEVOS_TALENTOS, "Barbie");
        asociar(buenosAires, TipoSeccion.NUEVOS_TALENTOS, "Tar");
        asociar(buenosAires, TipoSeccion.NUEVOS_TALENTOS, "Dune");
        asociar(buenosAires, TipoSeccion.NUEVOS_TALENTOS, "La La Land");
        asociar(paris, TipoSeccion.COMPETENCIA_OFICIAL, "Interstellar");
        asociar(paris, TipoSeccion.COMPETENCIA_OFICIAL, "Arrival");
        asociar(paris, TipoSeccion.CINE_INDEPENDIENTE, "Gravity");
        asociar(madrid, TipoSeccion.COMPETENCIA_OFICIAL, "Dolor y gloria");
        asociar(madrid, TipoSeccion.CINE_INDEPENDIENTE, "Moonrise Kingdom");
        asociar(madrid, TipoSeccion.NUEVOS_TALENTOS, "Lady Bird");
        asociar(madrid, TipoSeccion.NUEVOS_TALENTOS, "Whiplash");
    }

    // CORRECCION I10: los actores se buscan por email.
    private void inicializarReparto() {
        reparto("deniro@cine.com", "The Irishman");
        reparto("pacino@cine.com", "The Irishman");
        reparto("cate@cine.com", "Tar");
        reparto("timo@cine.com", "Dune");
        reparto("margot@cine.com", "Barbie");
        reparto("ryan@cine.com", "Barbie");
        reparto("emma@cine.com", "La La Land");
        reparto("ryan@cine.com", "La La Land");
        reparto("joaquin@cine.com", "Oppenheimer");
        reparto("meryl@cine.com", "Roma");
        reparto("leo@cine.com", "The Grand Budapest Hotel");
    }

    private void inicializarFunciones() {
        Sala sala1 = festivalServicio.buscarSala("Sala 1");
        Sala sala2 = festivalServicio.buscarSala("Sala 2");
        Sala sala3 = festivalServicio.buscarSala("Sala 3");
        Sala sala4 = festivalServicio.buscarSala("Sala 4");
        Sala sala5 = festivalServicio.buscarSala("Sala 5");

        irishman15 = presencial("The Irishman", sala1, 7, 15);
        irishman20 = presencial("The Irishman", sala2, 7, 20);
        roma16 = presencial("Roma", sala2, 8, 16);
        roma21 = presencial("Roma", sala3, 8, 21);
        tar17 = presencial("Tar", sala3, 9, 17);
        tar21 = presencial("Tar", sala4, 9, 21);
        oppenheimer14 = presencial("Oppenheimer", sala1, 10, 14);
        oppenheimer19 = presencial("Oppenheimer", sala5, 10, 19);
        parasite16 = presencial("Parasite", sala2, 11, 16);
        parasite20 = presencial("Parasite", sala4, 11, 20);
        volver18 = presencial("Volver", sala3, 12, 18);
        presencial("Volver", sala5, 12, 21);
        grandBudapest17 = presencial("The Grand Budapest Hotel", sala1, 13, 17);
        presencial("The Grand Budapest Hotel", sala4, 13, 20);

        barbie18 = streaming("Barbie", "https://stream.festival.com/barbie1", 500, 14, 18);
        streaming("Barbie", "https://stream.festival.com/barbie2", 500, 14, 21);
        dune17 = streaming("Dune", "https://stream.festival.com/dune1", 300, 15, 17);
        dune21 = streaming("Dune", "https://stream.festival.com/dune2", 300, 15, 21);
        laLaLand18 = streaming("La La Land", "https://stream.festival.com/lalaland1", 400, 16, 18);
        streaming("La La Land", "https://stream.festival.com/lalaland2", 400, 16, 21);
    }

    private void inicializarEntradas() {
        // The Irishman 15:00
        vender("ana@gmail.com", irishman15, 1);
        vender("carlos@gmail.com", irishman15, 2);
        vender("maria@gmail.com", irishman15, 3);
        vender("juan@gmail.com", irishman15, 4);
        vender("laura@gmail.com", irishman15, 5);

        // Roma 16:00
        vender("diego@gmail.com", roma16, 1);
        vender("sofia@gmail.com", roma16, 2);
        vender("pablo@gmail.com", roma16, 3);

        // Tar 17:00
        vender("vale@gmail.com", tar17, 1);
        vender("matias@gmail.com", tar17, 2);
        vender("ana@gmail.com", tar17, 3);
        vender("carlos@gmail.com", tar17, 4);

        // Oppenheimer 14:00
        vender("maria@gmail.com", oppenheimer14, 1);
        vender("juan@gmail.com", oppenheimer14, 2);
        vender("laura@gmail.com", oppenheimer14, 3);
        vender("diego@gmail.com", oppenheimer14, 4);
        vender("sofia@gmail.com", oppenheimer14, 5);
        vender("pablo@gmail.com", oppenheimer14, 6);

        // Parasite 16:00
        vender("vale@gmail.com", parasite16, 1);
        vender("matias@gmail.com", parasite16, 2);
        vender("ana@gmail.com", parasite16, 3);

        // Parasite 20:00
        vender("carlos@gmail.com", parasite20, 5);
        vender("maria@gmail.com", parasite20, 6);
        vender("juan@gmail.com", parasite20, 7);
        vender("laura@gmail.com", parasite20, 8);

        // Volver 18:00
        vender("diego@gmail.com", volver18, 1);
        vender("sofia@gmail.com", volver18, 2);

        // Grand Budapest Hotel 17:00
        vender("pablo@gmail.com", grandBudapest17, 1);
        vender("vale@gmail.com", grandBudapest17, 2);
        vender("matias@gmail.com", grandBudapest17, 3);

        // Barbie streaming 18:00
        venderStreaming("ana@gmail.com", barbie18);
        venderStreaming("carlos@gmail.com", barbie18);
        venderStreaming("maria@gmail.com", barbie18);
        venderStreaming("juan@gmail.com", barbie18);
        venderStreaming("laura@gmail.com", barbie18);

        // Dune streaming 17:00
        venderStreaming("diego@gmail.com", dune17);
        venderStreaming("sofia@gmail.com", dune17);
        venderStreaming("pablo@gmail.com", dune17);

        // La La Land streaming 18:00
        venderStreaming("vale@gmail.com", laLaLand18);
        venderStreaming("matias@gmail.com", laLaLand18);
        venderStreaming("ana@gmail.com", laLaLand18);
        venderStreaming("carlos@gmail.com", laLaLand18);
    }

    private void inicializarAbonos() {
        // Abono Full con tarifa de estudiante
        Abono full = abonoServicio.comprarAbonoFull(espectador("ana@gmail.com"), TipoTarifa.ESTUDIANTE);
        abonoServicio.reservarPresencial(full, roma21, 10);
        abonoServicio.reservarPresencial(full, oppenheimer19, 10);

        // Abono 3 dias
        Abono tresDias = abonoServicio.comprarAbonoTresDias(espectador("carlos@gmail.com"), TipoTarifa.GENERAL,
                List.of(fechaBase.plusDays(7), fechaBase.plusDays(8), fechaBase.plusDays(9)));
        abonoServicio.reservarPresencial(tresDias, irishman20, 10);
        abonoServicio.reservarPresencial(tresDias, roma16, 10);
        abonoServicio.reservarPresencial(tresDias, tar21, 10);

        // Abono Streaming con tarifa de jubilado
        Abono streaming = abonoServicio.comprarAbonoStreaming(espectador("vale@gmail.com"), TipoTarifa.JUBILADO);
        abonoServicio.reservarStreaming(streaming, dune21);
    }

    private void inicializarVotosPublico() {
        votar("ana@gmail.com", "The Irishman", 4);
        votar("carlos@gmail.com", "The Irishman", 5);
        votar("maria@gmail.com", "The Irishman", 4);
        votar("diego@gmail.com", "Roma", 5);
        votar("sofia@gmail.com", "Roma", 5);
        votar("pablo@gmail.com", "Roma", 4);
        votar("vale@gmail.com", "Tar", 3);
        votar("matias@gmail.com", "Tar", 4);
        votar("maria@gmail.com", "Oppenheimer", 5);
        votar("juan@gmail.com", "Oppenheimer", 4);
        votar("laura@gmail.com", "Oppenheimer", 5);
        votar("vale@gmail.com", "Parasite", 5);
        votar("carlos@gmail.com", "Parasite", 4);
        votar("diego@gmail.com", "Volver", 3);
        votar("ana@gmail.com", "Barbie", 4);
        votar("carlos@gmail.com", "Barbie", 3);
        votar("diego@gmail.com", "Dune", 5);
        votar("sofia@gmail.com", "Dune", 4);
    }
    

    private Pelicula pelicula(String titulo) {
        return peliculaServicio.buscarPelicula(titulo);
    }

    private Espectador espectador(String email) {
        return personaServicio.buscarEspectador(email);
    }

    private void asociar(Edicion edicion, TipoSeccion tipo, String titulo) {
        festivalServicio.asociarPeliculaASeccion(edicion, tipo, pelicula(titulo));
    }

    private void reparto(String emailActor, String titulo) {
        peliculaServicio.asociarActorAPelicula(personaServicio.buscarActor(emailActor), pelicula(titulo));
    }

    private FuncionPresencial presencial(String titulo, Sala sala, int diasDesdeHoy, int hora) {
        return funcionServicio.programarFuncionPresencial(pelicula(titulo), sala,
                fechaBase.plusDays(diasDesdeHoy), LocalTime.of(hora, 0));
    }

    private FuncionStreaming streaming(String titulo, String enlace, int capacidad, int diasDesdeHoy, int hora) {
        return funcionServicio.programarFuncionStreaming(pelicula(titulo), enlace, capacidad,
                fechaBase.plusDays(diasDesdeHoy), LocalTime.of(hora, 0));
    }

    private void vender(String email, FuncionPresencial funcion, int butaca) {
        ventaServicio.venderEntradaPresencial(espectador(email), funcion, butaca, TipoTarifa.GENERAL);
    }

    private void venderStreaming(String email, FuncionStreaming funcion) {
        ventaServicio.venderEntradaStreaming(espectador(email), funcion, TipoTarifa.GENERAL);
    }

    private void votar(String email, String titulo, int puntaje) {
        evaluacionServicio.registrarVotoPublico(espectador(email), pelicula(titulo), puntaje);
    }
}

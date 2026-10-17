package inicializacion;

import modelo.festival.TipoSeccion;
import servicios.FestivalServicio;
import servicios.PeliculaServicio;

// Qué películas compiten en cada sección. Cada película compite en una sola edición
// (puede estar en varias secciones de esa edición). Las películas que tienen funciones en la demo
// compiten en la edición actual, porque las funciones tienen que caer dentro de su edición
class InicializadorSecciones implements Inicializador {
    private final FestivalServicio festivalServicio;
    private final PeliculaServicio peliculaServicio;
    private final CalendarioDemo calendario;

    InicializadorSecciones(FestivalServicio festivalServicio, PeliculaServicio peliculaServicio, CalendarioDemo calendario) {
        this.festivalServicio = festivalServicio;
        this.peliculaServicio = peliculaServicio;
        this.calendario = calendario;
    }

    @Override
    public void inicializar() {
        if (peliculaServicio.getPeliculas().isEmpty()) return;

        int actual = calendario.anioEdicionActual();
        asociar(actual, TipoSeccion.COMPETENCIA_OFICIAL, "The Irishman", "Roma", "Oppenheimer", "Parasite");
        asociar(actual, TipoSeccion.CINE_INDEPENDIENTE, "Volver", "Tar", "The Grand Budapest Hotel");
        asociar(actual, TipoSeccion.CORTOMETRAJE, "La La Land");
        asociar(actual, TipoSeccion.NUEVOS_TALENTOS, "Barbie", "Dune");

        asociar(2025, TipoSeccion.COMPETENCIA_OFICIAL, "Interstellar", "Arrival");
        asociar(2025, TipoSeccion.CINE_INDEPENDIENTE, "Moonrise Kingdom");
        asociar(2025, TipoSeccion.CORTOMETRAJE, "Gravity");

        asociar(2024, TipoSeccion.COMPETENCIA_OFICIAL, "Dolor y gloria");
        asociar(2024, TipoSeccion.CORTOMETRAJE, "Whiplash");
        asociar(2024, TipoSeccion.NUEVOS_TALENTOS, "Lady Bird");
    }

    private void asociar(int anio, TipoSeccion tipo, String... titulos) {
        // Las ediciones anteriores solo existen si son de un año anterior al de la edición actual
        if (festivalServicio.getEdiciones().stream().noneMatch(edicion -> edicion.getAnio() == anio)) return;
        for (String titulo : titulos) {
            festivalServicio.asociarPeliculaASeccion(anio, tipo, peliculaServicio.buscarPelicula(titulo));
        }
    }
}

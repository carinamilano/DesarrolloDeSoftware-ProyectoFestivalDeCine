package inicializacion;

import modelo.TipoSeccion;
import servicios.FestivalServicio;
import servicios.PeliculaServicio;

// Qué películas compiten en cada sección. Cada película compite en una sola edición
// (puede estar en varias secciones de esa edición)
class InicializadorSecciones implements Inicializador {
    private final FestivalServicio festivalServicio;
    private final PeliculaServicio peliculaServicio;

    InicializadorSecciones(FestivalServicio festivalServicio, PeliculaServicio peliculaServicio) {
        this.festivalServicio = festivalServicio;
        this.peliculaServicio = peliculaServicio;
    }

    @Override
    public void inicializar() {
        if (peliculaServicio.getPeliculas().isEmpty()) return;

        asociar(2026, TipoSeccion.COMPETENCIA_OFICIAL, "Oppenheimer", "Parasite");
        asociar(2026, TipoSeccion.CINE_INDEPENDIENTE, "Volver", "Barbie");
        asociar(2026, TipoSeccion.CORTOMETRAJE, "La La Land");
        asociar(2026, TipoSeccion.NUEVOS_TALENTOS, "Barbie");

        asociar(2025, TipoSeccion.COMPETENCIA_OFICIAL, "Tar", "Interstellar");
        asociar(2025, TipoSeccion.CINE_INDEPENDIENTE, "The Grand Budapest Hotel");
        asociar(2025, TipoSeccion.CORTOMETRAJE, "Gravity");
        asociar(2025, TipoSeccion.NUEVOS_TALENTOS, "Dune", "Arrival");

        asociar(2024, TipoSeccion.COMPETENCIA_OFICIAL, "The Irishman", "Roma");
        asociar(2024, TipoSeccion.CINE_INDEPENDIENTE, "Dolor y gloria", "Moonrise Kingdom");
        asociar(2024, TipoSeccion.CORTOMETRAJE, "Whiplash");
        asociar(2024, TipoSeccion.NUEVOS_TALENTOS, "Lady Bird");
    }

    private void asociar(int anio, TipoSeccion tipo, String... titulos) {
        for (String titulo : titulos) {
            festivalServicio.asociarPeliculaASeccion(anio, tipo, peliculaServicio.buscarPelicula(titulo));
        }
    }
}

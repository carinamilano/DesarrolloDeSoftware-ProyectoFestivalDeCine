package inicializacion;

import modelo.festival.TipoSeccion;
import servicios.FestivalServicio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// 2 ediciones anteriores y la edición actual, cada una con sus 4 secciones y premios
class InicializadorEdiciones implements Inicializador {
    static final String CIUDAD_EDICION_ACTUAL = "Buenos Aires";

    private record DatosEdicion(int anio, String ciudad, LocalDate inicio, LocalDate fin) {}
    private record DatosPremio(TipoSeccion tipo, String nombre, String descripcion) {}

    private static final List<DatosEdicion> EDICIONES_ANTERIORES = List.of(
            new DatosEdicion(2024, "Madrid", LocalDate.of(2024, 5, 1), LocalDate.of(2024, 5, 15)),
            new DatosEdicion(2025, "Paris", LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 20)));

    private static final List<DatosPremio> PREMIOS = List.of(
            new DatosPremio(TipoSeccion.COMPETENCIA_OFICIAL, "Palma de Oro", "Premio a la mejor pelicula"),
            new DatosPremio(TipoSeccion.CINE_INDEPENDIENTE, "Premio Independiente", "Premio al mejor cine independiente"),
            new DatosPremio(TipoSeccion.CORTOMETRAJE, "Premio Cortometraje", "Premio al mejor cortometraje"),
            new DatosPremio(TipoSeccion.NUEVOS_TALENTOS, "Premio Revelacion", "Premio al nuevo talento"));

    private final FestivalServicio festivalServicio;
    private final CalendarioDemo calendario;

    InicializadorEdiciones(FestivalServicio festivalServicio, CalendarioDemo calendario) {
        this.festivalServicio = festivalServicio;
        this.calendario = calendario;
    }

    @Override
    public void inicializar() {
        if (!festivalServicio.getEdiciones().isEmpty()) return;

        List<DatosEdicion> ediciones = new ArrayList<>(EDICIONES_ANTERIORES.stream()
                .filter(edicion -> edicion.anio() < calendario.anioEdicionActual())
                .toList());
        ediciones.add(new DatosEdicion(calendario.anioEdicionActual(), CIUDAD_EDICION_ACTUAL,
                calendario.inicioEdicionActual(), calendario.finEdicionActual()));

        for (DatosEdicion edicion : ediciones) {
            festivalServicio.registrarEdicion(edicion.anio(), edicion.ciudad(), edicion.inicio(), edicion.fin());
            for (DatosPremio premio : PREMIOS) {
                festivalServicio.registrarSeccion(edicion.anio(), premio.tipo(),
                        premio.nombre() + " " + edicion.anio(), premio.descripcion());
            }
        }
    }
}

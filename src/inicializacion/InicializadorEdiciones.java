package inicializacion;

import modelo.TipoSeccion;
import servicios.FestivalServicio;

import java.time.LocalDate;
import java.util.List;

// 3 ediciones con sus 4 secciones y premios (antes eran tres bloques copiados a mano)
class InicializadorEdiciones implements Inicializador {

    private record DatosEdicion(int anio, String ciudad, LocalDate inicio, LocalDate fin) {}
    private record DatosPremio(TipoSeccion tipo, String nombre, String descripcion) {}

    private static final List<DatosEdicion> EDICIONES = List.of(
            new DatosEdicion(2024, "Madrid", LocalDate.of(2024, 5, 1), LocalDate.of(2024, 5, 15)),
            new DatosEdicion(2025, "Paris", LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 20)),
            new DatosEdicion(2026, "Buenos Aires", LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 31)));

    private static final List<DatosPremio> PREMIOS = List.of(
            new DatosPremio(TipoSeccion.COMPETENCIA_OFICIAL, "Palma de Oro", "Premio a la mejor pelicula"),
            new DatosPremio(TipoSeccion.CINE_INDEPENDIENTE, "Premio Independiente", "Premio al mejor cine independiente"),
            new DatosPremio(TipoSeccion.CORTOMETRAJE, "Premio Cortometraje", "Premio al mejor cortometraje"),
            new DatosPremio(TipoSeccion.NUEVOS_TALENTOS, "Premio Revelacion", "Premio al nuevo talento"));

    private final FestivalServicio festivalServicio;

    InicializadorEdiciones(FestivalServicio festivalServicio) {
        this.festivalServicio = festivalServicio;
    }

    @Override
    public void inicializar() {
        if (!festivalServicio.getEdiciones().isEmpty()) return;

        for (DatosEdicion edicion : EDICIONES) {
            festivalServicio.registrarEdicion(edicion.anio(), edicion.ciudad(), edicion.inicio(), edicion.fin());
            for (DatosPremio premio : PREMIOS) {
                festivalServicio.registrarSeccion(edicion.anio(), premio.tipo(),
                        premio.nombre() + " " + edicion.anio(), premio.descripcion());
            }
        }
    }
}

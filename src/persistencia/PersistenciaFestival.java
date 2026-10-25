package persistencia;

import modelo.Edicion;
import modelo.Pelicula;
import modelo.Sala;
import modelo.Seccion;
import modelo.TipoSeccion;
import servicios.FestivalServicio;
import servicios.PeliculaServicio;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// CORRECCION S15: salas, ediciones, secciones y películas de cada sección (antes en Persistencia).
class PersistenciaFestival extends ArchivosDeDominio {

    private static final String SALAS = "salas.txt";
    private static final String EDICIONES = "ediciones.txt";
    private static final String SECCIONES = "secciones.txt";
    private static final String SECCIONES_PELICULAS = "secciones_peliculas.txt";
    private final FestivalServicio festivalServicio;
    private final PeliculaServicio peliculaServicio;

    PersistenciaFestival(Path carpeta, List<String> erroresDeCarga, FestivalServicio festivalServicio,
                         PeliculaServicio peliculaServicio) {
        super(carpeta, erroresDeCarga);
        this.festivalServicio = festivalServicio;
        this.peliculaServicio = peliculaServicio;
    }

    @Override
    void cargar() {
        cargarCada(SALAS, r -> festivalServicio.registrarSala(r.texto(0), r.entero(1)));
        cargarCada(EDICIONES, r -> festivalServicio.registrarEdicion(r.entero(0), r.texto(1), r.fecha(2), r.fecha(3)));
        cargarCada(SECCIONES, r -> {
            Edicion edicion = festivalServicio.buscarEdicion(r.entero(0));
            festivalServicio.registrarSeccion(edicion, r.enumerado(1, TipoSeccion.class), r.texto(2), r.textoOpcional(3));
        });
        cargarCada(SECCIONES_PELICULAS, r -> {
            Edicion edicion = festivalServicio.buscarEdicion(r.entero(0));
            Pelicula pelicula = peliculaServicio.buscarPelicula(r.texto(2));
            festivalServicio.asociarPeliculaASeccion(edicion, r.enumerado(1, TipoSeccion.class), pelicula);
        });
    }

    @Override
    void agregarLineas(Map<String, List<String>> archivos) {
        archivos.put(SALAS, lineasSalas());
        archivos.put(EDICIONES, lineasEdiciones());
        archivos.put(SECCIONES, lineasSecciones());
        archivos.put(SECCIONES_PELICULAS, lineasSeccionesPeliculas());
    }

    private List<String> lineasSalas() {
        List<String> lineas = new ArrayList<>();
        for (Sala s : festivalServicio.getSalas()) {
            lineas.add(FormatoLinea.unir(s.getNombre(), s.getCantidadButacas()));
        }
        return lineas;
    }

    private List<String> lineasEdiciones() {
        List<String> lineas = new ArrayList<>();
        for (Edicion e : festivalServicio.getEdiciones()) {
            lineas.add(FormatoLinea.unir(e.getAnio(), e.getCiudad(), e.getFechaInicio(), e.getFechaFin()));
        }
        return lineas;
    }

    private List<String> lineasSecciones() {
        List<String> lineas = new ArrayList<>();
        for (Edicion e : festivalServicio.getEdiciones()) {
            for (Seccion s : e.getSecciones()) {
                lineas.add(FormatoLinea.unir(e.getAnio(), s.getTipo().name(),
                        s.getPremio().getNombre(), s.getPremio().getDescripcion()));
            }
        }
        return lineas;
    }

    private List<String> lineasSeccionesPeliculas() {
        List<String> lineas = new ArrayList<>();
        for (Edicion e : festivalServicio.getEdiciones()) {
            for (Seccion s : e.getSecciones()) {
                for (Pelicula p : s.getPeliculas()) {
                    lineas.add(FormatoLinea.unir(e.getAnio(), s.getTipo().name(), p.getTitulo()));
                }
            }
        }
        return lineas;
    }
}

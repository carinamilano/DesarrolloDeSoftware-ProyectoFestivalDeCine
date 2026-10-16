package modelo;

import excepciones.FestivalException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Edicion {
    public static final int ANIO_MINIMO = 2001;
    public static final int ANIO_MAXIMO = 2030;

    private final int anio;
    private final String ciudad;
    private final LocalDate fechaInicio;
    private final LocalDate fechaFin;
    private final List<Seccion> secciones;

    public Edicion(int anio, String ciudad, LocalDate fechaInicio, LocalDate fechaFin) {
        if (anio < ANIO_MINIMO || anio > ANIO_MAXIMO) {
            throw new FestivalException("El año debe estar entre " + ANIO_MINIMO + " y " + ANIO_MAXIMO + ".");
        }
        this.ciudad = Validar.texto(ciudad, "La ciudad es obligatoria.");
        this.fechaInicio = Validar.presente(fechaInicio, "La fecha de inicio es obligatoria.");
        this.fechaFin = Validar.presente(fechaFin, "La fecha de fin es obligatoria.");
        if (fechaInicio.getYear() != anio) {
            throw new FestivalException("La fecha inicio debe corresponder al año " + anio + ".");
        }
        if (fechaFin.getYear() != anio) {
            throw new FestivalException("La fecha fin debe corresponder al año " + anio + ".");
        }
        if (fechaFin.isBefore(fechaInicio)) {
            throw new FestivalException("La fecha fin debe ser posterior a la fecha inicio.");
        }
        this.anio = anio;
        this.secciones = new ArrayList<>();
    }

    // Creator: la edición contiene sus secciones, así que ella las crea
    public Seccion agregarSeccion(TipoSeccion tipo, Premio premio) {
        if (tieneSeccion(tipo)) {
            throw new FestivalException("La edición " + anio + " ya tiene la sección " + tipo + ".");
        }
        Seccion seccion = new Seccion(tipo, premio);
        secciones.add(seccion);
        return seccion;
    }

    public Optional<Seccion> buscarSeccion(TipoSeccion tipo) {
        return secciones.stream().filter(seccion -> seccion.getTipo() == tipo).findFirst();
    }

    public boolean tieneSeccion(TipoSeccion tipo) {
        return buscarSeccion(tipo).isPresent();
    }

    public List<TipoSeccion> getTiposSinSeccion() {
        return List.of(TipoSeccion.values()).stream().filter(tipo -> !tieneSeccion(tipo)).toList();
    }

    public boolean tieneTodasLasSecciones() {
        return getTiposSinSeccion().isEmpty();
    }

    public boolean incluye(Pelicula pelicula) {
        return secciones.stream().anyMatch(seccion -> seccion.contiene(pelicula));
    }

    public void quitarPelicula(Pelicula pelicula) {
        secciones.forEach(seccion -> seccion.quitarPelicula(pelicula));
    }

    // Películas de todas las secciones, sin repetir
    public List<Pelicula> getPeliculas() {
        return secciones.stream().flatMap(seccion -> seccion.getPeliculas().stream()).distinct().toList();
    }

    // Premio del Público: la película de la edición con mejor promedio de votos del público
    public Optional<Pelicula> determinarGanadoraPublico() {
        return getPeliculas().stream()
                .filter(Pelicula::tieneVotos)
                .max(Comparator.comparingDouble(Pelicula::calcularPromedioPublico));
    }

    public int getAnio() {
        return anio;
    }
    public String getCiudad() {
        return ciudad;
    }
    public LocalDate getFechaInicio() {
        return fechaInicio;
    }
    public LocalDate getFechaFin() {
        return fechaFin;
    }
    public List<Seccion> getSecciones() {
        return Collections.unmodifiableList(secciones);
    }

    @Override
    public String toString() {
        return anio + " - " + ciudad;
    }
}

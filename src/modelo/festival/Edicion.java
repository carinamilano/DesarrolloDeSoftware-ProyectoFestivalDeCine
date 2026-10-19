package modelo.festival;

import excepciones.DatoInvalidoException;
import excepciones.EntidadDuplicadaException;
import excepciones.EntidadNoEncontradaException;
import excepciones.FuncionNoDisponibleException;
import excepciones.ReglaNegocioException;
import modelo.comun.Validar;
import modelo.peliculas.Pelicula;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
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
            throw new DatoInvalidoException("El año debe estar entre " + ANIO_MINIMO + " y " + ANIO_MAXIMO + ".");
        }
        this.ciudad = Validar.texto(ciudad, "La ciudad es obligatoria.");
        this.fechaInicio = Validar.presente(fechaInicio, "La fecha de inicio es obligatoria.");
        this.fechaFin = Validar.presente(fechaFin, "La fecha de fin es obligatoria.");
        if (fechaInicio.getYear() != anio) {
            throw new DatoInvalidoException("La fecha inicio debe corresponder al año " + anio + ".");
        }
        if (fechaFin.getYear() != anio) {
            throw new DatoInvalidoException("La fecha fin debe corresponder al año " + anio + ".");
        }
        if (fechaFin.isBefore(fechaInicio)) {
            throw new DatoInvalidoException("La fecha fin debe ser posterior a la fecha inicio.");
        }
        this.anio = anio;
        this.secciones = new ArrayList<>();
    }

    // Creator: la edición contiene sus secciones, así que ella las crea
    public Seccion agregarSeccion(TipoSeccion tipo, Premio premio) {
        if (tieneSeccion(tipo)) {
            throw new EntidadDuplicadaException("La edición " + anio + " ya tiene la sección " + tipo + ".");
        }
        Seccion seccion = new Seccion(tipo, premio);
        secciones.add(seccion);
        return seccion;
    }

    // Una película con funciones no puede dejar de competir en la edición: sus funciones quedarían fuera de toda edición
    public void quitarDeSeccion(TipoSeccion tipo, Pelicula pelicula) {
        Seccion seccion = buscarSeccion(tipo).orElseThrow(() -> new EntidadNoEncontradaException("Seccion", "tipo: " + tipo));
        if (!seccion.contiene(pelicula)) {
            throw new ReglaNegocioException(pelicula.getTitulo() + " no está en la sección " + tipo + ".");
        }
        boolean sigueEnOtraSeccion = secciones.stream().anyMatch(otra -> otra != seccion && otra.contiene(pelicula));
        if (!sigueEnOtraSeccion && pelicula.tieneFunciones()) {
            throw new ReglaNegocioException(pelicula.getTitulo() + " tiene funciones programadas: no puede dejar de competir en la edición " + anio + ".");
        }
        seccion.quitarPelicula(pelicula);
    }

    // Las funciones de las películas de la edición se programan entre su fecha de inicio y su fecha de fin
    public boolean incluyeFecha(LocalDate fecha) {
        return !fecha.isBefore(fechaInicio) && !fecha.isAfter(fechaFin);
    }

    public void validarFechaDeFuncion(LocalDate fecha) {
        if (!incluyeFecha(fecha)) {
            throw new FuncionNoDisponibleException("la edición " + anio + " va del " + fechaInicio + " al " + fechaFin
                    + "; la función del " + fecha + " queda fuera.");
        }
    }

    public boolean yaTermino(LocalDate hoy) {
        return fechaFin.isBefore(hoy);
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

    // Al eliminar una película (que no tiene funciones) se la quita de todas las secciones
    public void quitarPelicula(Pelicula pelicula) {
        secciones.forEach(seccion -> seccion.quitarPelicula(pelicula));
    }

    // Películas de todas las secciones, sin repetir
    public List<Pelicula> getPeliculas() {
        return secciones.stream().flatMap(seccion -> seccion.getPeliculas().stream()).distinct().toList();
    }

    // Premio del Público: la película de la edición con mejor promedio de votos del público
    public Optional<Pelicula> determinarGanadoraPublico() {
        return Premiacion.mejorPromedio(getPeliculas(), Pelicula::tieneVotos, Pelicula::calcularPromedioPublico);
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

    // Identidad: el año (el festival tiene una sola edición por año)
    @Override
    public boolean equals(Object otro) {
        return otro instanceof Edicion edicion && anio == edicion.anio;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(anio);
    }

    @Override
    public String toString() {
        return anio + " - " + ciudad;
    }
}

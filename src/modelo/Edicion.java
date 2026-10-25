package modelo;

import excepciones.DatoInvalidoException;
import excepciones.EntidadDuplicadaException;
import excepciones.EntidadNoEncontradaException;
import excepciones.ReglaNegocioException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Edicion {
    public static final int ANIO_MINIMO = 2001;
    public static final int ANIO_MAXIMO = 2030;

    private int anio;
    private String ciudad;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private List<Seccion> secciones;

    public Edicion(int anio, String ciudad, LocalDate fechaInicio, LocalDate fechaFin) {
        /* CORRECCION I9: un solo rango de año (antes PanelEdiciones y FestivalServicio usaban rangos distintos)
         y las fechas se validan acá, no solo en la interfaz.*/
        if (anio < ANIO_MINIMO || anio > ANIO_MAXIMO) {
            throw new DatoInvalidoException("El año debe estar entre " + ANIO_MINIMO + " y " + ANIO_MAXIMO + ".");
        }
        this.anio = anio;
        this.ciudad = Validar.texto(ciudad, "La ciudad es obligatoria.");
        this.fechaInicio = Validar.presente(fechaInicio, "La fecha de inicio es obligatoria.");
        this.fechaFin = Validar.presente(fechaFin, "La fecha fin es obligatoria.");
        if (fechaInicio.getYear() != anio) {
            throw new DatoInvalidoException("La fecha inicio debe corresponder al año " + anio + ".");
        }
        if (fechaFin.getYear() != anio) {
            throw new DatoInvalidoException("La fecha fin debe corresponder al año " + anio + ".");
        }
        if (fechaFin.isBefore(fechaInicio)) {
            throw new DatoInvalidoException("La fecha fin debe ser posterior a la fecha inicio.");
        }
        this.secciones = new ArrayList<>();
    }

    public void agregarSeccion(Seccion s) {
        Validar.presente(s, "La sección es obligatoria.");
        // CORRECCION I8: la edición controla sus secciones repetidas (antes lo hacía FestivalServicio).
        if (tieneSeccion(s.getTipo())) {
            throw new EntidadDuplicadaException("La edición " + anio + " ya tiene la sección " + s.getTipo() + ".");
        }
        secciones.add(s);
    }

    public boolean tieneSeccion(TipoSeccion tipo) {
        for (Seccion s : secciones) {
            if (s.getTipo() == tipo) return true;
        }
        return false;
    }

    // CORRECCION S5: si no existe lanza una excepción en lugar de devolver null.
    public Seccion buscarSeccion(TipoSeccion tipo) {
        for (Seccion s : secciones) {
            if (s.getTipo() == tipo) return s;
        }
        throw new EntidadNoEncontradaException("Seccion", tipo + " en la edición " + anio);
    }

    public List<TipoSeccion> getTiposSinSeccion() {
        List<TipoSeccion> disponibles = new ArrayList<>();
        for (TipoSeccion t : TipoSeccion.values()) {
            if (!tieneSeccion(t)) disponibles.add(t);
        }
        return disponibles;
    }

    // CORRECCION I6: las funciones tienen que caer entre el inicio y el fin de la edición.
    public boolean incluyeFecha(LocalDate fecha) {
        return !fecha.isBefore(fechaInicio) && !fecha.isAfter(fechaFin);
    }

    public boolean incluye(Pelicula pelicula) {
        for (Seccion s : secciones) {
            if (s.contiene(pelicula)) return true;
        }
        return false;
    }

    public List<Pelicula> getPeliculas() {
        List<Pelicula> peliculas = new ArrayList<>();
        for (Seccion s : secciones) {
            for (Pelicula p : s.getPeliculas()) {
                if (!peliculas.contains(p)) peliculas.add(p);
            }
        }
        return peliculas;
    }

    /* CORRECCION I7: no se puede quitar una película con funciones de su última sección en la edición,
    porque quedarían funciones de una película que ya no compite.*/
    public void quitarPeliculaDeSeccion(TipoSeccion tipo, Pelicula pelicula) {
        Seccion seccion = buscarSeccion(tipo);
        if (!seccion.contiene(pelicula)) {
            throw new ReglaNegocioException(pelicula.getTitulo() + " no está en la sección " + tipo + ".");
        }
        if (pelicula.tieneFunciones() && cantidadDeSeccionesCon(pelicula) == 1) {
            throw new ReglaNegocioException("No se puede quitar " + pelicula.getTitulo()
                    + ": tiene funciones programadas y es su única sección en la edición " + anio + ".");
        }
        seccion.quitarPelicula(pelicula);
    }

    void quitarDeTodasLasSecciones(Pelicula pelicula) {
        for (Seccion s : secciones) {
            s.quitarPelicula(pelicula);
        }
    }

    private int cantidadDeSeccionesCon(Pelicula pelicula) {
        int cantidad = 0;
        for (Seccion s : secciones) {
            if (s.contiene(pelicula)) cantidad++;
        }
        return cantidad;
    }

    // CORRECCION I12: solo hay ganadora del público si alguna película recibió votos.
    public boolean tieneGanadoraPublico() {
        for (Pelicula p : getPeliculas()) {
            if (p.getCalificaciones().tieneVotos()) return true;
        }
        return false;
    }

    // CORRECCION I12: si hay empate gana la que tiene más votos. Consultar tieneGanadoraPublico() antes.
    public Pelicula determinarGanadoraPublico() {
        Pelicula ganadora = null;
        for (Pelicula p : getPeliculas()) {
            if (!p.getCalificaciones().tieneVotos()) continue;
            if (ganadora == null || esMejorSegunPublico(p, ganadora)) {
                ganadora = p;
            }
        }
        if (ganadora == null) {
            throw new ReglaNegocioException("La edición " + anio + " no tiene votos del público.");
        }
        return ganadora;
    }

    private boolean esMejorSegunPublico(Pelicula candidata, Pelicula actual) {
        Calificaciones c = candidata.getCalificaciones();
        Calificaciones a = actual.getCalificaciones();
        if (c.calcularPromedioPublico() != a.calcularPromedioPublico()) {
            return c.calcularPromedioPublico() > a.calcularPromedioPublico();
        }
        return c.cantidadVotos() > a.cantidadVotos();
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
    // CORRECCION I13: la lista no se puede modificar desde afuera.
    public List<Seccion> getSecciones() {
        return Collections.unmodifiableList(secciones);
    }

    // CORRECCION S13: el año identifica a la edición.
    @Override
    public boolean equals(Object otro) {
        if (this == otro) return true;
        if (!(otro instanceof Edicion)) return false;
        return anio == ((Edicion) otro).anio;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(anio);
    }

    @Override
    public String toString() {
        return "Edicion " + anio + " - " + ciudad;
    }
}

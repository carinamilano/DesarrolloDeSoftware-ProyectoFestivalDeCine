package modelo;

import excepciones.EntidadDuplicadaException;
import excepciones.ReglaNegocioException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Seccion {
    private TipoSeccion tipo;
    private Premio premio;
    private List<Pelicula> peliculas;

    public Seccion(TipoSeccion tipo, Premio premio) {
        // CORRECCION I9: datos obligatorios.
        this.tipo = Validar.presente(tipo, "El tipo de sección es obligatorio.");
        this.premio = Validar.presente(premio, "El premio es obligatorio.");
        this.peliculas = new ArrayList<>();
    }

    public void agregarPelicula(Pelicula p) {
        Validar.presente(p, "La película es obligatoria.");
        // CORRECCION I8: la sección controla sus duplicados (antes lo hacía FestivalServicio).
        if (contiene(p)) {
            throw new EntidadDuplicadaException(p.getTitulo() + " ya está en la sección " + tipo + ".");
        }
        peliculas.add(p);
    }

    // CORRECCION I7: solo se quita a través de Edicion, que controla las funciones programadas.
    void quitarPelicula(Pelicula p) {
        peliculas.remove(p);
    }

    public boolean contiene(Pelicula p) {
        return peliculas.contains(p);
    }

    // CORRECCION I12: solo hay ganadora si alguna película fue evaluada.
    public boolean tieneGanadora() {
        for (Pelicula p : peliculas) {
            if (p.getCalificaciones().tieneEvaluaciones()) return true;
        }
        return false;
    }

    /* CORRECCION I12: ignora las películas sin evaluaciones (antes podía ganar una con promedio 0)
     y desempata por cantidad de evaluaciones. Consultar tieneGanadora() antes de llamarlo.*/
    public Pelicula determinarGanadora() {
        Pelicula ganadora = null;
        for (Pelicula p : peliculas) {
            if (!p.getCalificaciones().tieneEvaluaciones()) continue;
            if (ganadora == null || esMejor(p, ganadora)) {
                ganadora = p;
            }
        }
        if (ganadora == null) {
            throw new ReglaNegocioException("La sección " + tipo + " no tiene películas evaluadas.");
        }
        return ganadora;
    }

    private boolean esMejor(Pelicula candidata, Pelicula actual) {
        Calificaciones c = candidata.getCalificaciones();
        Calificaciones a = actual.getCalificaciones();
        if (c.calcularPromedio() != a.calcularPromedio()) {
            return c.calcularPromedio() > a.calcularPromedio();
        }
        return c.cantidadEvaluaciones() > a.cantidadEvaluaciones();
    }

    public TipoSeccion getTipo() {
        return tipo;
    }
    public Premio getPremio() {
        return premio;
    }
    // CORRECCION I13: la lista no se puede modificar desde afuera.
    public List<Pelicula> getPeliculas() {
        return Collections.unmodifiableList(peliculas);
    }

    @Override
    public String toString() {
        return "Seccion: " + tipo;
    }
}

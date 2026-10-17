package servicios;

import excepciones.EntidadDuplicadaException;
import excepciones.EntidadNoEncontradaException;
import excepciones.ReglaNegocioException;
import modelo.festival.Edicion;
import modelo.festival.Festival;
import modelo.festival.Premio;
import modelo.festival.Sala;
import modelo.festival.Seccion;
import modelo.festival.TipoSeccion;
import modelo.peliculas.Pelicula;
import repositorio.Repositorio;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// Ediciones, secciones (con su premio) y salas. Las reglas de cada una viven en el modelo
// (Edicion valida año y fechas, Edicion no repite secciones, Festival no repite ediciones)
public class FestivalServicio {
    private final Festival festival;
    private final Repositorio<Sala> salas;
    private final Repositorio<Pelicula> peliculas;

    public FestivalServicio(Festival festival, Repositorio<Sala> salas, Repositorio<Pelicula> peliculas) {
        this.festival = festival;
        this.salas = salas;
        this.peliculas = peliculas;
    }

    public Edicion registrarEdicion(int anio, String ciudad, LocalDate fechaInicio, LocalDate fechaFin) {
        Edicion edicion = new Edicion(anio, ciudad, fechaInicio, fechaFin);
        festival.agregarEdicion(edicion);
        return edicion;
    }

    public Seccion registrarSeccion(int anioEdicion, TipoSeccion tipo, String nombrePremio, String descripcionPremio) {
        Premio premio = new Premio(nombrePremio, descripcionPremio);
        return buscarEdicion(anioEdicion).agregarSeccion(tipo, premio);
    }

    public Sala registrarSala(int numero, int capacidad) {
        Sala sala = Sala.numerada(numero, capacidad);
        if (salas.existe(sala::equals)) {
            throw new EntidadDuplicadaException("Ya existe la " + sala.getNombre() + ".");
        }
        salas.agregar(sala);
        return sala;
    }

    // Una película compite en una sola edición (puede estar en varias secciones de esa edición)
    public void asociarPeliculaASeccion(int anioEdicion, TipoSeccion tipo, Pelicula pelicula) {
        Seccion seccion = buscarSeccion(anioEdicion, tipo);
        Optional<Edicion> otraEdicion = buscarEdicionDePelicula(pelicula).filter(edicion -> edicion.getAnio() != anioEdicion);
        if (otraEdicion.isPresent()) {
            throw new ReglaNegocioException(pelicula.getTitulo() + " ya compite en la edición " + otraEdicion.get().getAnio() + ".");
        }
        seccion.agregarPelicula(pelicula);
    }

    // Edicion controla que una película con funciones no deje de competir en ella
    public void quitarPeliculaDeSeccion(int anioEdicion, TipoSeccion tipo, Pelicula pelicula) {
        buscarEdicion(anioEdicion).quitarDeSeccion(tipo, pelicula);
    }

    // Películas que se pueden sumar a la sección: no están en ella y no compiten en otra edición
    public List<Pelicula> peliculasAsociables(Edicion edicion, Seccion seccion) {
        return peliculas.listar().stream()
                .filter(pelicula -> buscarEdicionDePelicula(pelicula).map(otra -> otra.equals(edicion)).orElse(true))
                .filter(pelicula -> !seccion.contiene(pelicula))
                .toList();
    }

    public Optional<Edicion> buscarEdicionDePelicula(Pelicula pelicula) {
        return festival.buscarEdicionDe(pelicula);
    }

    public Edicion buscarEdicion(int anio) {
        return festival.buscarEdicion(anio)
                .orElseThrow(() -> new EntidadNoEncontradaException("Edicion", "anio: " + anio));
    }

    public Seccion buscarSeccion(int anioEdicion, TipoSeccion tipo) {
        return buscarEdicion(anioEdicion).buscarSeccion(tipo)
                .orElseThrow(() -> new EntidadNoEncontradaException("Seccion", "tipo: " + tipo));
    }

    public Sala buscarSala(String nombre) {
        return salas.obtener(sala -> sala.seLlama(nombre), "Sala", "nombre: " + nombre);
    }

    public List<Sala> getSalas() { return salas.listar(); }
    public List<Edicion> getEdiciones() { return festival.getEdiciones(); }
}

package modelo.personas;

import excepciones.ReglaNegocioException;
import modelo.abonos.Abono;
import modelo.funciones.Entrada;
import modelo.peliculas.Pelicula;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Espectador extends Persona {
    private final List<Entrada> entradas;
    private final List<Abono> abonos;

    public Espectador(String nombre, String email, String nacionalidad) {
        super(nombre, email, nacionalidad);
        this.entradas = new ArrayList<>();
        this.abonos = new ArrayList<>();
    }

    // Entradas y abonos se registran solos al emitirse (Entrada.vincular y Abono.vincular): ningún servicio
    // llama a estos métodos. Son públicos porque Entrada y Abono están en otros paquetes;
    // por eso solo aceptan lo que pertenece a este espectador
    public void agregarEntrada(Entrada entrada) {
        validarQueEsSuyo(entrada.getEspectador());
        entradas.add(entrada);
    }

    public void quitarEntrada(Entrada entrada) {
        entradas.remove(entrada);
    }

    public void agregarAbono(Abono abono) {
        validarQueEsSuyo(abono.getEspectador());
        abonos.add(abono);
    }

    private void validarQueEsSuyo(Espectador titular) {
        if (!equals(titular)) {
            throw new ReglaNegocioException("La entrada o el abono pertenece a otro espectador.");
        }
    }

    // Un espectador puede votar una película si tiene entrada para alguna de sus funciones
    public boolean tieneEntradaPara(Pelicula pelicula) {
        return entradas.stream().anyMatch(entrada -> entrada.esPara(pelicula));
    }

    public List<Pelicula> getPeliculasVistas() {
        return entradas.stream().map(Entrada::getPelicula).distinct().toList();
    }

    public int cantidadEntradas() {
        return entradas.size();
    }

    public List<Entrada> getEntradas() {
        return Collections.unmodifiableList(entradas);
    }

    public List<Abono> getAbonos() {
        return Collections.unmodifiableList(abonos);
    }

    @Override
    protected String getRol() {
        return "Espectador";
    }
}

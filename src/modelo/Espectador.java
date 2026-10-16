package modelo;

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

    void agregarEntrada(Entrada entrada) {
        entradas.add(entrada);
    }

    void quitarEntrada(Entrada entrada) {
        entradas.remove(entrada);
    }

    public void agregarAbono(Abono abono) {
        abonos.add(abono);
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

package modelo;

import java.util.ArrayList;
import java.util.List;

public class Espectador extends Persona {
    private List<Entrada> entradas;
    private List<Abono> abonos;

    public Espectador(String nombre, String email, String nacionalidad) {
        super(nombre, email, nacionalidad);
        this.entradas = new ArrayList<>();
        this.abonos = new ArrayList<>();
    }

    public void agregarEntrada(Entrada e) {
        entradas.add(e);
    }

    public void quitarEntrada(Entrada e) {
        entradas.remove(e);
    }

    public void agregarAbono(Abono a) {
        abonos.add(a);
    }

    // Un espectador puede votar una película si tiene entrada para alguna de sus funciones
    public boolean tieneEntradaPara(Pelicula pelicula) {
        for (Entrada e : entradas) {
            if (e.getFuncion().getPelicula() == pelicula) return true;
        }
        return false;
    }

    public List<Entrada> getEntradas() {
        return entradas;
    }
    public List<Abono> getAbonos() {
        return abonos;
    }

    @Override
    public String toString() {
        return "Espectador: " + getNombre();
    }
}

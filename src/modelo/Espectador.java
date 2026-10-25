package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Espectador extends Persona {
    private List<Entrada> entradas;
    private List<Abono> abonos;

    public Espectador(String nombre, String email, String nacionalidad) {
        super(nombre, email, nacionalidad);
        this.entradas = new ArrayList<>();
        this.abonos = new ArrayList<>();
    }

    // CORRECCIoN I14: solo los llaman Entrada.vincular/desvincular y Abono.vincular.
    void agregarEntrada(Entrada e) {
        if (!entradas.contains(e)) entradas.add(e);
    }
    void quitarEntrada(Entrada e) {
        entradas.remove(e);
    }
    void agregarAbono(Abono a) {
        if (!abonos.contains(a)) abonos.add(a);
    }

    public boolean tieneEntradaPara(Pelicula pelicula) {
        for (Entrada e : entradas) {
            // CORRECCION S13/S15: la entrada responde si es para la película.
            if (e.esPara(pelicula)) return true;
        }
        return false;
    }

    // CORRECCION I13: las listas no se pueden modificar desde afuera.
    public List<Entrada> getEntradas() {
        return Collections.unmodifiableList(entradas);
    }
    public List<Abono> getAbonos() {
        return Collections.unmodifiableList(abonos);
    }

    @Override
    public String toString() {
        return "Espectador: " + getNombre();
    }
}

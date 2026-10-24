package modelo;

import java.util.ArrayList;
import java.util.List;

public class Festival {
    private String nombre;
    private List<Edicion> ediciones;

    public Festival(String nombre) {
        this.nombre = nombre;
        this.ediciones = new ArrayList<>();
    }

    public void agregarEdicion(Edicion e) {
        ediciones.add(e);
    }

    public Edicion buscarEdicion(int anio) {
        for (Edicion e : ediciones) {
            if (e.getAnio() == anio) return e;
        }
        return null;
    }

    public String getNombre() {
        return nombre;
    }
    public List<Edicion> getEdiciones() {
        return ediciones;
    }

    @Override
    public String toString() {
        return "Festival: " + nombre;
    }
}
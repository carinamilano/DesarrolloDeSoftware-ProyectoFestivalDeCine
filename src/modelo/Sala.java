package modelo;

import excepciones.DatoInvalidoException;

public class Sala {
    private String nombre;
    private int cantidadButacas;

    public Sala(String nombre, int cantidadButacas) {
        // CORRECCION I9: antes solo lo validaba PanelSalas.
        this.nombre = Validar.texto(nombre, "El nombre de la sala es obligatorio.");
        if (cantidadButacas <= 0) {
            throw new DatoInvalidoException("La capacidad debe ser mayor a 0.");
        }
        this.cantidadButacas = cantidadButacas;
    }

    public String getNombre() {
        return nombre;
    }
    public int getCantidadButacas() {
        return cantidadButacas;
    }

    // CORRECCION S15: el nombre identifica a la sala.
    @Override
    public boolean equals(Object otro) {
        if (this == otro) return true;
        if (!(otro instanceof Sala)) return false;
        return nombre.equalsIgnoreCase(((Sala) otro).nombre);
    }

    @Override
    public int hashCode() {
        return nombre.toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return "Sala: " + nombre + " (capacidad: " + cantidadButacas + ")";
    }
}

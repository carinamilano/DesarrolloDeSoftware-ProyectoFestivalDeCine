package modelo;

public class Sala {
    private String nombre;
    private int cantidadButacas;

    public Sala(String nombre, int cantidadButacas) {
        this.nombre = nombre;
        this.cantidadButacas = cantidadButacas;
    }

    public String getNombre() {
        return nombre;
    }
    public int getCantidadButacas() {
        return cantidadButacas;
    }

    @Override
    public String toString() {
        return "Sala: " + nombre + " (capacidad: " + cantidadButacas + ")";
    }
}
package modelo.festival;

import excepciones.DatoInvalidoException;
import modelo.comun.Validar;

import java.util.Locale;

public class Sala {
    private final String nombre;
    private final int cantidadButacas;

    public Sala(String nombre, int cantidadButacas) {
        if (cantidadButacas <= 0) {
            throw new DatoInvalidoException("La capacidad de la sala debe ser mayor a 0.");
        }
        this.nombre = Validar.texto(nombre, "El nombre de la sala es obligatorio.");
        this.cantidadButacas = cantidadButacas;
    }

    // Las salas se identifican por número y se registran como "Sala N"
    public static Sala numerada(int numero, int cantidadButacas) {
        if (numero <= 0) {
            throw new DatoInvalidoException("El número de sala debe ser mayor a 0.");
        }
        return new Sala("Sala " + numero, cantidadButacas);
    }

    // Las butacas se numeran de 1 a la capacidad de la sala
    public boolean esButacaValida(int numeroButaca) {
        return numeroButaca >= 1 && numeroButaca <= cantidadButacas;
    }

    public boolean estaLlena(int butacasOcupadas) {
        return butacasOcupadas >= cantidadButacas;
    }

    public double porcentajeOcupado(int butacasOcupadas) {
        return (double) butacasOcupadas / cantidadButacas * 100;
    }

    public boolean seLlama(String otroNombre) {
        return otroNombre != null && nombre.equalsIgnoreCase(otroNombre.trim());
    }

    public String getNombre() {
        return nombre;
    }
    public int getCantidadButacas() {
        return cantidadButacas;
    }

    // Identidad: el nombre (no puede haber dos salas con el mismo nombre)
    @Override
    public boolean equals(Object otro) {
        return otro instanceof Sala sala && nombre.equalsIgnoreCase(sala.nombre);
    }

    @Override
    public int hashCode() {
        return nombre.toLowerCase(Locale.ROOT).hashCode();
    }

    @Override
    public String toString() {
        return "Sala: " + nombre + " (capacidad: " + cantidadButacas + ")";
    }
}

package modelo;

import excepciones.ButacaOcupadaException;
import excepciones.CapacidadExcedidaException;
import excepciones.DatoInvalidoException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FuncionPresencial extends Funcion {
    public static final double PRECIO_BASE = 8000;

    private Sala sala;
    private List<Integer> butacasOcupadas;

    public FuncionPresencial(String codigo, LocalDate fecha, LocalTime horario, Pelicula pelicula, Sala sala) {
        super(codigo, fecha, horario, pelicula);
        this.sala = Validar.presente(sala, "La sala es obligatoria.");
        this.butacasOcupadas = new ArrayList<>();
    }

    @Override
    public boolean verificarDisponibilidad() {
        return butacasOcupadas.size() < sala.getCantidadButacas();
    }

    // CORRECCION I9: la función valida la butaca antes de ocuparla (antes lo hacía EntradaServicio).
    public void ocuparButaca(int numeroButaca) {
        if (numeroButaca < 1 || numeroButaca > sala.getCantidadButacas()) {
            throw new DatoInvalidoException("Número de butaca inválido. Debe estar entre 1 y " + sala.getCantidadButacas() + ".");
        }
        if (!verificarDisponibilidad()) {
            throw new CapacidadExcedidaException(getPelicula().getTitulo());
        }
        if (!butacaDisponible(numeroButaca)) {
            throw new ButacaOcupadaException(numeroButaca);
        }
        butacasOcupadas.add(numeroButaca);
    }

    public void liberarButaca(int numeroButaca) {
        butacasOcupadas.remove(Integer.valueOf(numeroButaca));
    }

    public boolean butacaDisponible(int numeroButaca) {
        return !butacasOcupadas.contains(numeroButaca);
    }

    public boolean usaSala(Sala otra) {
        return sala.equals(otra);
    }

    @Override
    public double calcularOcupacion() {
        return (double) butacasOcupadas.size() / sala.getCantidadButacas() * 100;
    }

    // CORRECCION S3: respuestas propias de la función presencial.
    @Override
    public double getPrecioBase() {
        return PRECIO_BASE;
    }

    @Override
    public String getTipo() {
        return "Presencial";
    }

    @Override
    public String getLugar() {
        return sala.getNombre();
    }

    @Override
    public boolean requiereButaca() {
        return true;
    }

    public Sala getSala() {
        return sala;
    }

    // CORRECCION I13: devuelve una copia ordenada que no se puede modificar.
    public List<Integer> getButacasOcupadas() {
        List<Integer> copia = new ArrayList<>(butacasOcupadas);
        Collections.sort(copia);
        return Collections.unmodifiableList(copia);
    }

    @Override
    public String toString() {
        return "Presencial - " + super.toString() + " - Sala: " + sala.getNombre();
    }
}

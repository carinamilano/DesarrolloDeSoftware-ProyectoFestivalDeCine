package modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class Abono {
    private String codigo;
    private Espectador espectador;
    private TipoTarifa tarifa;
    private double precio;
    private LocalDate fechaCompra;
    private List<Entrada> reservas;

    public Abono(String codigo, Espectador espectador, TipoTarifa tarifa, double precio) {
        this.codigo = codigo;
        this.espectador = espectador;
        this.tarifa = tarifa;
        this.precio = precio;
        this.fechaCompra = LocalDate.now();
        this.reservas = new ArrayList<>();
    }

    // Cada tipo de abono define qué funciones cubre
    public abstract boolean cubre(Funcion funcion);
    public abstract String getTipo();
    public abstract String getCobertura();

    public void agregarReserva(Entrada e) {
        reservas.add(e);
    }

    public void quitarReserva(Entrada e) {
        reservas.remove(e);
    }

    public boolean tieneReservaEn(LocalDate fecha, java.time.LocalTime horario) {
        for (Entrada e : reservas) {
            if (e.getFuncion().getFecha().equals(fecha) && e.getFuncion().getHorario().equals(horario)) {
                return true;
            }
        }
        return false;
    }

    public String getCodigo() {
        return codigo;
    }
    public Espectador getEspectador() {
        return espectador;
    }
    public TipoTarifa getTarifa() {
        return tarifa;
    }
    public double getPrecio() {
        return precio;
    }
    public LocalDate getFechaCompra() {
        return fechaCompra;
    }
    public List<Entrada> getReservas() {
        return reservas;
    }

    @Override
    public String toString() {
        return codigo + " - " + getTipo() + " - " + espectador.getNombre();
    }
}

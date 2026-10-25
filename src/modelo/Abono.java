package modelo;

import excepciones.DatoInvalidoException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Abono {
    private String codigo;
    private Espectador espectador;
    private TipoTarifa tarifa;
    private double precio;
    private LocalDate fechaCompra;
    private List<Entrada> reservas;

    public Abono(String codigo, Espectador espectador, TipoTarifa tarifa, double precio, LocalDate fechaCompra) {

        // CORRECCION I9: datos obligatorios y precio no negativo.

        this.codigo = Validar.texto(codigo, "El código del abono es obligatorio.");
        this.espectador = Validar.presente(espectador, "El espectador es obligatorio.");
        this.tarifa = Validar.presente(tarifa, "La tarifa es obligatoria.");
        if (precio < 0) {
            throw new DatoInvalidoException("El precio no puede ser negativo.");
        }
        this.precio = precio;
        this.fechaCompra = Validar.presente(fechaCompra, "La fecha de compra es obligatoria.");
        this.reservas = new ArrayList<>();
    }

    // CORRECCION I14: agrega el abono a su espectador. Lo llama el servicio una vez que el abono es valido
    public void vincular() {
        espectador.agregarAbono(this);
    }

    public abstract boolean cubre(Funcion funcion);

    // CORRECCION S4: Con el objetivo de evitar el uso de String, el tipo es un enum ahora
    public abstract TipoAbono getTipo();
    public abstract String getCobertura();
    public List<LocalDate> getDiasElegidos() {
        return Collections.emptyList();
    }
    // CORRECCION I14: solo los llama Entrada.vincular/desvincular.
    void agregarReserva(Entrada e) {
        if (!reservas.contains(e)) reservas.add(e);
    }
    void quitarReserva(Entrada e) {
        reservas.remove(e);
    }

    // CORRECCION I5: controla la superposición con la duración de cada función, no la hora exacta.
    public boolean tieneReservaSuperpuestaCon(Funcion funcion) {
        for (Entrada e : reservas) {
            if (e.getFuncion().seSuperponeCon(funcion)) {
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
    // CORRECCION I13: la lista no se puede modificar desde afuera.
    public List<Entrada> getReservas() {
        return Collections.unmodifiableList(reservas);
    }

    // CORRECCION S13: el código identifica al abono.
    @Override
    public boolean equals(Object otro) {
        if (this == otro) return true;
        if (!(otro instanceof Abono)) return false;
        return codigo.equals(((Abono) otro).codigo);
    }

    @Override
    public int hashCode() {
        return codigo.hashCode();
    }

    @Override
    public String toString() {
        return codigo + " - " + getTipo() + " - " + espectador.getNombre();
    }
}

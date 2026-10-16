package modelo;

import excepciones.AbonoInvalidoException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Abono {
    private final String codigo;
    private final Espectador espectador;
    private final TipoTarifa tarifa;
    private final TipoAbono tipo;
    private final double precio;
    private final List<Entrada> reservas;

    protected Abono(String codigo, Espectador espectador, TipoTarifa tarifa, TipoAbono tipo) {
        this.codigo = Validar.texto(codigo, "El código del abono es obligatorio.");
        this.espectador = Validar.presente(espectador, "El espectador es obligatorio.");
        this.tarifa = Validar.presente(tarifa, "La tarifa es obligatoria.");
        this.tipo = tipo;
        this.precio = tipo.precioCon(tarifa);
        this.reservas = new ArrayList<>();
    }

    public abstract String getCobertura();

    // Cada tipo de abono define qué funciones cubre. La función resuelve de qué tipo es
    // (doble despacho), así no hace falta preguntar con instanceof
    public boolean cubre(Funcion funcion) {
        return funcion.esCubiertaPor(this);
    }

    protected boolean cubrePresencial(FuncionPresencial funcion) {
        return false;
    }

    protected boolean cubreStreaming(FuncionStreaming funcion) {
        return false;
    }

    public void validarReserva(Funcion funcion) {
        if (funcion.yaPaso()) {
            throw new AbonoInvalidoException("la función ya pasó.");
        }
        if (!cubre(funcion)) {
            throw new AbonoInvalidoException("el " + tipo + " no cubre la función del " + funcion.getFecha() + ".");
        }
        if (tieneReservaEnElHorarioDe(funcion)) {
            throw new AbonoInvalidoException("ya tiene una reserva el " + funcion.getFecha() + " a las " + funcion.getHorario() + ".");
        }
    }

    private boolean tieneReservaEnElHorarioDe(Funcion funcion) {
        return reservas.stream().anyMatch(reserva -> reserva.esEnElHorarioDe(funcion));
    }

    void agregarReserva(Entrada reserva) {
        reservas.add(reserva);
    }

    void quitarReserva(Entrada reserva) {
        reservas.remove(reserva);
    }

    public boolean tieneReservas() {
        return !reservas.isEmpty();
    }

    public int cantidadReservas() {
        return reservas.size();
    }

    public String getCodigo() {
        return codigo;
    }
    public Espectador getEspectador() {
        return espectador;
    }
    public String getNombreEspectador() {
        return espectador.getNombre();
    }
    public TipoTarifa getTarifa() {
        return tarifa;
    }
    public TipoAbono getTipo() {
        return tipo;
    }
    public double getPrecio() {
        return precio;
    }
    public List<Entrada> getReservas() {
        return Collections.unmodifiableList(reservas);
    }

    @Override
    public String toString() {
        return codigo + " - " + tipo + " - " + getNombreEspectador();
    }
}

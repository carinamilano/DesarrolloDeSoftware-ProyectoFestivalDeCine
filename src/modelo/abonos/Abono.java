package modelo.abonos;

import excepciones.AbonoInvalidoException;
import excepciones.ReglaNegocioException;
import modelo.comun.Validar;
import modelo.funciones.Entrada;
import modelo.funciones.Funcion;
import modelo.funciones.FuncionPresencial;
import modelo.funciones.FuncionStreaming;
import modelo.personas.Espectador;
import modelo.precios.Dinero;
import modelo.precios.TipoTarifa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class Abono {
    private final String codigo;
    private final Espectador espectador;
    private final TipoTarifa tarifa;
    private final TipoAbono tipo;
    private final Dinero precio;
    private final List<Entrada> reservas;

    // El precio lo calcula el Tarifario, igual que el de las entradas (antes el abono aplicaba la tarifa por su cuenta)
    protected Abono(String codigo, Espectador espectador, TipoTarifa tarifa, TipoAbono tipo, Dinero precio) {
        this.codigo = Validar.texto(codigo, "El código del abono es obligatorio.");
        this.espectador = Validar.presente(espectador, "El espectador es obligatorio.");
        this.tarifa = Validar.presente(tarifa, "La tarifa es obligatoria.");
        this.tipo = Validar.presente(tipo, "El tipo de abono es obligatorio.");
        this.precio = Validar.presente(precio, "El precio es obligatorio.");
        this.reservas = new ArrayList<>();
    }

    // El abono se registra solo en su espectador al emitirse, igual que Entrada.vincular
    // (antes lo registraba el servicio llamando a espectador.agregarAbono)
    protected static <A extends Abono> A vincular(A abono) {
        abono.getEspectador().agregarAbono(abono);
        return abono;
    }

    public abstract String getCobertura();

    // Días que eligió el espectador (solo el abono de 3 días elige días)
    public List<LocalDate> getDiasElegidos() {
        return List.of();
    }

    // Cada tipo de abono define qué funciones cubre. La función resuelve de qué tipo es
    // (doble despacho), así no hace falta preguntar con instanceof.
    // Son públicos porque los llaman las funciones, que están en otro paquete
    public boolean cubre(Funcion funcion) {
        return funcion.esCubiertaPor(this);
    }

    public boolean cubrePresencial(FuncionPresencial funcion) {
        return false;
    }

    public boolean cubreStreaming(FuncionStreaming funcion) {
        return false;
    }

    // Que la función no haya comenzado lo controla Funcion.emitirEntrada (vale para ventas y reservas)
    public void validarReserva(Funcion funcion) {
        if (!cubre(funcion)) {
            throw new AbonoInvalidoException("el " + tipo + " no cubre la función del " + funcion.getFecha() + ".");
        }
        Optional<Entrada> superpuesta = reservas.stream().filter(reserva -> reserva.seSuperponeCon(funcion)).findFirst();
        if (superpuesta.isPresent()) {
            throw new AbonoInvalidoException("se superpone con otra reserva del abono ("
                    + superpuesta.get().getFuncion().getResumen() + ").");
        }
    }

    // Lo llama Entrada.vincular. Es público porque Entrada está en otro paquete;
    // por eso solo acepta reservas hechas con este abono
    public void agregarReserva(Entrada reserva) {
        if (!reserva.esDelAbono(this)) {
            throw new ReglaNegocioException("La reserva " + reserva.getCodigo() + " no es del abono " + codigo + ".");
        }
        reservas.add(reserva);
    }

    public void quitarReserva(Entrada reserva) {
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
    public Dinero getPrecio() {
        return precio;
    }
    public List<Entrada> getReservas() {
        return Collections.unmodifiableList(reservas);
    }

    // Identidad: el código del abono
    @Override
    public boolean equals(Object otro) {
        return otro instanceof Abono abono && codigo.equals(abono.codigo);
    }

    @Override
    public int hashCode() {
        return codigo.hashCode();
    }

    @Override
    public String toString() {
        return codigo + " - " + tipo + " - " + getNombreEspectador();
    }
}

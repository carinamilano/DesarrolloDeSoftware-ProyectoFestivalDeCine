package modelo.funciones;

import excepciones.ReglaNegocioException;
import modelo.abonos.Abono;
import modelo.comun.Validar;
import modelo.peliculas.Pelicula;
import modelo.personas.Espectador;
import modelo.precios.Dinero;

import java.util.Optional;

public abstract class Entrada {
    private final String codigo;
    private final Espectador espectador;
    private final Dinero precio;
    private final Optional<Abono> abono; // vacío si es una entrada suelta (antes era null)

    protected Entrada(String codigo, SolicitudEntrada solicitud) {
        this.codigo = Validar.texto(codigo, "El código de la entrada es obligatorio.");
        this.espectador = solicitud.espectador();
        this.precio = solicitud.precio();
        this.abono = solicitud.abono();
    }

    // Cada subclase conoce su tipo concreto de función (retorno covariante, sin casteos)
    public abstract Funcion getFuncion();
    public abstract String getDetalle();
    protected abstract void liberarLugar();

    // Las entradas presenciales tienen butaca; las de streaming no
    public Optional<Butaca> getButacaAsignada() {
        return Optional.empty();
    }

    // Registra la entrada en su espectador y, si es una reserva, en su abono
    void vincular() {
        espectador.agregarEntrada(this);
        abono.ifPresent(abonoDeLaReserva -> abonoDeLaReserva.agregarReserva(this));
    }

    // Solo se cancelan reservas hechas con abono y de funciones que no comenzaron
    public void cancelar() {
        Abono abonoDeLaReserva = abono.orElseThrow(
                () -> new ReglaNegocioException("Solo se pueden cancelar reservas hechas con abono."));
        if (getFuncion().yaPaso()) {
            throw new ReglaNegocioException("No se puede cancelar una reserva de una función que ya comenzó.");
        }
        liberarLugar();
        espectador.quitarEntrada(this);
        abonoDeLaReserva.quitarReserva(this);
    }

    public boolean esReservaDeAbono() {
        return abono.isPresent();
    }

    public boolean esDelAbono(Abono otro) {
        return abono.filter(otro::equals).isPresent();
    }

    public boolean esPara(Pelicula pelicula) {
        return getFuncion().esDe(pelicula);
    }

    public boolean seSuperponeCon(Funcion funcion) {
        return getFuncion().seSuperponeEnHorarioCon(funcion);
    }

    public Pelicula getPelicula() {
        return getFuncion().getPelicula();
    }

    public String getCodigo() {
        return codigo;
    }
    public Espectador getEspectador() {
        return espectador;
    }
    public Dinero getPrecio() {
        return precio;
    }
    public Optional<Abono> getAbono() {
        return abono;
    }

    // Identidad: el código de la entrada
    @Override
    public boolean equals(Object otro) {
        return otro instanceof Entrada entrada && codigo.equals(entrada.codigo);
    }

    @Override
    public int hashCode() {
        return codigo.hashCode();
    }

    @Override
    public String toString() {
        return "Entrada: " + codigo + " - " + getFuncion().getResumen() + " - " + getDetalle();
    }
}

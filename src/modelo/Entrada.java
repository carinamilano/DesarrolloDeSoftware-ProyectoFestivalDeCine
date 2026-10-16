package modelo;

import excepciones.FestivalException;

public abstract class Entrada {
    private final String codigo;
    private final Espectador espectador;
    private final double precio;
    private final Abono abono; // null si es una entrada suelta

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

    // Registra la entrada en su espectador y, si es una reserva, en su abono
    void vincular() {
        espectador.agregarEntrada(this);
        if (esReservaDeAbono()) abono.agregarReserva(this);
    }

    // Solo se cancelan reservas hechas con abono y de funciones que no pasaron
    public void cancelar() {
        if (!esReservaDeAbono()) {
            throw new FestivalException("Solo se pueden cancelar reservas hechas con abono.");
        }
        if (getFuncion().yaPaso()) {
            throw new FestivalException("No se puede cancelar una reserva de una función que ya pasó.");
        }
        liberarLugar();
        espectador.quitarEntrada(this);
        abono.quitarReserva(this);
    }

    public boolean esReservaDeAbono() {
        return abono != null;
    }

    public boolean esPara(Pelicula pelicula) {
        return getFuncion().esDe(pelicula);
    }

    public boolean esEnElHorarioDe(Funcion funcion) {
        return getFuncion().coincideEnHorarioCon(funcion);
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
    public double getPrecio() {
        return precio;
    }
    public Abono getAbono() {
        return abono;
    }

    @Override
    public String toString() {
        return "Entrada: " + codigo + " - " + getFuncion().getResumen() + " - " + getDetalle();
    }
}

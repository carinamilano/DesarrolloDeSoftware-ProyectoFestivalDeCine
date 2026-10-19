package modelo.funciones;

import excepciones.CapacidadExcedidaException;
import excepciones.DatoInvalidoException;
import modelo.abonos.Abono;
import modelo.comun.Validar;
import modelo.peliculas.Pelicula;
import modelo.precios.Dinero;

import java.time.Clock;
import java.time.LocalDateTime;

public class FuncionStreaming extends Funcion {
    public static final Dinero PRECIO_BASE = Dinero.de(5000);
    public static final int CAPACIDAD_MAXIMA = 1000;

    private final String enlace;
    private final int capacidadVirtual;
    private int espectadoresConectados;

    public FuncionStreaming(String codigo, Pelicula pelicula, String enlace, int capacidadVirtual,
                            LocalDateTime fechaHora, Clock reloj) {
        super(codigo, pelicula, fechaHora, reloj);
        if (capacidadVirtual < 1 || capacidadVirtual > CAPACIDAD_MAXIMA) {
            throw new DatoInvalidoException("La capacidad debe estar entre 1 y " + CAPACIDAD_MAXIMA + ".");
        }
        this.enlace = Validar.texto(enlace, "El enlace es obligatorio.");
        this.capacidadVirtual = capacidadVirtual;
        this.espectadoresConectados = 0;
    }

    @Override
    public boolean verificarDisponibilidad() {
        return espectadoresConectados < capacidadVirtual;
    }

    @Override
    protected void ocuparLugar(SolicitudEntrada solicitud) {
        if (solicitud.butaca().isPresent()) {
            throw new DatoInvalidoException("Las funciones streaming no tienen butacas.");
        }
        if (!verificarDisponibilidad()) {
            throw new CapacidadExcedidaException(getTituloPelicula());
        }
        espectadoresConectados++;
    }

    @Override
    protected Entrada crearEntrada(String codigoEntrada, SolicitudEntrada solicitud) {
        return new EntradaStreaming(codigoEntrada, this, solicitud);
    }

    @Override
    protected String getPrefijoEntrada() {
        return "ES-";
    }

    void desconectarEspectador() {
        if (espectadoresConectados > 0) espectadoresConectados--;
    }

    @Override
    public boolean esCubiertaPor(Abono abono) {
        return abono.cubreStreaming(this);
    }

    @Override
    public double calcularOcupacion() {
        return (double) espectadoresConectados / capacidadVirtual * 100;
    }

    @Override
    public Dinero getPrecioBase() {
        return PRECIO_BASE;
    }

    @Override
    public boolean requiereButaca() {
        return false;
    }

    @Override
    public String getModalidad() {
        return "Streaming";
    }

    @Override
    public String getLugar() {
        return enlace;
    }

    @Override
    public String getEstadoOcupacion() {
        return espectadoresConectados + " de " + capacidadVirtual + " conectados (sin butaca)";
    }

    public String getEnlace() {
        return enlace;
    }
    public int getCapacidadVirtual() {
        return capacidadVirtual;
    }
}

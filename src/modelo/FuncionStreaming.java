package modelo;

import excepciones.CapacidadExcedidaException;
import excepciones.FestivalException;

import java.time.LocalDateTime;

public class FuncionStreaming extends Funcion {
    public static final double PRECIO_BASE = 5000;
    public static final int CAPACIDAD_MAXIMA = 1000;

    private final String enlace;
    private final int capacidadVirtual;
    private int espectadoresConectados;

    public FuncionStreaming(Pelicula pelicula, String enlace, int capacidadVirtual, LocalDateTime fechaHora) {
        super(pelicula, fechaHora);
        if (capacidadVirtual < 1 || capacidadVirtual > CAPACIDAD_MAXIMA) {
            throw new FestivalException("La capacidad debe estar entre 1 y " + CAPACIDAD_MAXIMA + ".");
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
        if (!verificarDisponibilidad()) {
            throw new CapacidadExcedidaException(getTituloPelicula());
        }
        espectadoresConectados++;
    }

    @Override
    protected Entrada crearEntrada(Numerador numerador, SolicitudEntrada solicitud) {
        return new EntradaStreaming("ES-" + numerador.siguiente(), this, solicitud);
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
    public double getPrecioBase() {
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
}

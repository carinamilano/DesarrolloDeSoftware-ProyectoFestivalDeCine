package modelo;

import excepciones.CapacidadExcedidaException;
import excepciones.DatoInvalidoException;

import java.time.LocalDate;
import java.time.LocalTime;

public class FuncionStreaming extends Funcion {
    public static final double PRECIO_BASE = 5000;
    public static final int CAPACIDAD_MAXIMA = 1000;

    private String enlace;
    private int capacidadVirtual;
    private int espectadoresConectados;

    public FuncionStreaming(String codigo, LocalDate fecha, LocalTime horario, Pelicula pelicula,
                            String enlace, int capacidadVirtual) {
        super(codigo, fecha, horario, pelicula);
        this.enlace = Validar.texto(enlace, "El enlace es obligatorio.");
        // CORRECCION I9: antes solo lo controlaba PanelFunciones; con 0 la ocupación daba NaN%.
        if (capacidadVirtual < 1 || capacidadVirtual > CAPACIDAD_MAXIMA) {
            throw new DatoInvalidoException("La capacidad virtual debe estar entre 1 y " + CAPACIDAD_MAXIMA + ".");
        }
        this.capacidadVirtual = capacidadVirtual;
        this.espectadoresConectados = 0;
    }

    @Override
    public boolean verificarDisponibilidad() {
        return espectadoresConectados < capacidadVirtual;
    }

    // CORRECCION I9: la función controla su capacidad (antes lo hacía EntradaServicio).
    public void agregarEspectador() {
        if (!verificarDisponibilidad()) {
            throw new CapacidadExcedidaException(getPelicula().getTitulo());
        }
        espectadoresConectados++;
    }

    public void quitarEspectador() {
        if (espectadoresConectados > 0) espectadoresConectados--;
    }

    @Override
    public double calcularOcupacion() {
        return (double) espectadoresConectados / capacidadVirtual * 100;
    }

    // CORRECCION S3: respuestas propias de la función streaming.
    @Override
    public double getPrecioBase() {
        return PRECIO_BASE;
    }

    @Override
    public TipoFuncion getTipo() {
        return TipoFuncion.STREAMING;
    }

    @Override
    public String getLugar() {
        return enlace;
    }

    @Override
    public boolean requiereButaca() {
        return false;
    }

    public String getEnlace() {
        return enlace;
    }
    public int getCapacidadVirtual() {
        return capacidadVirtual;
    }
    public int getEspectadoresConectados() {
        return espectadoresConectados;
    }

    @Override
    public String toString() {
        return getTipo() + " - " + super.toString() + " - Enlace: " + enlace;
    }
}

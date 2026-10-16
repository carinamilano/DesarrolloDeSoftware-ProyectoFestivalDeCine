package modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public abstract class Funcion {
    private final Pelicula pelicula;
    private final LocalDateTime fechaHora;

    protected Funcion(Pelicula pelicula, LocalDateTime fechaHora) {
        this.pelicula = Validar.presente(pelicula, "La película es obligatoria.");
        this.fechaHora = Validar.presente(fechaHora, "La fecha y el horario son obligatorios.");
    }

    public abstract boolean verificarDisponibilidad();
    public abstract double calcularOcupacion();
    public abstract double getPrecioBase();
    public abstract boolean requiereButaca();
    public abstract String getModalidad();
    public abstract String getLugar();
    public abstract String getEstadoOcupacion();

    // Doble despacho: la función le dice al abono qué tipo de función es, sin usar instanceof
    public abstract boolean esCubiertaPor(Abono abono);

    protected abstract void ocuparLugar(SolicitudEntrada solicitud);
    protected abstract Entrada crearEntrada(Numerador numerador, SolicitudEntrada solicitud);

    // Creator + Template Method: la función conoce su sala (o su enlace) y su ocupación,
    // así que ella valida el lugar, lo ocupa y emite la entrada
    public final Entrada emitirEntrada(Numerador numerador, SolicitudEntrada solicitud) {
        ocuparLugar(solicitud);
        Entrada entrada = crearEntrada(numerador, solicitud);
        entrada.vincular();
        return entrada;
    }

    public boolean yaPaso() {
        return getFecha().isBefore(LocalDate.now());
    }

    public boolean coincideEnHorarioCon(Funcion otra) {
        return fechaHora.equals(otra.fechaHora);
    }

    public boolean esDe(Pelicula otraPelicula) {
        return pelicula == otraPelicula;
    }

    public boolean corresponde(String tituloPelicula, LocalDateTime otraFechaHora) {
        return pelicula.seTitula(tituloPelicula) && fechaHora.equals(otraFechaHora);
    }

    public LocalDate getFecha() {
        return fechaHora.toLocalDate();
    }
    public LocalTime getHorario() {
        return fechaHora.toLocalTime();
    }
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
    public Pelicula getPelicula() {
        return pelicula;
    }
    public String getTituloPelicula() {
        return pelicula.getTitulo();
    }

    public String getResumen() {
        return getTituloPelicula() + " - " + getFecha() + " " + getHorario();
    }

    @Override
    public String toString() {
        return getModalidad() + " - " + getResumen() + " - " + getLugar();
    }
}

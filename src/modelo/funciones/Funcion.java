package modelo.funciones;

import excepciones.FuncionNoDisponibleException;
import modelo.abonos.Abono;
import modelo.comun.Numerador;
import modelo.comun.Validar;
import modelo.peliculas.Pelicula;
import modelo.precios.Dinero;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.Supplier;

public abstract class Funcion {
    private final String codigo;
    private final Pelicula pelicula;
    private final LocalDateTime fechaHora;
    // El reloj se inyecta (antes se usaba LocalDate.now() directo): con un reloj fijo se puede probar todo lo que depende de la fecha
    private final Clock reloj;

    protected Funcion(String codigo, Pelicula pelicula, LocalDateTime fechaHora, Clock reloj) {
        this.codigo = Validar.texto(codigo, "El código de la función es obligatorio.");
        this.pelicula = Validar.presente(pelicula, "La película es obligatoria.");
        this.fechaHora = Validar.presente(fechaHora, "La fecha y el horario son obligatorios.");
        this.reloj = Validar.presente(reloj, "El reloj es obligatorio.");
    }

    public abstract boolean verificarDisponibilidad();
    public abstract double calcularOcupacion();
    public abstract Dinero getPrecioBase();
    public abstract boolean requiereButaca();
    public abstract String getModalidad();
    public abstract String getLugar();
    public abstract String getEstadoOcupacion();

    // Doble despacho: la función le dice al abono qué tipo de función es, sin usar instanceof
    public abstract boolean esCubiertaPor(Abono abono);

    protected abstract void ocuparLugar(SolicitudEntrada solicitud);
    protected abstract Entrada crearEntrada(String codigoEntrada, SolicitudEntrada solicitud);
    protected abstract String getPrefijoEntrada();

    // Creator + Template Method: la función conoce su sala (o su enlace) y su ocupación,
    // así que ella valida el lugar, lo ocupa y emite la entrada.
    // Como toda venta y toda reserva pasa por acá, la regla "no se vende una función que ya comenzó"
    // queda en el modelo (antes la venta suelta no la controlaba)
    public final Entrada emitirEntrada(Numerador numerador, SolicitudEntrada solicitud) {
        if (yaPaso()) {
            throw new FuncionNoDisponibleException("la función de " + getTituloPelicula() + " del "
                    + getFecha() + " a las " + getHorario() + " ya comenzó.");
        }
        return registrarEntrada(() -> getPrefijoEntrada() + numerador.siguiente(), solicitud);
    }

    // Para la persistencia: vuelve a registrar una entrada vendida antes, aunque la función ya haya pasado
    public final Entrada restaurarEntrada(String codigoEntrada, SolicitudEntrada solicitud) {
        return registrarEntrada(() -> codigoEntrada, solicitud);
    }

    private Entrada registrarEntrada(Supplier<String> codigoEntrada, SolicitudEntrada solicitud) {
        ocuparLugar(solicitud);
        Entrada entrada = crearEntrada(codigoEntrada.get(), solicitud);
        entrada.vincular();
        return entrada;
    }

    // Compara fecha Y hora con el momento actual: antes comparaba solo la fecha
    // y una función de hoy a las 10:00 se seguía vendiendo a las 23:00
    public boolean yaPaso() {
        return !fechaHora.isAfter(LocalDateTime.now(reloj));
    }

    // La función ocupa el intervalo [inicio, fin), donde fin = inicio + duración de la película
    public LocalDateTime getFin() {
        return pelicula.terminaSiEmpiezaA(fechaHora);
    }

    // Dos intervalos se superponen si cada uno empieza antes de que termine el otro.
    // Antes solo se comparaba la hora exacta de inicio
    public boolean seSuperponeEnHorarioCon(LocalDateTime inicio, LocalDateTime fin) {
        return fechaHora.isBefore(fin) && inicio.isBefore(getFin());
    }

    public boolean seSuperponeEnHorarioCon(Funcion otra) {
        return seSuperponeEnHorarioCon(otra.fechaHora, otra.getFin());
    }

    public boolean esDe(Pelicula otraPelicula) {
        return pelicula.equals(otraPelicula);
    }

    public boolean corresponde(String tituloPelicula, LocalDateTime otraFechaHora) {
        return pelicula.seTitula(tituloPelicula) && fechaHora.equals(otraFechaHora);
    }

    public boolean tieneCodigo(String otroCodigo) {
        return codigo.equalsIgnoreCase(otroCodigo);
    }

    public String getCodigo() {
        return codigo;
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
        return getTituloPelicula() + " - " + getFecha() + " " + getHorario() + " a " + getFin().toLocalTime();
    }

    // Identidad: el código de la función
    @Override
    public boolean equals(Object otro) {
        return otro instanceof Funcion funcion && codigo.equals(funcion.codigo);
    }

    @Override
    public int hashCode() {
        return codigo.hashCode();
    }

    @Override
    public String toString() {
        return getModalidad() + " - " + getResumen() + " - " + getLugar();
    }
}

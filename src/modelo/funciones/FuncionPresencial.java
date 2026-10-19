package modelo.funciones;

import excepciones.ButacaOcupadaException;
import excepciones.CapacidadExcedidaException;
import excepciones.DatoInvalidoException;
import modelo.abonos.Abono;
import modelo.comun.Validar;
import modelo.festival.Sala;
import modelo.peliculas.Pelicula;
import modelo.precios.Dinero;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class FuncionPresencial extends Funcion {
    public static final Dinero PRECIO_BASE = Dinero.de(8000);

    private final Sala sala;
    // Set: una butaca no puede quedar ocupada dos veces
    private final Set<Butaca> butacasOcupadas;

    public FuncionPresencial(String codigo, Pelicula pelicula, Sala sala, LocalDateTime fechaHora, Clock reloj) {
        super(codigo, pelicula, fechaHora, reloj);
        this.sala = Validar.presente(sala, "La sala es obligatoria.");
        this.butacasOcupadas = new TreeSet<>();
    }

    @Override
    public boolean verificarDisponibilidad() {
        return !sala.estaLlena(butacasOcupadas.size());
    }

    public boolean butacaDisponible(Butaca butaca) {
        return !butacasOcupadas.contains(butaca);
    }

    @Override
    protected void ocuparLugar(SolicitudEntrada solicitud) {
        Butaca butaca = solicitud.butaca()
                .orElseThrow(() -> new DatoInvalidoException("Las funciones presenciales requieren elegir una butaca."));
        if (!sala.esButacaValida(butaca.numero())) {
            throw new DatoInvalidoException("Número de butaca inválido. Debe estar entre 1 y " + sala.getCantidadButacas());
        }
        if (!verificarDisponibilidad()) {
            throw new CapacidadExcedidaException(getTituloPelicula());
        }
        if (!butacaDisponible(butaca)) {
            throw new ButacaOcupadaException(butaca.numero());
        }
        butacasOcupadas.add(butaca);
    }

    @Override
    protected Entrada crearEntrada(String codigoEntrada, SolicitudEntrada solicitud) {
        return new EntradaPresencial(codigoEntrada, this, solicitud);
    }

    @Override
    protected String getPrefijoEntrada() {
        return "EP-";
    }

    void liberarButaca(Butaca butaca) {
        butacasOcupadas.remove(butaca);
    }

    // No puede haber dos funciones presenciales que ocupen la misma sala al mismo tiempo
    // (se tiene en cuenta la duración de las películas, no solo la hora de inicio)
    public boolean ocupaLaSala(Sala otraSala, LocalDateTime inicio, LocalDateTime fin) {
        return sala.equals(otraSala) && seSuperponeEnHorarioCon(inicio, fin);
    }

    @Override
    public boolean esCubiertaPor(Abono abono) {
        return abono.cubrePresencial(this);
    }

    @Override
    public double calcularOcupacion() {
        return sala.porcentajeOcupado(butacasOcupadas.size());
    }

    @Override
    public Dinero getPrecioBase() {
        return PRECIO_BASE;
    }

    @Override
    public boolean requiereButaca() {
        return true;
    }

    @Override
    public String getModalidad() {
        return "Presencial";
    }

    @Override
    public String getLugar() {
        return sala.getNombre();
    }

    @Override
    public String getEstadoOcupacion() {
        return butacasOcupadas.isEmpty() ? "ninguna" : butacasOcupadas.toString();
    }

    public Sala getSala() {
        return sala;
    }
    public Set<Butaca> getButacasOcupadas() {
        return Collections.unmodifiableSet(butacasOcupadas);
    }
}

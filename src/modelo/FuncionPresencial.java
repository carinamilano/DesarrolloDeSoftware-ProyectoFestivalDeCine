package modelo;

import excepciones.ButacaOcupadaException;
import excepciones.CapacidadExcedidaException;
import excepciones.FestivalException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class FuncionPresencial extends Funcion {
    public static final double PRECIO_BASE = 8000;

    private final Sala sala;
    // Set: una butaca no puede quedar ocupada dos veces (antes era una List<Integer>)
    private final Set<Integer> butacasOcupadas;

    public FuncionPresencial(Pelicula pelicula, Sala sala, LocalDateTime fechaHora) {
        super(pelicula, fechaHora);
        this.sala = Validar.presente(sala, "La sala es obligatoria.");
        this.butacasOcupadas = new TreeSet<>();
    }

    @Override
    public boolean verificarDisponibilidad() {
        return !sala.estaLlena(butacasOcupadas.size());
    }

    public boolean butacaDisponible(int numeroButaca) {
        return !butacasOcupadas.contains(numeroButaca);
    }

    @Override
    protected void ocuparLugar(SolicitudEntrada solicitud) {
        int numeroButaca = solicitud.numeroButaca();
        if (!sala.esButacaValida(numeroButaca)) {
            throw new FestivalException("Número de butaca inválido. Debe estar entre 1 y " + sala.getCantidadButacas());
        }
        if (!verificarDisponibilidad()) {
            throw new CapacidadExcedidaException(getTituloPelicula());
        }
        if (!butacaDisponible(numeroButaca)) {
            throw new ButacaOcupadaException(numeroButaca);
        }
        butacasOcupadas.add(numeroButaca);
    }

    @Override
    protected Entrada crearEntrada(Numerador numerador, SolicitudEntrada solicitud) {
        return new EntradaPresencial("EP-" + numerador.siguiente(), this, solicitud);
    }

    void liberarButaca(int numeroButaca) {
        butacasOcupadas.remove(numeroButaca);
    }

    // No puede haber dos funciones presenciales en la misma sala, fecha y horario
    public boolean seSuperponeCon(FuncionPresencial otra) {
        return sala == otra.sala && coincideEnHorarioCon(otra);
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
    public double getPrecioBase() {
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
    public Set<Integer> getButacasOcupadas() {
        return Collections.unmodifiableSet(butacasOcupadas);
    }
}

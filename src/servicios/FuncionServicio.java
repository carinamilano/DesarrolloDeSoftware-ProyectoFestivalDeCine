package servicios;

import excepciones.DatoInvalidoException;
import excepciones.EntidadDuplicadaException;
import excepciones.EntidadNoEncontradaException;
import excepciones.FuncionNoDisponibleException;
import modelo.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// CORRECCION S1: las funciones tienen su propio servicio (antes estaban en EntradaServicio).
public class FuncionServicio {
    private Festival festival;
    // CORRECCION S3: se guardan también por tipo, así no hace falta instanceof para filtrarlas.
    private List<Funcion> funciones;
    private List<FuncionPresencial> funcionesPresenciales;
    private List<FuncionStreaming> funcionesStreaming;
    private Contador contador;

    public FuncionServicio(Festival festival) {
        this.festival = festival;
        this.funciones = new ArrayList<>();
        this.funcionesPresenciales = new ArrayList<>();
        this.funcionesStreaming = new ArrayList<>();
        this.contador = new Contador();
    }

    public FuncionPresencial programarFuncionPresencial(Pelicula pelicula, Sala sala, LocalDate fecha, LocalTime horario) {
        validarProgramacion(pelicula, fecha, horario);
        LocalDateTime inicio = LocalDateTime.of(fecha, horario);
        LocalDateTime fin = pelicula.terminaSiEmpiezaA(inicio);
        // CORRECCION I4: se rechaza cualquier superposición en la sala, usando la duración de la película.
        for (FuncionPresencial f : funcionesPresenciales) {
            if (f.usaSala(sala) && f.seSuperponeCon(inicio, fin)) {
                throw new FuncionNoDisponibleException("la " + sala.getNombre() + " está ocupada por "
                        + f.getPelicula().getTitulo() + " de " + f.getHorario() + " a " + f.getFin().toLocalTime() + ".");
            }
        }
        // CORRECCION I11: cada función recibe su código.
        FuncionPresencial funcion = new FuncionPresencial(contador.siguiente("F-"), fecha, horario, pelicula, sala);
        agregarPresencial(funcion);
        return funcion;
    }

    public FuncionStreaming programarFuncionStreaming(Pelicula pelicula, String enlace, int capacidad,
                                                      LocalDate fecha, LocalTime horario) {
        validarProgramacion(pelicula, fecha, horario);
        // CORRECCION I9: la capacidad la valida el constructor de FuncionStreaming.
        FuncionStreaming funcion = new FuncionStreaming(contador.siguiente("F-"), fecha, horario, pelicula, enlace, capacidad);
        agregarStreaming(funcion);
        return funcion;
    }

    private void validarProgramacion(Pelicula pelicula, LocalDate fecha, LocalTime horario) {
        if (pelicula == null || fecha == null || horario == null) {
            throw new DatoInvalidoException("La película, la fecha y el horario son obligatorios.");
        }
        // CORRECCION I1: compara fecha y hora; antes se podía programar hoy a una hora que ya pasó.
        if (!LocalDateTime.of(fecha, horario).isAfter(LocalDateTime.now())) {
            throw new FuncionNoDisponibleException("no se puede programar una función en una fecha u hora pasada.");
        }
        // CORRECCION I6: la película tiene que competir en una edición y la fecha caer dentro de ella.
        if (!festival.compiteEnAlgunaEdicion(pelicula)) {
            throw new FuncionNoDisponibleException(pelicula.getTitulo()
                    + " no compite en ninguna edición. Asóciela a una sección primero.");
        }
        Edicion edicion = festival.buscarEdicionDe(pelicula);
        if (!edicion.incluyeFecha(fecha)) {
            throw new FuncionNoDisponibleException("la edición " + edicion.getAnio() + " es del "
                    + edicion.getFechaInicio() + " al " + edicion.getFechaFin() + ".");
        }
    }


    public FuncionPresencial restaurarFuncionPresencial(String codigo, Pelicula pelicula, Sala sala, LocalDate fecha, LocalTime horario) {
        validarCodigoLibre(codigo);
        FuncionPresencial funcion = new FuncionPresencial(codigo, fecha, horario, pelicula, sala);
        contador.registrarUsado(codigo);
        agregarPresencial(funcion);
        return funcion;
    }

    public FuncionStreaming restaurarFuncionStreaming(String codigo, Pelicula pelicula, String enlace, int capacidad, LocalDate fecha, LocalTime horario) {
        validarCodigoLibre(codigo);
        FuncionStreaming funcion = new FuncionStreaming(codigo, fecha, horario, pelicula, enlace, capacidad);
        contador.registrarUsado(codigo);
        agregarStreaming(funcion);
        return funcion;
    }

    private void validarCodigoLibre(String codigo) {
        for (Funcion f : funciones) {
            if (f.getCodigo().equalsIgnoreCase(codigo)) {
                throw new EntidadDuplicadaException("Ya existe la función " + codigo + ".");
            }
        }
    }

    // CORRECCION I14: la función se vincula con su película recién cuando ya es válida.
    private void agregarPresencial(FuncionPresencial funcion) {
        funcion.vincular();
        funciones.add(funcion);
        funcionesPresenciales.add(funcion);
    }

    private void agregarStreaming(FuncionStreaming funcion) {
        funcion.vincular();
        funciones.add(funcion);
        funcionesStreaming.add(funcion);
    }

    // CORRECCION S7/I1: la interfaz ofrece solo las funciones que todavía no comenzaron.
    public List<FuncionPresencial> getFuncionesPresencialesEnVenta() {
        List<FuncionPresencial> enVenta = new ArrayList<>();
        for (FuncionPresencial f : funcionesPresenciales) {
            if (!f.yaComenzo()) enVenta.add(f);
        }
        return enVenta;
    }

    public List<FuncionStreaming> getFuncionesStreamingEnVenta() {
        List<FuncionStreaming> enVenta = new ArrayList<>();
        for (FuncionStreaming f : funcionesStreaming) {
            if (!f.yaComenzo()) enVenta.add(f);
        }
        return enVenta;
    }

    public List<LocalDate> getFechasConFuncionesPresenciales() {
        List<LocalDate> fechas = new ArrayList<>();
        for (FuncionPresencial f : funcionesPresenciales) {
            if (!f.yaComenzo() && !fechas.contains(f.getFecha())) {
                fechas.add(f.getFecha());
            }
        }
        Collections.sort(fechas);
        return fechas;
    }

    public boolean hayFuncionesPresencialesEl(LocalDate fecha) {
        for (FuncionPresencial f : funcionesPresenciales) {
            if (f.getFecha().equals(fecha)) return true;
        }
        return false;
    }

    // CORRECCION I11/B1: se busca por código; antes se buscaba por título y fecha y se devolvía la primera.
    public Funcion buscarFuncion(String codigo) {
        for (Funcion f : funciones) {
            if (f.getCodigo().equalsIgnoreCase(codigo)) return f;
        }
        throw new EntidadNoEncontradaException("Funcion", "código: " + codigo);
    }

    public FuncionPresencial buscarFuncionPresencial(String codigo) {
        for (FuncionPresencial f : funcionesPresenciales) {
            if (f.getCodigo().equalsIgnoreCase(codigo)) return f;
        }
        throw new EntidadNoEncontradaException("Funcion presencial", "código: " + codigo);
    }

    public FuncionStreaming buscarFuncionStreaming(String codigo) {
        for (FuncionStreaming f : funcionesStreaming) {
            if (f.getCodigo().equalsIgnoreCase(codigo)) return f;
        }
        throw new EntidadNoEncontradaException("Funcion streaming", "código: " + codigo);
    }

    // CORRECCION I13: las listas no se pueden modificar desde afuera.
    public List<Funcion> getFunciones() {
        return Collections.unmodifiableList(funciones);
    }
    public List<FuncionPresencial> getFuncionesPresenciales() {
        return Collections.unmodifiableList(funcionesPresenciales);
    }
    public List<FuncionStreaming> getFuncionesStreaming() {
        return Collections.unmodifiableList(funcionesStreaming);
    }
}

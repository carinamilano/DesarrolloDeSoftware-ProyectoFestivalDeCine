package modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public abstract class Funcion {
    // CORRECCION I11: cada función tiene un código propio, como las entradas y los abonos.
    private String codigo;
    private LocalDate fecha;
    private LocalTime horario;
    private Pelicula pelicula;

    public Funcion(String codigo, LocalDate fecha, LocalTime horario, Pelicula pelicula) {
        // CORRECCION I9: no se crea una función con datos faltantes.
        this.codigo = Validar.texto(codigo, "El código de la función es obligatorio.");
        this.fecha = Validar.presente(fecha, "La fecha es obligatoria.");
        this.horario = Validar.presente(horario, "El horario es obligatorio.");
        this.pelicula = Validar.presente(pelicula, "La película es obligatoria.");
    }

    // CORRECCION I14: agrega la función a su película. Lo llama el servicio una vez que la función es válida.
    public void vincular() {
        pelicula.agregarFuncion(this);
    }

    public abstract boolean verificarDisponibilidad();
    public abstract double calcularOcupacion();

    /* CORRECCION S3/S4: cada tipo de función responde su precio, su tipo (enum TipoFuncion) y su lugar,
    así el servicio, los abonos y los paneles no preguntan con instanceof.*/
    public abstract double getPrecioBase();
    public abstract TipoFuncion getTipo();
    public abstract String getLugar();
    public abstract boolean requiereButaca();

    // CORRECCION S11: la función responde el título de su película, sin cadenas de getters en la UI.
    public String getTituloPelicula() {
        return pelicula.getTitulo();
    }

    public LocalDateTime getInicio() {
        return LocalDateTime.of(fecha, horario);
    }

    // CORRECCION I4: la función ocupa desde que empieza hasta que termina la película.
    public LocalDateTime getFin() {
        return pelicula.terminaSiEmpiezaA(getInicio());
    }

    // CORRECCION I4/I5: compara intervalos, no solo la hora de inicio.
    public boolean seSuperponeCon(LocalDateTime inicio, LocalDateTime fin) {
        return inicio.isBefore(getFin()) && getInicio().isBefore(fin);
    }

    public boolean seSuperponeCon(Funcion otra) {
        return seSuperponeCon(otra.getInicio(), otra.getFin());
    }

    // CORRECCION I1/I2: compara fecha y hora; antes solo se miraba la fecha.
    public boolean yaComenzo() {
        return !LocalDateTime.now().isBefore(getInicio());
    }

    public boolean esDe(Pelicula p) {
        return pelicula.equals(p);
    }

    public String getCodigo() {
        return codigo;
    }
    public LocalDate getFecha() {
        return fecha;
    }
    public LocalTime getHorario() {
        return horario;
    }
    public Pelicula getPelicula() {
        return pelicula;
    }

    // CORRECCION S13: el código identifica a la función.
    @Override
    public boolean equals(Object otro) {
        if (this == otro) return true;
        if (!(otro instanceof Funcion)) return false;
        return codigo.equals(((Funcion) otro).codigo);
    }

    @Override
    public int hashCode() {
        return codigo.hashCode();
    }

    @Override
    public String toString() {
        return pelicula.getTitulo() + " - " + fecha + " " + horario;
    }
}

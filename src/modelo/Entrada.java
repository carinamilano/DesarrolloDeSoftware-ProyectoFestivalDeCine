package modelo;

import excepciones.DatoInvalidoException;
import excepciones.ReglaNegocioException;

public abstract class Entrada {
    private String codigo;
    private Funcion funcion;
    private Espectador espectador;
    private double precio;
    private Abono abono;

    public Entrada(String codigo, Funcion funcion, Espectador espectador, double precio, Abono abono) {

        // CORRECCION I9: datos obligatorios y precio no negativo.
        this.codigo = Validar.texto(codigo, "El código de la entrada es obligatorio.");
        this.funcion = Validar.presente(funcion, "La función es obligatoria.");
        this.espectador = Validar.presente(espectador, "El espectador es obligatorio.");
        if (precio < 0) {
            throw new DatoInvalidoException("El precio no puede ser negativo.");
        }
        // CORRECCION I14: una reserva solo puede ser del dueño del abono.
        if (abono != null && !abono.getEspectador().equals(espectador)) {
            throw new ReglaNegocioException("La reserva debe ser del dueño del abono " + abono.getCodigo() + ".");
        }
        this.precio = precio;
        this.abono = abono;
    }

    // CORRECCION I14: un solo metodo vincula la entrada con su espectador y, si es reserva, con su abono.
    public void vincular() {
        espectador.agregarEntrada(this);
        if (abono != null) abono.agregarReserva(this);
    }

    public void desvincular() {
        espectador.quitarEntrada(this);
        if (abono != null) abono.quitarReserva(this);
    }

    public boolean esReservaDeAbono() {
        return abono != null;
    }

    // CORRECCION S11: la entrada responde si es para una película
    public boolean esPara(Pelicula pelicula) {
        return funcion.esDe(pelicula);
    }

    // CORRECCION S11: la UI le pide estos datos a la entrada, sin entrada.getFuncion().getPelicula().getTitulo().
    public String getTituloPelicula() {
        return funcion.getTituloPelicula();
    }

    public TipoFuncion getTipoFuncion() {
        return funcion.getTipo();
    }
    // CORRECCION S3: cada tipo de entrada describe y libera su lugar, sin instanceof.
    public abstract String getDetalle();
    public abstract void liberarLugar();
    public String getCodigo() {
        return codigo;
    }
    public Funcion getFuncion() {
        return funcion;
    }
    public Espectador getEspectador() {
        return espectador;
    }
    public double getPrecio() {
        return precio;
    }

    // CORRECCION S5: no devuelve null. Antes de llamarlo hay que preguntar esReservaDeAbono().
    public Abono getAbono() {
        if (abono == null) {
            throw new ReglaNegocioException("La entrada " + codigo + " no es una reserva de abono.");
        }
        return abono;
    }

    // CORRECCION S13: el código identifica a la entrada.
    @Override
    public boolean equals(Object otro) {
        if (this == otro) return true;
        if (!(otro instanceof Entrada)) return false;
        return codigo.equals(((Entrada) otro).codigo);
    }

    @Override
    public int hashCode() {
        return codigo.hashCode();
    }

    @Override
    public String toString() {
        return "Entrada: " + codigo + " - " + funcion.getPelicula().getTitulo()
                + " - " + funcion.getFecha() + " " + funcion.getHorario();
    }
}

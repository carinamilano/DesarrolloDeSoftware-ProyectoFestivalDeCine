package modelo;

// Objeto parámetro: agrupa los datos con los que se emite una entrada
// (antes viajaban sueltos en firmas de 5 y 6 parámetros)
public record SolicitudEntrada(Espectador espectador, double precio, Abono abono, int numeroButaca) {

    public static final int SIN_BUTACA = 0;

    public SolicitudEntrada {
        Validar.presente(espectador, "El espectador es obligatorio.");
    }

    public static SolicitudEntrada venta(Espectador espectador, double precio, int numeroButaca) {
        return new SolicitudEntrada(espectador, precio, null, numeroButaca);
    }

    // Una reserva con abono es una entrada de precio $0 asociada al abono
    public static SolicitudEntrada reserva(Abono abono, int numeroButaca) {
        return new SolicitudEntrada(abono.getEspectador(), 0, abono, numeroButaca);
    }
}

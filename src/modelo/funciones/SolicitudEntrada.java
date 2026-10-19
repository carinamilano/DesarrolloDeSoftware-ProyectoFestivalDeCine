package modelo.funciones;

import modelo.abonos.Abono;
import modelo.comun.Validar;
import modelo.personas.Espectador;
import modelo.precios.Dinero;

import java.util.Optional;

// Objeto parámetro: agrupa los datos con los que se emite una entrada.
// El abono y la butaca son opcionales: una entrada suelta no tiene abono y una de streaming no tiene butaca
// (antes eran null y 0)
public record SolicitudEntrada(Espectador espectador, Dinero precio, Optional<Abono> abono, Optional<Butaca> butaca) {

    public SolicitudEntrada {
        Validar.presente(espectador, "El espectador es obligatorio.");
        Validar.presente(precio, "El precio es obligatorio.");
        Validar.presente(abono, "Indique si la entrada es con abono.");
        Validar.presente(butaca, "Indique la butaca (o que no tiene).");
    }

    public static SolicitudEntrada venta(Espectador espectador, Dinero precio, Optional<Butaca> butaca) {
        return new SolicitudEntrada(espectador, precio, Optional.empty(), butaca);
    }

    // Una reserva con abono es una entrada de precio $0 asociada al abono
    public static SolicitudEntrada reserva(Abono abono, Optional<Butaca> butaca) {
        Validar.presente(abono, "El abono es obligatorio.");
        return new SolicitudEntrada(abono.getEspectador(), Dinero.CERO, Optional.of(abono), butaca);
    }
}

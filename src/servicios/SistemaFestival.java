package servicios;

import modelo.comun.Numerador;
import repositorio.Catalogo;
import servicios.precios.Tarifario;

import java.time.Clock;

// Arma los servicios y le reparte a cada uno solo los repositorios que necesita.
// El reloj se recibe desde afuera (Main usa el del sistema; una prueba puede pasar uno fijo)
public class SistemaFestival {
    private final PersonaServicio personaServicio;
    private final PeliculaServicio peliculaServicio;
    private final FestivalServicio festivalServicio;
    private final FuncionServicio funcionServicio;
    private final VentaServicio ventaServicio;
    private final AbonoServicio abonoServicio;
    private final EvaluacionServicio evaluacionServicio;

    public SistemaFestival(Catalogo catalogo, Clock reloj) {
        Numerador numerador = catalogo.getNumerador();
        Tarifario tarifario = new Tarifario(reloj);
        personaServicio = new PersonaServicio(catalogo.getDirectores(), catalogo.getActores(),
                catalogo.getJurados(), catalogo.getEspectadores());
        peliculaServicio = new PeliculaServicio(catalogo.getPeliculas(), catalogo.getFestival());
        festivalServicio = new FestivalServicio(catalogo.getFestival(), catalogo.getSalas(), catalogo.getPeliculas());
        funcionServicio = new FuncionServicio(catalogo.getFuncionesPresenciales(), catalogo.getFuncionesStreaming(),
                catalogo.getFestival(), numerador, reloj);
        ventaServicio = new VentaServicio(catalogo.getEntradas(), tarifario, numerador);
        abonoServicio = new AbonoServicio(catalogo.getAbonos(), funcionServicio, ventaServicio, tarifario, numerador, reloj);
        evaluacionServicio = new EvaluacionServicio(peliculaServicio);
    }

    public PersonaServicio getPersonaServicio() { return personaServicio; }
    public PeliculaServicio getPeliculaServicio() { return peliculaServicio; }
    public FestivalServicio getFestivalServicio() { return festivalServicio; }
    public FuncionServicio getFuncionServicio() { return funcionServicio; }
    public VentaServicio getVentaServicio() { return ventaServicio; }
    public AbonoServicio getAbonoServicio() { return abonoServicio; }
    public EvaluacionServicio getEvaluacionServicio() { return evaluacionServicio; }
}

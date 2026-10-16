package servicios;

import modelo.Numerador;
import repositorio.Catalogo;
import servicios.precios.Tarifario;

// Arma los servicios y le reparte a cada uno solo los repositorios que necesita.
// La UI recibe este único objeto en vez de una lista larga de servicios
public class SistemaFestival {
    private final PersonaServicio personaServicio;
    private final PeliculaServicio peliculaServicio;
    private final FestivalServicio festivalServicio;
    private final FuncionServicio funcionServicio;
    private final VentaServicio ventaServicio;
    private final AbonoServicio abonoServicio;
    private final EvaluacionServicio evaluacionServicio;

    public SistemaFestival(Catalogo catalogo) {
        Numerador numerador = new Numerador();
        personaServicio = new PersonaServicio(catalogo.getDirectores(), catalogo.getActores(),
                catalogo.getJurados(), catalogo.getEspectadores());
        peliculaServicio = new PeliculaServicio(catalogo.getPeliculas(), personaServicio, catalogo.getFestival());
        festivalServicio = new FestivalServicio(catalogo.getFestival(), catalogo.getSalas(), catalogo.getPeliculas());
        funcionServicio = new FuncionServicio(catalogo.getFuncionesPresenciales(), catalogo.getFuncionesStreaming());
        ventaServicio = new VentaServicio(catalogo.getEntradas(), personaServicio, new Tarifario(), numerador);
        abonoServicio = new AbonoServicio(catalogo.getAbonos(), personaServicio, funcionServicio, ventaServicio, numerador);
        evaluacionServicio = new EvaluacionServicio(personaServicio, peliculaServicio);
    }

    public PersonaServicio getPersonaServicio() { return personaServicio; }
    public PeliculaServicio getPeliculaServicio() { return peliculaServicio; }
    public FestivalServicio getFestivalServicio() { return festivalServicio; }
    public FuncionServicio getFuncionServicio() { return funcionServicio; }
    public VentaServicio getVentaServicio() { return ventaServicio; }
    public AbonoServicio getAbonoServicio() { return abonoServicio; }
    public EvaluacionServicio getEvaluacionServicio() { return evaluacionServicio; }
}

package repositorio;

import modelo.*;

// Reúne los datos del sistema. Lo comparten la persistencia (que lo carga y lo guarda)
// y SistemaFestival (que reparte cada repositorio al servicio que lo necesita)
public class Catalogo {
    private final Festival festival;
    private final Repositorio<Sala> salas = new RepositorioEnMemoria<>();
    private final Repositorio<Director> directores = new RepositorioEnMemoria<>();
    private final Repositorio<Actor> actores = new RepositorioEnMemoria<>();
    private final Repositorio<Jurado> jurados = new RepositorioEnMemoria<>();
    private final Repositorio<Espectador> espectadores = new RepositorioEnMemoria<>();
    private final Repositorio<Pelicula> peliculas = new RepositorioEnMemoria<>();
    private final Repositorio<FuncionPresencial> funcionesPresenciales = new RepositorioEnMemoria<>();
    private final Repositorio<FuncionStreaming> funcionesStreaming = new RepositorioEnMemoria<>();
    private final Repositorio<Entrada> entradas = new RepositorioEnMemoria<>();
    private final Repositorio<Abono> abonos = new RepositorioEnMemoria<>();

    public Catalogo(String nombreFestival) {
        this.festival = new Festival(nombreFestival);
    }

    public Festival getFestival() { return festival; }
    public Repositorio<Sala> getSalas() { return salas; }
    public Repositorio<Director> getDirectores() { return directores; }
    public Repositorio<Actor> getActores() { return actores; }
    public Repositorio<Jurado> getJurados() { return jurados; }
    public Repositorio<Espectador> getEspectadores() { return espectadores; }
    public Repositorio<Pelicula> getPeliculas() { return peliculas; }
    public Repositorio<FuncionPresencial> getFuncionesPresenciales() { return funcionesPresenciales; }
    public Repositorio<FuncionStreaming> getFuncionesStreaming() { return funcionesStreaming; }
    public Repositorio<Entrada> getEntradas() { return entradas; }
    public Repositorio<Abono> getAbonos() { return abonos; }
}

package persistencia;

import modelo.Persona;
import servicios.PersonaServicio;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// CORRECCION S17: directores, actores, jurados y espectadores (antes en Persistencia).
class PersistenciaPersonas extends ArchivosDeDominio {

    private static final String DIRECTORES = "directores.txt";
    private static final String ACTORES = "actores.txt";
    private static final String JURADOS = "jurados.txt";
    private static final String ESPECTADORES = "espectadores.txt";
    private final PersonaServicio personaServicio;

    PersistenciaPersonas(Path carpeta, List<String> erroresDeCarga, PersonaServicio personaServicio) {
        super(carpeta, erroresDeCarga);
        this.personaServicio = personaServicio;
    }

    @Override
    void cargar() {
        cargarCada(DIRECTORES, r -> personaServicio.registrarDirector(r.texto(0), r.texto(1), r.texto(2)));
        cargarCada(ACTORES, r -> personaServicio.registrarActor(r.texto(0), r.texto(1), r.texto(2)));
        cargarCada(JURADOS, r -> personaServicio.registrarJurado(r.texto(0), r.texto(1), r.texto(2)));
        cargarCada(ESPECTADORES, r -> personaServicio.registrarEspectador(r.texto(0), r.texto(1), r.texto(2)));
    }

    @Override
    void agregarLineas(Map<String, List<String>> archivos) {
        archivos.put(DIRECTORES, lineasPersonas(personaServicio.getDirectores()));
        archivos.put(ACTORES, lineasPersonas(personaServicio.getActores()));
        archivos.put(JURADOS, lineasPersonas(personaServicio.getJurados()));
        archivos.put(ESPECTADORES, lineasPersonas(personaServicio.getEspectadores()));
    }

    private List<String> lineasPersonas(List<? extends Persona> personas) {
        List<String> lineas = new ArrayList<>();
        for (Persona p : personas) {
            lineas.add(FormatoLinea.unir(p.getNombre(), p.getEmail(), p.getNacionalidad()));
        }
        return lineas;
    }
}

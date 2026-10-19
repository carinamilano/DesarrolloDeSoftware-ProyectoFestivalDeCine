package inicializacion;

import excepciones.EntidadNoEncontradaException;
import modelo.personas.Actor;
import modelo.personas.Espectador;
import modelo.personas.Persona;
import servicios.PersonaServicio;

import java.util.List;

// Los datos de demostración nombran a las personas por su nombre, que en los archivos de ejemplo
// no se repite. El sistema las identifica por email
class PersonasDemo {
    private final PersonaServicio personas;

    PersonasDemo(PersonaServicio personas) {
        this.personas = personas;
    }

    Espectador espectador(String nombre) {
        return buscar(personas.getEspectadores(), nombre, "Espectador");
    }

    Actor actor(String nombre) {
        return buscar(personas.getActores(), nombre, "Actor");
    }

    private static <T extends Persona> T buscar(List<T> lista, String nombre, String entidad) {
        return lista.stream()
                .filter(persona -> persona.seLlama(nombre))
                .findFirst()
                .orElseThrow(() -> new EntidadNoEncontradaException(entidad, "nombre: " + nombre));
    }
}

package modelo.personas;

import modelo.comun.Validar;

import java.util.Locale;
import java.util.Objects;

public abstract class Persona {
    private final String nombre;
    private final String email;
    private final String nacionalidad;

    protected Persona(String nombre, String email, String nacionalidad) {
        this.nombre = Validar.texto(nombre, "El nombre es obligatorio.");
        this.email = Validar.texto(email, "El email es obligatorio.").toLowerCase(Locale.ROOT);
        this.nacionalidad = Validar.texto(nacionalidad, "La nacionalidad es obligatoria.");
    }

    // Template Method: Persona arma el toString y cada subclase solo dice qué rol cumple
    protected abstract String getRol();

    // Búsqueda por nombre (filtros y datos de demostración). La identidad de la persona es el email
    public boolean seLlama(String otroNombre) {
        return otroNombre != null && nombre.equalsIgnoreCase(otroNombre.trim());
    }

    public boolean tieneEmail(String otroEmail) {
        return otroEmail != null && email.equalsIgnoreCase(otroEmail.trim());
    }

    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getNacionalidad() { return nacionalidad; }

    // Para mostrar en listas desplegables: con el email se distinguen dos personas homónimas
    public String getNombreYEmail() {
        return nombre + " <" + email + ">";
    }

    // Identidad: el email dentro de cada rol. Antes se identificaban por el nombre y no podían existir dos homónimos
    @Override
    public boolean equals(Object otro) {
        return otro instanceof Persona persona && getClass() == persona.getClass() && email.equals(persona.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), email);
    }

    @Override
    public String toString() {
        return getRol() + ": " + nombre + " (" + nacionalidad + ")";
    }
}

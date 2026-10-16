package modelo;

public abstract class Persona {
    private final String nombre;
    private final String email;
    private final String nacionalidad;

    protected Persona(String nombre, String email, String nacionalidad) {
        this.nombre = Validar.texto(nombre, "El nombre es obligatorio.");
        this.email = Validar.texto(email, "El email es obligatorio.");
        this.nacionalidad = Validar.texto(nacionalidad, "La nacionalidad es obligatoria.");
    }

    // Template Method: Persona arma el toString y cada subclase solo dice qué rol cumple
    protected abstract String getRol();

    public boolean seLlama(String otroNombre) {
        return otroNombre != null && nombre.equalsIgnoreCase(otroNombre.trim());
    }

    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getNacionalidad() { return nacionalidad; }

    @Override
    public String toString() {
        return getRol() + ": " + nombre + " (" + nacionalidad + ")";
    }
}

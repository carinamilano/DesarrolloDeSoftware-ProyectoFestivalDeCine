package modelo;

import java.util.Objects;

public abstract class Persona {
    private String nombre;
    private String email;
    private String nacionalidad;

    public Persona(String nombre, String email, String nacionalidad) {
        // CORRECCION I9: los datos obligatorios se validan acá, no solo en los formularios.
        this.nombre = Validar.texto(nombre, "El nombre es obligatorio.");
        // CORRECCION I10: el email identifica a la persona; se guarda en minúsculas.
        this.email = Validar.texto(email, "El email es obligatorio.").toLowerCase();
        this.nacionalidad = Validar.texto(nacionalidad, "La nacionalidad es obligatoria.");
    }

    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getNacionalidad() { return nacionalidad; }

    // CORRECCION I10: las búsquedas se hacen por email.
    public boolean tieneEmail(String email) {
        return email != null && this.email.equalsIgnoreCase(email.trim());
    }

    // CORRECCION I10: texto para los combos, así se distinguen los homónimos.
    public String getNombreYEmail() {
        return nombre + " <" + email + ">";
    }

    // CORRECCION S13: dos personas son la misma si son del mismo tipo y tienen el mismo email.
    @Override
    public boolean equals(Object otro) {
        if (this == otro) return true;
        if (otro == null || getClass() != otro.getClass()) return false;
        return email.equals(((Persona) otro).email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), email);
    }

    @Override
    public String toString() {
        return nombre + " (" + nacionalidad + ")";
    }
}

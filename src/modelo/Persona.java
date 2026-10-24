package modelo;

public abstract class Persona {
    private String nombre;
    private String email;
    private String nacionalidad;

    public Persona(String nombre, String email, String nacionalidad) {
        this.nombre = nombre;
        this.email = email;
        this.nacionalidad = nacionalidad;
    }

    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getNacionalidad() { return nacionalidad; }

    @Override
    public String toString() {
        return nombre + " (" + nacionalidad + ")";
    }
}
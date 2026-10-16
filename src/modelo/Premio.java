package modelo;

// Objeto de valor: el premio que otorga una sección
public class Premio {
    private final String nombre;
    private final String descripcion;

    public Premio(String nombre, String descripcion) {
        this.nombre = Validar.texto(nombre, "El nombre del premio es obligatorio.");
        this.descripcion = descripcion == null ? "" : descripcion.trim();
    }

    public String getNombre() {
        return nombre;
    }
    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return nombre + " - " + descripcion;
    }
}

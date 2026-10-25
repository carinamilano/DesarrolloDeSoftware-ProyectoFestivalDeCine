package modelo;

public class Premio {
    private String nombre;
    private String descripcion;

    public Premio(String nombre, String descripcion) {
        // CORRECCION I9: el nombre obligatorio se valida acá (antes lo hacía FestivalServicio).
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

package modelo.peliculas;

import excepciones.DatoInvalidoException;

// Antes el género era un String y la lista de géneros válidos estaba escrita en la UI
public enum Genero {
    DRAMA("Drama"),
    COMEDIA("Comedia"),
    THRILLER("Thriller"),
    CIENCIA_FICCION("Ciencia Ficcion"),
    TERROR("Terror"),
    ANIMACION("Animacion"),
    DOCUMENTAL("Documental"),
    MUSICAL("Musical"),
    ROMANCE("Romance"),
    ACCION("Accion");

    private final String descripcion;

    Genero(String descripcion) {
        this.descripcion = descripcion;
    }

    public static Genero desde(String texto) {
        for (Genero genero : values()) {
            if (texto != null && (genero.descripcion.equalsIgnoreCase(texto.trim()) || genero.name().equalsIgnoreCase(texto.trim()))) {
                return genero;
            }
        }
        throw new DatoInvalidoException("Género desconocido: " + texto);
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}

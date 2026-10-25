package modelo;

// CORRECCION S4: el género deja de ser texto libre.
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

    private final String etiqueta;

    Genero(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}

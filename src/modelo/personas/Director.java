package modelo.personas;

public class Director extends Artista {

    public Director(String nombre, String email, String nacionalidad) {
        super(nombre, email, nacionalidad);
    }

    @Override
    protected String getRol() {
        return "Director";
    }
}

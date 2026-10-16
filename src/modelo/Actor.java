package modelo;

public class Actor extends Artista {

    public Actor(String nombre, String email, String nacionalidad) {
        super(nombre, email, nacionalidad);
    }

    @Override
    protected String getRol() {
        return "Actor";
    }
}

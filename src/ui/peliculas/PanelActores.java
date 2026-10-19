package ui.peliculas;

import modelo.peliculas.Pelicula;
import modelo.personas.Actor;
import servicios.PeliculaServicio;
import servicios.PersonaServicio;
import ui.VentanaPrincipal;
import ui.comun.ComponentesUI;
import ui.comun.DialogoPersona;
import ui.comun.FormularioDialogo;
import ui.comun.PanelListado;
import ui.comun.SelectorPersona;

import javax.swing.JComboBox;
import java.util.List;

public class PanelActores extends PanelListado {
    private final PersonaServicio personaServicio;
    private final PeliculaServicio peliculaServicio;

    public PanelActores(VentanaPrincipal ventana, PersonaServicio personaServicio, PeliculaServicio peliculaServicio) {
        super(ventana, "Gestión de Actores", "Nombre", "Email", "Nacionalidad", "Películas");
        this.personaServicio = personaServicio;
        this.peliculaServicio = peliculaServicio;
        agregarBotones(
                ComponentesUI.crearBoton("Agregar actor", this::agregarActor),
                ComponentesUI.crearBoton("Asociar actor a película", this::asociarActorAPelicula));
        recargar();
    }

    @Override
    protected List<Object[]> obtenerFilas() {
        return personaServicio.getActores().stream()
                .map(actor -> new Object[]{
                        actor.getNombre(), actor.getEmail(), actor.getNacionalidad(), actor.cantidadPeliculas()})
                .toList();
    }

    private void agregarActor() {
        DialogoPersona.mostrar(getVentana(), "Agregar actor", personaServicio::registrarActor)
                .ifPresent(actor -> {
                    recargar();
                    getVentana().setEstado("Actor agregado: " + actor.getNombre());
                });
    }

    private void asociarActorAPelicula() {
        List<Actor> actores = personaServicio.getActores();
        List<Pelicula> peliculas = peliculaServicio.getPeliculas();
        if (actores.isEmpty() || peliculas.isEmpty()) {
            ComponentesUI.avisar(getVentana(), "Se necesitan actores y películas registrados.");
            return;
        }

        FormularioDialogo formulario = new FormularioDialogo(getVentana(), "Asociar actor a película", 460, 220);
        JComboBox<Actor> comboActor = formulario.agregarCampo("Actor:", SelectorPersona.crearCombo(actores));
        JComboBox<Pelicula> comboPelicula = formulario.agregarCampo("Película:",
                ComponentesUI.crearCombo(peliculas, Pelicula::getTitulo));

        formulario.alAceptar(() -> {
            Actor actor = ComponentesUI.seleccionado(comboActor);
            Pelicula pelicula = ComponentesUI.seleccionado(comboPelicula);
            peliculaServicio.asociarActorAPelicula(actor, pelicula);
            recargar();
            getVentana().setEstado("Actor " + actor.getNombre() + " asociado a " + pelicula.getTitulo());
            formulario.cerrar();
        });
        formulario.mostrar();
    }
}

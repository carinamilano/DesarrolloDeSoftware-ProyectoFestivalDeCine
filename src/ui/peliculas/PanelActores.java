package ui.peliculas;

import modelo.Actor;
import modelo.Pelicula;
import servicios.PeliculaServicio;
import servicios.PersonaServicio;
import ui.VentanaPrincipal;
import ui.comun.ComponentesUI;
import ui.comun.DialogoPersona;
import ui.comun.FormularioDialogo;
import ui.comun.PanelListado;

import javax.swing.JComboBox;
import java.util.List;

public class PanelActores extends PanelListado {

    public PanelActores(VentanaPrincipal ventana) {
        super(ventana, "Gestión de Actores", "Nombre", "Email", "Nacionalidad", "Películas");
        agregarBotones(
                ComponentesUI.crearBoton("Agregar actor", this::agregarActor),
                ComponentesUI.crearBoton("Asociar actor a película", this::asociarActorAPelicula));
        recargar();
    }

    @Override
    protected List<Object[]> obtenerFilas() {
        return getSistema().getPersonaServicio().getActores().stream()
                .map(actor -> new Object[]{
                        actor.getNombre(), actor.getEmail(), actor.getNacionalidad(), actor.cantidadPeliculas()})
                .toList();
    }

    private void agregarActor() {
        String nombre = DialogoPersona.mostrar(getVentana(), "Agregar actor",
                getSistema().getPersonaServicio()::registrarActor);
        if (nombre == null) return;
        recargar();
        getVentana().setEstado("Actor agregado: " + nombre);
    }

    private void asociarActorAPelicula() {
        PersonaServicio personas = getSistema().getPersonaServicio();
        PeliculaServicio peliculas = getSistema().getPeliculaServicio();
        if (personas.getActores().isEmpty() || peliculas.getPeliculas().isEmpty()) {
            ComponentesUI.avisar(getVentana(), "Se necesitan actores y películas registrados.");
            return;
        }

        FormularioDialogo formulario = new FormularioDialogo(getVentana(), "Asociar actor a película", 400, 220);
        JComboBox<String> comboActor = formulario.agregarCampo("Actor:", new JComboBox<>(
                personas.getActores().stream().map(Actor::getNombre).toArray(String[]::new)));
        JComboBox<String> comboPelicula = formulario.agregarCampo("Película:", new JComboBox<>(
                peliculas.getPeliculas().stream().map(Pelicula::getTitulo).toArray(String[]::new)));

        formulario.alAceptar(() -> {
            String nombreActor = (String) comboActor.getSelectedItem();
            String tituloPelicula = (String) comboPelicula.getSelectedItem();
            peliculas.asociarActorAPelicula(nombreActor, tituloPelicula);
            recargar();
            getVentana().setEstado("Actor " + nombreActor + " asociado a " + tituloPelicula);
            formulario.cerrar();
        });
        formulario.mostrar();
    }
}

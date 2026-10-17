package ui.peliculas;

import excepciones.FestivalException;
import modelo.peliculas.Genero;
import modelo.personas.Director;
import servicios.PeliculaServicio;
import servicios.PersonaServicio;
import ui.VentanaPrincipal;
import ui.comun.ComponentesUI;
import ui.comun.FormularioDialogo;
import ui.comun.LectorCampos;
import ui.comun.PanelListado;
import ui.comun.SelectorPersona;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.util.List;

public class PanelPeliculas extends PanelListado {
    private static final int COLUMNA_TITULO = 0;

    private final PeliculaServicio peliculaServicio;
    private final PersonaServicio personaServicio;

    public PanelPeliculas(VentanaPrincipal ventana, PeliculaServicio peliculaServicio, PersonaServicio personaServicio) {
        super(ventana, "Gestión de Películas", "Título", "Género", "Duración (min)", "Director", "Promedio");
        this.peliculaServicio = peliculaServicio;
        this.personaServicio = personaServicio;
        agregarBotones(
                ComponentesUI.crearBoton("Agregar película", this::agregarPelicula),
                ComponentesUI.crearBoton("Eliminar película", this::eliminarPelicula));
        recargar();
    }

    @Override
    protected List<Object[]> obtenerFilas() {
        return peliculaServicio.getPeliculas().stream()
                .map(pelicula -> new Object[]{
                        pelicula.getTitulo(),
                        pelicula.getGenero(),
                        pelicula.getDuracion(),
                        pelicula.getNombreDirector(),
                        String.format("%.2f", pelicula.calcularPromedioJurado())})
                .toList();
    }

    private void agregarPelicula() {
        List<Director> directores = personaServicio.getDirectores();
        if (directores.isEmpty()) {
            ComponentesUI.avisar(getVentana(), "No hay directores registrados. Agregue un director primero.");
            return;
        }

        FormularioDialogo formulario = new FormularioDialogo(getVentana(), "Agregar película", 460, 300);
        JTextField campoTitulo = formulario.agregarCampo("Título:", new JTextField());
        JComboBox<Genero> comboGenero = formulario.agregarCampo("Género:", new JComboBox<>(Genero.values()));
        JTextField campoDuracion = formulario.agregarCampo("Duración (min):", new JTextField());
        JComboBox<Director> comboDirector = formulario.agregarCampo("Director:", SelectorPersona.crearCombo(directores));
        formulario.encadenarEnter(campoTitulo, campoDuracion);

        formulario.alAceptar(() -> {
            String titulo = LectorCampos.texto(campoTitulo, "El título es obligatorio.");
            int duracion = LectorCampos.entero(campoDuracion,
                    "La duración es obligatoria.", "La duración debe ser un número entero.");
            peliculaServicio.registrarPelicula(titulo, ComponentesUI.seleccionado(comboGenero), duracion,
                    ComponentesUI.seleccionado(comboDirector));
            recargar();
            getVentana().setEstado("Película agregada: " + titulo);
            formulario.cerrar();
        });
        formulario.mostrar();
    }

    private void eliminarPelicula() {
        String titulo = (String) getValorSeleccionado(COLUMNA_TITULO);
        if (titulo == null) {
            ComponentesUI.avisar(getVentana(), "Seleccione una película de la tabla.");
            return;
        }
        if (!ComponentesUI.confirmar(getVentana(),
                "¿Está seguro que desea eliminar la película \"" + titulo + "\"?", "Confirmar eliminación")) {
            return;
        }
        try {
            peliculaServicio.eliminarPelicula(titulo);
            recargar();
            getVentana().setEstado("Película eliminada: " + titulo);
        } catch (FestivalException ex) {
            ComponentesUI.avisar(getVentana(), ex.getMessage());
        }
    }
}

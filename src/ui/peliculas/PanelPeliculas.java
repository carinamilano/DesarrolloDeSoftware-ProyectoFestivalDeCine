package ui.peliculas;

import modelo.Director;
import modelo.Genero;
import ui.VentanaPrincipal;
import ui.comun.ComponentesUI;
import ui.comun.FormularioDialogo;
import ui.comun.LectorCampos;
import ui.comun.PanelListado;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.util.List;

public class PanelPeliculas extends PanelListado {
    private static final int COLUMNA_TITULO = 0;

    public PanelPeliculas(VentanaPrincipal ventana) {
        super(ventana, "Gestión de Películas", "Título", "Género", "Duración (min)", "Director", "Promedio");
        agregarBotones(
                ComponentesUI.crearBoton("Agregar película", this::agregarPelicula),
                ComponentesUI.crearBoton("Eliminar película", this::eliminarPelicula));
        recargar();
    }

    @Override
    protected List<Object[]> obtenerFilas() {
        return getSistema().getPeliculaServicio().getPeliculas().stream()
                .map(pelicula -> new Object[]{
                        pelicula.getTitulo(),
                        pelicula.getGenero(),
                        pelicula.getDuracion(),
                        pelicula.getNombreDirector(),
                        String.format("%.2f", pelicula.calcularPromedioJurado())})
                .toList();
    }

    private void agregarPelicula() {
        List<Director> directores = getSistema().getPersonaServicio().getDirectores();
        if (directores.isEmpty()) {
            ComponentesUI.avisar(getVentana(), "No hay directores registrados. Agregue un director primero.");
            return;
        }

        FormularioDialogo formulario = new FormularioDialogo(getVentana(), "Agregar película", 420, 300);
        JTextField campoTitulo = formulario.agregarCampo("Título:", new JTextField());
        JComboBox<Genero> comboGenero = formulario.agregarCampo("Género:", new JComboBox<>(Genero.values()));
        JTextField campoDuracion = formulario.agregarCampo("Duración (min):", new JTextField());
        JComboBox<String> comboDirector = formulario.agregarCampo("Director:", new JComboBox<>(
                directores.stream().map(Director::getNombre).toArray(String[]::new)));
        formulario.encadenarEnter(campoTitulo, campoDuracion);

        formulario.alAceptar(() -> {
            String titulo = LectorCampos.texto(campoTitulo, "El título es obligatorio.");
            int duracion = LectorCampos.entero(campoDuracion,
                    "La duración es obligatoria.", "La duración debe ser un número entero.");
            getSistema().getPeliculaServicio().registrarPelicula(titulo,
                    (Genero) comboGenero.getSelectedItem(), duracion, (String) comboDirector.getSelectedItem());
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
            getSistema().getPeliculaServicio().eliminarPelicula(titulo);
            recargar();
            getVentana().setEstado("Película eliminada: " + titulo);
        } catch (RuntimeException ex) {
            ComponentesUI.avisar(getVentana(), ex.getMessage());
        }
    }
}

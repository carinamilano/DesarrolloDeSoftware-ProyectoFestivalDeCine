package ui.funciones;

import modelo.festival.Edicion;
import modelo.funciones.Funcion;
import modelo.peliculas.Pelicula;
import servicios.FuncionServicio;
import ui.comun.CampoInvalidoException;
import ui.comun.ComponentesUI;
import ui.comun.FormularioDialogo;
import ui.comun.LectorCampos;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Window;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

// Template Method para los diálogos de alta de funciones (antes eran dos métodos largos dentro de PanelFunciones).
// La base arma película, edición, fecha y horario; cada subclase agrega sus campos y programa la función
abstract class DialogoFuncion {
    private final Window padre;
    private final List<Pelicula> peliculas;
    protected final FuncionServicio funcionServicio;

    protected DialogoFuncion(Window padre, FuncionServicio funcionServicio) {
        this.padre = padre;
        this.funcionServicio = funcionServicio;
        this.peliculas = funcionServicio.getPeliculasProgramables();
    }

    protected abstract String getTitulo();
    protected abstract int getAlto();
    protected abstract void agregarCamposPropios(FormularioDialogo formulario);
    protected abstract Funcion programar(Pelicula pelicula, LocalDateTime fechaHora) throws CampoInvalidoException;

    boolean hayPeliculasProgramables() {
        return !peliculas.isEmpty();
    }

    // Devuelve la función programada, o vacío si se canceló
    Optional<Funcion> mostrar() {
        FormularioDialogo formulario = new FormularioDialogo(padre, getTitulo(), 480, getAlto());
        JComboBox<Pelicula> comboPelicula = formulario.agregarCampo("Película:",
                ComponentesUI.crearCombo(peliculas, Pelicula::getTitulo));
        JLabel labelEdicion = formulario.agregarCampo("Edición:", new JLabel());
        labelEdicion.setFont(ComponentesUI.FUENTE_AYUDA);
        agregarCamposPropios(formulario);
        JTextField campoFecha = formulario.agregarCampo("Fecha (AAAA-MM-DD):", new JTextField());
        JTextField campoHorario = formulario.agregarCampo("Horario (HH:MM):", new JTextField());
        formulario.encadenarEnter(campoFecha, campoHorario);

        // La función tiene que caer dentro de las fechas de la edición en la que compite la película
        Runnable mostrarEdicion = () -> {
            Pelicula pelicula = ComponentesUI.seleccionado(comboPelicula);
            if (pelicula == null) return;
            Edicion edicion = funcionServicio.buscarEdicionDe(pelicula);
            labelEdicion.setText(edicion.getAnio() + " (del " + edicion.getFechaInicio() + " al " + edicion.getFechaFin() + ")");
        };
        comboPelicula.addActionListener(e -> mostrarEdicion.run());
        mostrarEdicion.run();

        AtomicReference<Funcion> programada = new AtomicReference<>();
        formulario.alAceptar(() -> {
            LocalDateTime fechaHora = LocalDateTime.of(
                    LectorCampos.fecha(campoFecha, "La fecha es obligatoria."),
                    LectorCampos.horario(campoHorario, "El horario es obligatorio."));
            programada.set(programar(ComponentesUI.seleccionado(comboPelicula), fechaHora));
            formulario.cerrar();
        });
        formulario.mostrar();
        return Optional.ofNullable(programada.get());
    }
}

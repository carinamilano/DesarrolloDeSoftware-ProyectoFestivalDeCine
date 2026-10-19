package ui.festival;

import modelo.festival.Edicion;
import modelo.festival.Seccion;
import modelo.festival.TipoSeccion;
import servicios.FestivalServicio;
import ui.comun.ComponentesUI;
import ui.comun.FormularioDialogo;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.awt.Window;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

// Alta de una sección con su premio (antes era un método largo dentro de PanelSecciones)
class DialogoAgregarSeccion {
    private final Window padre;
    private final FestivalServicio festivalServicio;
    private final Edicion edicion;

    DialogoAgregarSeccion(Window padre, FestivalServicio festivalServicio, Edicion edicion) {
        this.padre = padre;
        this.festivalServicio = festivalServicio;
        this.edicion = edicion;
    }

    // Devuelve la sección registrada, o vacío si se canceló
    Optional<Seccion> mostrar() {
        // Solo se ofrecen los tipos que la edición todavía no tiene
        List<TipoSeccion> disponibles = edicion.getTiposSinSeccion();

        FormularioDialogo formulario = new FormularioDialogo(padre, "Agregar sección - Edición " + edicion.getAnio(), 440, 250);
        JComboBox<TipoSeccion> comboTipo = formulario.agregarCampo("Tipo de sección:",
                new JComboBox<>(disponibles.toArray(new TipoSeccion[0])));
        JTextField campoPremio = formulario.agregarCampo("Nombre del premio:", new JTextField());
        JTextField campoDescripcion = formulario.agregarCampo("Descripción del premio:", new JTextField());
        formulario.encadenarEnter(campoPremio, campoDescripcion);

        AtomicReference<Seccion> registrada = new AtomicReference<>();
        formulario.alAceptar(() -> {
            registrada.set(festivalServicio.registrarSeccion(edicion.getAnio(), ComponentesUI.seleccionado(comboTipo),
                    campoPremio.getText(), campoDescripcion.getText()));
            formulario.cerrar();
        });
        formulario.mostrar();
        return Optional.ofNullable(registrada.get());
    }
}

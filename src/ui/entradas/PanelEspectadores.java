package ui.entradas;

import servicios.PersonaServicio;
import ui.VentanaPrincipal;
import ui.comun.ComponentesUI;
import ui.comun.PanelListado;

import java.util.List;

public class PanelEspectadores extends PanelListado {
    private final PersonaServicio personaServicio;

    public PanelEspectadores(VentanaPrincipal ventana, PersonaServicio personaServicio) {
        super(ventana, "Gestión de Espectadores", "Nombre", "Email", "Nacionalidad", "Entradas compradas");
        this.personaServicio = personaServicio;
        agregarBotones(ComponentesUI.crearBoton("Agregar espectador", this::agregarEspectador));
        recargar();
    }

    @Override
    protected List<Object[]> obtenerFilas() {
        return personaServicio.getEspectadores().stream()
                .map(espectador -> new Object[]{
                        espectador.getNombre(), espectador.getEmail(), espectador.getNacionalidad(),
                        espectador.cantidadEntradas()})
                .toList();
    }

    private void agregarEspectador() {
        if (SelectorEspectador.darDeAlta(getVentana(), personaServicio).isPresent()) {
            recargar();
        }
    }
}

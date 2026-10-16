package ui.entradas;

import ui.VentanaPrincipal;
import ui.comun.ComponentesUI;
import ui.comun.PanelListado;

import java.util.List;

public class PanelEspectadores extends PanelListado {

    public PanelEspectadores(VentanaPrincipal ventana) {
        super(ventana, "Gestión de Espectadores", "Nombre", "Email", "Nacionalidad", "Entradas compradas");
        agregarBotones(ComponentesUI.crearBoton("Agregar espectador", this::agregarEspectador));
        recargar();
    }

    @Override
    protected List<Object[]> obtenerFilas() {
        return getSistema().getPersonaServicio().getEspectadores().stream()
                .map(espectador -> new Object[]{
                        espectador.getNombre(), espectador.getEmail(), espectador.getNacionalidad(),
                        espectador.cantidadEntradas()})
                .toList();
    }

    private void agregarEspectador() {
        if (SelectorEspectador.darDeAlta(getVentana()) != null) {
            recargar();
        }
    }
}

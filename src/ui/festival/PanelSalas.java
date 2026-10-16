package ui.festival;

import modelo.Sala;
import ui.VentanaPrincipal;
import ui.comun.ComponentesUI;
import ui.comun.FormularioDialogo;
import ui.comun.LectorCampos;
import ui.comun.PanelListado;

import javax.swing.JTable;
import javax.swing.JTextField;
import java.util.List;

public class PanelSalas extends PanelListado {

    public PanelSalas(VentanaPrincipal ventana) {
        super(ventana, "Gestión de Salas", "Nombre", "Capacidad");
        // Columnas más chicas
        getTabla().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        getTabla().getColumnModel().getColumn(0).setPreferredWidth(200);
        getTabla().getColumnModel().getColumn(1).setPreferredWidth(100);
        agregarBotones(ComponentesUI.crearBoton("Agregar sala", this::agregarSala));
        recargar();
    }

    @Override
    protected List<Object[]> obtenerFilas() {
        return getSistema().getFestivalServicio().getSalas().stream()
                .map(sala -> new Object[]{sala.getNombre(), sala.getCantidadButacas()})
                .toList();
    }

    private void agregarSala() {
        FormularioDialogo formulario = new FormularioDialogo(getVentana(), "Agregar sala", 350, 220);
        JTextField campoNumero = formulario.agregarCampo("Sala N°:", new JTextField());
        JTextField campoCapacidad = formulario.agregarCampo("Capacidad:", new JTextField());
        formulario.encadenarEnter(campoNumero, campoCapacidad);

        formulario.alAceptar(() -> {
            int numero = LectorCampos.entero(campoNumero,
                    "El número de sala es obligatorio.", "El número de sala debe ser un entero.");
            int capacidad = LectorCampos.entero(campoCapacidad,
                    "La capacidad es obligatoria.", "La capacidad debe ser un número entero.");
            // El servicio arma el nombre "Sala N" y controla que no se repita (antes lo hacía la UI)
            Sala sala = getSistema().getFestivalServicio().registrarSala(numero, capacidad);
            recargar();
            getVentana().setEstado("Sala agregada: " + sala.getNombre());
            formulario.cerrar();
        });
        formulario.mostrar();
    }
}

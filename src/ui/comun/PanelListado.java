package ui.comun;

import servicios.SistemaFestival;
import ui.VentanaPrincipal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;
import java.util.regex.Pattern;

// Template Method para los paneles "título + buscador + tabla + botones"
// (ediciones, salas, películas, directores, actores y espectadores repetían las mismas ~45 líneas)
public abstract class PanelListado extends JPanel {
    private final VentanaPrincipal ventana;
    private final DefaultTableModel modeloTabla;
    private final JTable tabla;
    private final TableRowSorter<DefaultTableModel> filtro;
    private final JTextField campoBusqueda;

    protected PanelListado(VentanaPrincipal ventana, String titulo, String... columnas) {
        this.ventana = ventana;
        ComponentesUI.configurarPanel(this);

        modeloTabla = ComponentesUI.crearModeloTabla(columnas);
        tabla = ComponentesUI.crearTabla(modeloTabla);
        filtro = new TableRowSorter<>(modeloTabla);
        tabla.setRowSorter(filtro);
        campoBusqueda = new JTextField(20);
        ComponentesUI.alCambiarTexto(campoBusqueda, this::filtrar);

        add(crearEncabezado(titulo), BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    // Paso que define cada panel concreto: qué filas muestra
    protected abstract List<Object[]> obtenerFilas();

    protected final void recargar() {
        modeloTabla.setRowCount(0);
        obtenerFilas().forEach(modeloTabla::addRow);
    }

    protected final void agregarBotones(JButton... botones) {
        add(ComponentesUI.crearPanelBotones(botones), BorderLayout.SOUTH);
    }

    // Valor de la columna en la fila seleccionada (teniendo en cuenta el filtro), o null si no hay selección
    protected final Object getValorSeleccionado(int columna) {
        int fila = tabla.getSelectedRow();
        if (fila == -1) return null;
        return modeloTabla.getValueAt(tabla.convertRowIndexToModel(fila), columna);
    }

    protected final VentanaPrincipal getVentana() {
        return ventana;
    }

    protected final SistemaFestival getSistema() {
        return ventana.getSistema();
    }

    protected final JTable getTabla() {
        return tabla;
    }

    private JPanel crearEncabezado(String titulo) {
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBusqueda.add(new JLabel("Buscar:"));
        panelBusqueda.add(campoBusqueda);

        JPanel encabezado = new JPanel(new BorderLayout(10, 0));
        encabezado.add(ComponentesUI.crearTitulo(titulo), BorderLayout.WEST);
        encabezado.add(panelBusqueda, BorderLayout.EAST);
        return encabezado;
    }

    private void filtrar() {
        String texto = campoBusqueda.getText().trim();
        filtro.setRowFilter(texto.isEmpty() ? null : RowFilter.regexFilter("(?i)" + Pattern.quote(texto)));
    }
}

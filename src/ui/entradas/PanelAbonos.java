package ui.entradas;

import excepciones.FestivalException;
import modelo.abonos.Abono;
import modelo.funciones.Entrada;
import modelo.funciones.Funcion;
import servicios.AbonoServicio;
import servicios.FuncionServicio;
import servicios.PersonaServicio;
import ui.VentanaPrincipal;
import ui.comun.ComponentesUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

// Lista de abonos y sus reservas. Los diálogos de compra y de reserva están en sus propias clases
public class PanelAbonos extends JPanel {
    private static final int COLUMNA_CODIGO = 0;

    private final VentanaPrincipal ventana;
    private final AbonoServicio abonoServicio;
    private final FuncionServicio funcionServicio;
    private final PersonaServicio personaServicio;
    private final DefaultTableModel modeloAbonos;
    private final JTable tablaAbonos;
    private final DefaultTableModel modeloReservas;

    public PanelAbonos(VentanaPrincipal ventana, AbonoServicio abonoServicio, FuncionServicio funcionServicio,
                       PersonaServicio personaServicio) {
        this.ventana = ventana;
        this.abonoServicio = abonoServicio;
        this.funcionServicio = funcionServicio;
        this.personaServicio = personaServicio;
        ComponentesUI.configurarPanel(this);

        modeloAbonos = ComponentesUI.crearModeloTabla("Código", "Tipo", "Espectador", "Tarifa", "Precio", "Cobertura", "Reservas");
        tablaAbonos = ComponentesUI.crearTabla(modeloAbonos);
        tablaAbonos.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaAbonos.getColumnModel().getColumn(4).setPreferredWidth(70);
        tablaAbonos.getColumnModel().getColumn(5).setPreferredWidth(320);
        tablaAbonos.getColumnModel().getColumn(6).setPreferredWidth(55);
        tablaAbonos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarReservas();
        });
        modeloReservas = ComponentesUI.crearModeloTabla("Código", "Película", "Fecha", "Horario", "Detalle");

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setTopComponent(ComponentesUI.crearPanelConBorde("Abonos vendidos", tablaAbonos));
        splitPane.setBottomComponent(ComponentesUI.crearPanelConBorde("Reservas del abono seleccionado",
                ComponentesUI.crearTabla(modeloReservas)));

        add(ComponentesUI.crearTitulo("Abonos del Festival"), BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(ComponentesUI.crearPanelBotones(
                ComponentesUI.crearBoton("Comprar abono", this::comprarAbono),
                ComponentesUI.crearBoton("Reservar función", this::reservarFuncion),
                ComponentesUI.crearBoton("Cancelar reserva", this::cancelarReserva)), BorderLayout.SOUTH);

        cargarAbonos();
    }

    private void cargarAbonos() {
        modeloAbonos.setRowCount(0);
        for (Abono abono : abonoServicio.getAbonos()) {
            modeloAbonos.addRow(new Object[]{
                    abono.getCodigo(),
                    abono.getTipo(),
                    abono.getNombreEspectador(),
                    abono.getTarifa(),
                    ComponentesUI.formatearPrecio(abono.getPrecio()),
                    abono.getCobertura(),
                    abono.cantidadReservas()
            });
        }
        modeloReservas.setRowCount(0);
    }

    private void cargarReservas() {
        modeloReservas.setRowCount(0);
        Abono abono = getAbonoSeleccionado();
        if (abono == null) return;
        for (Entrada reserva : abono.getReservas()) {
            Funcion funcion = reserva.getFuncion();
            modeloReservas.addRow(new Object[]{
                    reserva.getCodigo(),
                    funcion.getTituloPelicula(),
                    funcion.getFecha().toString(),
                    funcion.getHorario().toString(),
                    funcion.getLugar() + " - " + reserva.getDetalle()
            });
        }
    }

    private Abono getAbonoSeleccionado() {
        int fila = tablaAbonos.getSelectedRow();
        if (fila < 0) return null;
        return abonoServicio.buscarAbono((String) modeloAbonos.getValueAt(fila, COLUMNA_CODIGO));
    }

    private void seleccionarAbono(Abono abono) {
        for (int i = 0; i < modeloAbonos.getRowCount(); i++) {
            if (modeloAbonos.getValueAt(i, COLUMNA_CODIGO).equals(abono.getCodigo())) {
                tablaAbonos.setRowSelectionInterval(i, i);
                return;
            }
        }
    }

    private void refrescarSeleccionando(Abono abono) {
        cargarAbonos();
        seleccionarAbono(abono);
    }

    private void comprarAbono() {
        if (!SelectorEspectador.asegurarEspectadores(ventana, personaServicio, "Comprar abono")) return;
        new DialogoComprarAbono(ventana, abonoServicio, funcionServicio, personaServicio).mostrar().ifPresent(abono -> {
            refrescarSeleccionando(abono);
            ventana.setEstado("Abono vendido: " + abono.getCodigo() + " - Precio: " + ComponentesUI.formatearPrecio(abono.getPrecio()));
        });
    }

    private void reservarFuncion() {
        Abono abono = getAbonoSeleccionado();
        if (abono == null) {
            ComponentesUI.avisar(ventana, "Seleccione un abono de la tabla.");
            return;
        }
        // Qué funciones cubre el abono lo resuelve el modelo, sin instanceof
        List<Funcion> funciones = abonoServicio.funcionesReservables(abono);
        if (funciones.isEmpty()) {
            ComponentesUI.avisar(ventana, "No hay funciones disponibles para este abono.");
            return;
        }
        new DialogoReservaAbono(ventana, abonoServicio, abono, funciones).mostrar().ifPresent(reserva -> {
            refrescarSeleccionando(abono);
            ventana.setEstado("Reserva realizada: " + reserva.getCodigo());
        });
    }

    private void cancelarReserva() {
        Abono abono = getAbonoSeleccionado();
        if (abono == null || !abono.tieneReservas()) {
            ComponentesUI.avisar(ventana, "Seleccione un abono que tenga reservas.");
            return;
        }

        Entrada[] reservas = abono.getReservas().toArray(new Entrada[0]);
        Entrada seleccion = (Entrada) JOptionPane.showInputDialog(ventana, "Reserva a cancelar:", "Cancelar reserva",
                JOptionPane.QUESTION_MESSAGE, null, reservas, reservas[0]);
        if (seleccion == null) return;

        try {
            abonoServicio.cancelarReserva(seleccion);
            refrescarSeleccionando(abono);
            ventana.setEstado("Reserva cancelada: " + seleccion.getCodigo());
        } catch (FestivalException ex) {
            ComponentesUI.mostrarError(ventana, "Error", ex.getMessage());
        }
    }
}

package ui.entradas;

import excepciones.FestivalException;
import modelo.Abono;
import modelo.Entrada;
import servicios.AbonoServicio;
import servicios.FuncionServicio;
import servicios.PersonaServicio;
import ui.VentanaPrincipal;
import ui.comun.ComponentesUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

// CORRECCION S8: los diálogos de compra y reserva pasaron a DialogoComprarAbono y DialogoReservaAbono.
public class PanelAbonos extends JPanel {

    private VentanaPrincipal ventana;
    private AbonoServicio abonoServicio;
    private FuncionServicio funcionServicio;
    private PersonaServicio personaServicio;
    private JTable tablaAbonos;
    private DefaultTableModel modeloAbonos;
    private DefaultTableModel modeloReservas;

    public PanelAbonos(VentanaPrincipal ventana, AbonoServicio abonoServicio, FuncionServicio funcionServicio, PersonaServicio personaServicio) {
        this.ventana = ventana;
        this.abonoServicio = abonoServicio;
        this.funcionServicio = funcionServicio;
        this.personaServicio = personaServicio;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(ComponentesUI.crearEncabezado("Abonos del Festival", null), BorderLayout.NORTH);
        construirPanelCentral();
        construirPanelBotones();

        cargarAbonos();
    }

    private void construirPanelCentral() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);

        modeloAbonos = ComponentesUI.crearModeloTabla("Código", "Tipo", "Espectador", "Tarifa", "Precio", "Cobertura", "Reservas");
        tablaAbonos = ComponentesUI.crearTabla(modeloAbonos);
        tablaAbonos.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaAbonos.getColumnModel().getColumn(4).setPreferredWidth(70);
        tablaAbonos.getColumnModel().getColumn(5).setPreferredWidth(320);
        tablaAbonos.getColumnModel().getColumn(6).setPreferredWidth(55);
        tablaAbonos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarReservas();
        });

        JPanel panelAbonos = new JPanel(new BorderLayout());
        panelAbonos.setBorder(BorderFactory.createTitledBorder("Abonos vendidos"));
        panelAbonos.add(new JScrollPane(tablaAbonos), BorderLayout.CENTER);

        modeloReservas = ComponentesUI.crearModeloTabla("Código", "Película", "Fecha", "Horario", "Detalle");
        JTable tablaReservas = ComponentesUI.crearTabla(modeloReservas);

        JPanel panelReservas = new JPanel(new BorderLayout());
        panelReservas.setBorder(BorderFactory.createTitledBorder("Reservas del abono seleccionado"));
        panelReservas.add(new JScrollPane(tablaReservas), BorderLayout.CENTER);

        splitPane.setTopComponent(panelAbonos);
        splitPane.setBottomComponent(panelReservas);
        add(splitPane, BorderLayout.CENTER);
    }

    private void construirPanelBotones() {
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JButton btnComprar = new JButton("Comprar abono");
        btnComprar.addActionListener(e -> comprarAbono());

        JButton btnReservar = new JButton("Reservar función");
        btnReservar.addActionListener(e -> reservarFuncion());

        JButton btnCancelar = new JButton("Cancelar reserva");
        btnCancelar.addActionListener(e -> cancelarReserva());

        panelBotones.add(btnComprar);
        panelBotones.add(btnReservar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarAbonos() {
        modeloAbonos.setRowCount(0);
        for (Abono a : abonoServicio.getAbonos()) {
            modeloAbonos.addRow(new Object[]{
                    a.getCodigo(),
                    a.getTipo(),
                    a.getEspectador().getNombre(),
                    a.getTarifa(),
                    ComponentesUI.formatearPrecio(a.getPrecio()),
                    a.getCobertura(),
                    a.getReservas().size()
            });
        }
        modeloReservas.setRowCount(0);
    }

    private void cargarReservas() {
        modeloReservas.setRowCount(0);
        Abono abono = getAbonoSeleccionado();
        if (abono == null) return;
        for (Entrada e : abono.getReservas()) {
            // CORRECCION S3: el lugar lo responde la función y el detalle la entrada, sin instanceof.
            modeloReservas.addRow(new Object[]{
                    e.getCodigo(),
                    e.getTituloPelicula(),
                    e.getFuncion().getFecha().toString(),
                    e.getFuncion().getHorario().toString(),
                    e.getFuncion().getLugar() + " - " + e.getDetalle()
            });
        }
    }

    private Abono getAbonoSeleccionado() {
        int fila = tablaAbonos.getSelectedRow();
        if (fila < 0) return null;
        String codigo = (String) modeloAbonos.getValueAt(tablaAbonos.convertRowIndexToModel(fila), 0);
        return abonoServicio.buscarAbono(codigo);
    }

    private void seleccionarAbono(String codigo) {
        for (int i = 0; i < modeloAbonos.getRowCount(); i++) {
            if (modeloAbonos.getValueAt(i, 0).equals(codigo)) {
                tablaAbonos.setRowSelectionInterval(i, i);
                return;
            }
        }
    }

    private void comprarAbono() {
        if (!ComponentesUI.asegurarEspectadores(ventana, personaServicio, "Comprar abono")) return;

        DialogoComprarAbono dialogo = new DialogoComprarAbono(ventana, abonoServicio, funcionServicio, personaServicio);
        if (dialogo.mostrar()) {
            Abono abono = dialogo.getAbonoComprado();
            cargarAbonos();
            seleccionarAbono(abono.getCodigo());
            ventana.setEstado("Abono vendido: " + abono.getCodigo() + " - Precio: " + ComponentesUI.formatearPrecio(abono.getPrecio()));
        }
    }

    private void reservarFuncion() {
        Abono abono = getAbonoSeleccionado();
        if (abono == null) {
            ComponentesUI.mostrarAviso(ventana, "Seleccione un abono de la tabla.");
            return;
        }
        if (abonoServicio.funcionesReservables(abono).isEmpty()) {
            ComponentesUI.mostrarAviso(ventana, "No hay funciones disponibles para este abono.");
            return;
        }

        DialogoReservaAbono dialogo = new DialogoReservaAbono(ventana, abonoServicio, funcionServicio, abono);
        if (dialogo.mostrar()) {
            cargarAbonos();
            seleccionarAbono(abono.getCodigo());
            ventana.setEstado("Reserva realizada: " + dialogo.getReserva().getCodigo());
        }
    }

    private void cancelarReserva() {
        Abono abono = getAbonoSeleccionado();
        if (abono == null || abono.getReservas().isEmpty()) {
            ComponentesUI.mostrarAviso(ventana, "Seleccione un abono que tenga reservas.");
            return;
        }

        // CORRECCION S10: el combo guarda las reservas; antes se separaba el código del texto elegido.
        JComboBox<Entrada> comboReserva = new JComboBox<>(abono.getReservas().toArray(new Entrada[0]));
        comboReserva.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    Entrada e = (Entrada) value;
                    setText(e.getCodigo() + " — " + ComponentesUI.describir(e.getFuncion()));
                }
                return this;
            }
        });
        int opcion = JOptionPane.showConfirmDialog(ventana, comboReserva, "Reserva a cancelar",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION) return;

        Entrada reserva = ComponentesUI.seleccionado(comboReserva);
        try {
            abonoServicio.cancelarReserva(reserva);
            cargarAbonos();
            seleccionarAbono(abono.getCodigo());
            ventana.setEstado("Reserva cancelada: " + reserva.getCodigo());
        } catch (FestivalException ex) {
            ComponentesUI.mostrarError(ventana, ex.getMessage());
        } catch (RuntimeException ex) {
            ComponentesUI.mostrarError(ventana, ComponentesUI.errorInesperado(ex));
        }
    }
}

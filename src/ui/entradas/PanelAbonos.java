package ui.entradas;

import modelo.*;
import servicios.AbonoServicio;
import ui.VentanaPrincipal;
import ui.comun.ComponentesUI;
import ui.comun.FormularioDialogo;
import ui.comun.LectorCampos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class PanelAbonos extends JPanel {
    private static final int COLUMNA_CODIGO = 0;

    private final VentanaPrincipal ventana;
    private final AbonoServicio abonoServicio;
    private final DefaultTableModel modeloAbonos;
    private final JTable tablaAbonos;
    private final DefaultTableModel modeloReservas;

    public PanelAbonos(VentanaPrincipal ventana) {
        this.ventana = ventana;
        this.abonoServicio = ventana.getSistema().getAbonoServicio();
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

    private void seleccionarAbono(String codigo) {
        for (int i = 0; i < modeloAbonos.getRowCount(); i++) {
            if (modeloAbonos.getValueAt(i, COLUMNA_CODIGO).equals(codigo)) {
                tablaAbonos.setRowSelectionInterval(i, i);
                return;
            }
        }
    }

    private void refrescarSeleccionando(Abono abono) {
        cargarAbonos();
        seleccionarAbono(abono.getCodigo());
    }

    // ========== COMPRAR ABONO ==========

    private void comprarAbono() {
        if (!SelectorEspectador.asegurarEspectadores(ventana, "Comprar abono")) return;

        FormularioDialogo formulario = new FormularioDialogo(ventana, "Comprar abono", 480, 420);
        formulario.setTextoAceptar("Comprar");
        JComboBox<String> comboEspectador = new JComboBox<>();
        formulario.agregarCampo("Espectador:", SelectorEspectador.crear(ventana, comboEspectador));
        JComboBox<TipoAbono> comboTipo = formulario.agregarCampo("Tipo de abono:", new JComboBox<>(TipoAbono.values()));
        JComboBox<TipoTarifa> comboTarifa = formulario.agregarCampo("Tarifa:", new JComboBox<>(TipoTarifa.values()));
        JLabel labelPrecio = formulario.agregarCampo("Precio:", new JLabel());
        labelPrecio.setFont(new Font("SansSerif", Font.BOLD, 13));

        // Lista de días para el abono de 3 días
        List<LocalDate> fechas = ventana.getSistema().getFuncionServicio().getFechasConFuncionesPresenciales();
        JList<LocalDate> listaDias = new JList<>(fechas.toArray(new LocalDate[0]));
        listaDias.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listaDias.setVisibleRowCount(5);
        JPanel panelDias = new JPanel(new BorderLayout());
        panelDias.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 20, 0, 20),
                BorderFactory.createTitledBorder("Elegí 3 días (Ctrl + clic)")));
        panelDias.add(new JScrollPane(listaDias), BorderLayout.CENTER);
        formulario.agregarDebajo(panelDias);

        // El precio lo sabe el tipo de abono (antes era un if/else sobre Strings en el servicio)
        Runnable actualizar = () -> {
            TipoAbono tipo = (TipoAbono) comboTipo.getSelectedItem();
            listaDias.setEnabled(tipo == TipoAbono.TRES_DIAS);
            labelPrecio.setText(ComponentesUI.formatearPrecio(tipo.precioCon((TipoTarifa) comboTarifa.getSelectedItem())));
        };
        comboTipo.addActionListener(e -> actualizar.run());
        comboTarifa.addActionListener(e -> actualizar.run());
        actualizar.run();

        formulario.alAceptar(() -> {
            String nombre = (String) comboEspectador.getSelectedItem();
            TipoTarifa tarifa = (TipoTarifa) comboTarifa.getSelectedItem();
            Abono abono = switch ((TipoAbono) comboTipo.getSelectedItem()) {
                case TRES_DIAS -> abonoServicio.comprarAbonoTresDias(nombre, tarifa, listaDias.getSelectedValuesList());
                case FULL -> abonoServicio.comprarAbonoFull(nombre, tarifa);
                case STREAMING -> abonoServicio.comprarAbonoStreaming(nombre, tarifa);
            };
            refrescarSeleccionando(abono);
            ventana.setEstado("Abono vendido: " + abono.getCodigo() + " - Precio: " + ComponentesUI.formatearPrecio(abono.getPrecio()));
            formulario.cerrar();
        });
        formulario.mostrar();
    }

    // ========== RESERVAR FUNCIÓN ==========

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

        FormularioDialogo formulario = new FormularioDialogo(ventana, "Reservar con " + abono.getCodigo(), 520, 280);
        formulario.setTextoAceptar("Reservar");
        formulario.agregarCampo("Espectador:", new JLabel(abono.getNombreEspectador() + " (" + abono.getTipo() + ")"));
        JComboBox<String> comboFuncion = formulario.agregarCampo("Función:", new JComboBox<>(funciones.stream()
                .map(funcion -> funcion.getResumen() + " — " + funcion.getModalidad() + " (" + funcion.getLugar() + ")")
                .toArray(String[]::new)));
        JLabel labelOcupadas = formulario.agregarCampo("Butacas ocupadas:", new JLabel());
        labelOcupadas.setForeground(ComponentesUI.COLOR_AVISO);
        labelOcupadas.setFont(ComponentesUI.FUENTE_AYUDA);
        JTextField campoButaca = formulario.agregarCampo("Número de butaca:", new JTextField());
        formulario.encadenarEnter(campoButaca);

        Runnable actualizar = () -> {
            Funcion funcion = funciones.get(comboFuncion.getSelectedIndex());
            labelOcupadas.setText(funcion.getEstadoOcupacion());
            campoButaca.setEnabled(funcion.requiereButaca());
            if (!funcion.requiereButaca()) campoButaca.setText("");
        };
        comboFuncion.addActionListener(e -> actualizar.run());
        actualizar.run();

        formulario.alAceptar(() -> {
            Funcion funcion = funciones.get(comboFuncion.getSelectedIndex());
            int butaca = funcion.requiereButaca()
                    ? LectorCampos.entero(campoButaca, "El número de butaca es obligatorio.", "El número de butaca debe ser un entero.")
                    : SolicitudEntrada.SIN_BUTACA;
            Entrada reserva = abonoServicio.reservar(abono.getCodigo(), funcion, butaca);
            refrescarSeleccionando(abono);
            ventana.setEstado("Reserva realizada: " + reserva.getCodigo());
            formulario.cerrar();
        });
        formulario.mostrar();
    }

    // ========== CANCELAR RESERVA ==========

    private void cancelarReserva() {
        Abono abono = getAbonoSeleccionado();
        if (abono == null || !abono.tieneReservas()) {
            ComponentesUI.avisar(ventana, "Seleccione un abono que tenga reservas.");
            return;
        }

        // Se elige la Entrada directamente (antes se armaba un texto y se lo volvía a partir con split)
        Entrada[] reservas = abono.getReservas().toArray(new Entrada[0]);
        Entrada seleccion = (Entrada) JOptionPane.showInputDialog(ventana, "Reserva a cancelar:", "Cancelar reserva",
                JOptionPane.QUESTION_MESSAGE, null, reservas, reservas[0]);
        if (seleccion == null) return;

        try {
            abonoServicio.cancelarReserva(seleccion.getCodigo());
            refrescarSeleccionando(abono);
            ventana.setEstado("Reserva cancelada: " + seleccion.getCodigo());
        } catch (RuntimeException ex) {
            ComponentesUI.mostrarError(ventana, "Error", ex.getMessage());
        }
    }
}

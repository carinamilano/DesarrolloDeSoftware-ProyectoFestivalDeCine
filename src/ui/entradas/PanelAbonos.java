package ui.entradas;

import modelo.*;
import ui.VentanaPrincipal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PanelAbonos extends JPanel {

    private static final String[] TIPOS_ABONO = {"Abono 3 días", "Abono Full", "Abono Streaming"};
    private static final String[] CLAVES_ABONO = {"TRES_DIAS", "FULL", "STREAMING"};

    private VentanaPrincipal ventana;
    private JTable tablaAbonos;
    private DefaultTableModel modeloAbonos;
    private DefaultTableModel modeloReservas;

    public PanelAbonos(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        construirPanelSuperior();
        construirPanelCentral();
        construirPanelBotones();

        cargarAbonos();
    }

    private void construirPanelSuperior() {
        JLabel titulo = new JLabel("Abonos del Festival");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(titulo, BorderLayout.NORTH);
    }

    private void construirPanelCentral() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);

        // --- Tabla de abonos (arriba) ---
        String[] columnasAbonos = {"Código", "Tipo", "Espectador", "Tarifa", "Precio", "Cobertura", "Reservas"};
        modeloAbonos = new DefaultTableModel(columnasAbonos, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaAbonos = new JTable(modeloAbonos);
        tablaAbonos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaAbonos.getTableHeader().setReorderingAllowed(false);
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

        // --- Tabla de reservas del abono seleccionado (abajo) ---
        String[] columnasReservas = {"Código", "Película", "Fecha", "Horario", "Detalle"};
        modeloReservas = new DefaultTableModel(columnasReservas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tablaReservas = new JTable(modeloReservas);
        tablaReservas.getTableHeader().setReorderingAllowed(false);

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
        for (Abono a : ventana.getEntradaServicio().getAbonos()) {
            modeloAbonos.addRow(new Object[]{
                    a.getCodigo(),
                    a.getTipo(),
                    a.getEspectador().getNombre(),
                    a.getTarifa(),
                    String.format("$ %.2f", a.getPrecio()),
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
            String detalle;
            if (e instanceof EntradaPresencial) {
                detalle = ((FuncionPresencial) e.getFuncion()).getSala().getNombre()
                        + " - Butaca " + ((EntradaPresencial) e).getButaca();
            } else {
                detalle = "Enlace: " + ((EntradaStreaming) e).getEnlaceAcceso();
            }
            modeloReservas.addRow(new Object[]{
                    e.getCodigo(),
                    e.getFuncion().getPelicula().getTitulo(),
                    e.getFuncion().getFecha().toString(),
                    e.getFuncion().getHorario().toString(),
                    detalle
            });
        }
    }

    private Abono getAbonoSeleccionado() {
        int fila = tablaAbonos.getSelectedRow();
        if (fila < 0) return null;
        String codigo = (String) modeloAbonos.getValueAt(fila, 0);
        return ventana.getEntradaServicio().buscarAbono(codigo);
    }

    private void seleccionarAbono(String codigo) {
        for (int i = 0; i < modeloAbonos.getRowCount(); i++) {
            if (modeloAbonos.getValueAt(i, 0).equals(codigo)) {
                tablaAbonos.setRowSelectionInterval(i, i);
                return;
            }
        }
    }

    // ========== COMPRAR ABONO ==========

    private void comprarAbono() {
        List<Espectador> espectadores = ventana.getEntradaServicio().getEspectadores();
        if (espectadores.isEmpty()) {
            JOptionPane.showMessageDialog(ventana,
                    "No hay espectadores registrados. Registre uno desde Entradas > Espectadores.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] nombresEspectadores = espectadores.stream()
                .map(Espectador::getNombre).toArray(String[]::new);
        List<LocalDate> fechas = ventana.getEntradaServicio().getFechasConFuncionesPresenciales();

        JDialog dialog = new JDialog(ventana, "Comprar abono", true);
        dialog.setSize(480, 420);
        dialog.setLocationRelativeTo(ventana);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel panelCampos = new JPanel(new GridLayout(4, 2, 10, 10));
        panelCampos.setBorder(BorderFactory.createEmptyBorder(20, 20, 5, 20));

        JComboBox<String> comboEspectador = new JComboBox<>(nombresEspectadores);
        JComboBox<String> comboTipo = new JComboBox<>(TIPOS_ABONO);
        JComboBox<TipoTarifa> comboTarifa = new JComboBox<>(TipoTarifa.values());
        JLabel labelPrecio = new JLabel();
        labelPrecio.setFont(new Font("SansSerif", Font.BOLD, 13));

        panelCampos.add(new JLabel("Espectador:"));
        panelCampos.add(comboEspectador);
        panelCampos.add(new JLabel("Tipo de abono:"));
        panelCampos.add(comboTipo);
        panelCampos.add(new JLabel("Tarifa:"));
        panelCampos.add(comboTarifa);
        panelCampos.add(new JLabel("Precio:"));
        panelCampos.add(labelPrecio);

        // Lista de días para el abono de 3 días
        JList<LocalDate> listaDias = new JList<>(fechas.toArray(new LocalDate[0]));
        listaDias.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listaDias.setVisibleRowCount(5);
        JPanel panelDias = new JPanel(new BorderLayout());
        panelDias.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 20, 0, 20),
                BorderFactory.createTitledBorder("Elegí 3 días (Ctrl + clic)")));
        panelDias.add(new JScrollPane(listaDias), BorderLayout.CENTER);

        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.add(panelCampos, BorderLayout.NORTH);
        panelCentro.add(panelDias, BorderLayout.CENTER);

        Runnable actualizar = () -> {
            int idx = comboTipo.getSelectedIndex();
            listaDias.setEnabled(idx == 0);
            double precio = ventana.getEntradaServicio().calcularPrecioAbono(
                    CLAVES_ABONO[idx], (TipoTarifa) comboTarifa.getSelectedItem());
            labelPrecio.setText(String.format("$ %.2f", precio));
        };
        comboTipo.addActionListener(e -> actualizar.run());
        comboTarifa.addActionListener(e -> actualizar.run());
        actualizar.run();

        JLabel labelError = new JLabel(" ");
        labelError.setForeground(Color.RED);
        labelError.setFont(new Font("SansSerif", Font.PLAIN, 12));
        labelError.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAceptar = new JButton("Comprar");
        JButton btnCancelar = new JButton("Cancelar");
        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);

        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.add(labelError, BorderLayout.CENTER);
        panelSur.add(panelBotones, BorderLayout.SOUTH);

        btnCancelar.addActionListener(e -> dialog.dispose());

        btnAceptar.addActionListener(e -> {
            String nombre = (String) comboEspectador.getSelectedItem();
            TipoTarifa tarifa = (TipoTarifa) comboTarifa.getSelectedItem();
            try {
                Abono abono;
                switch (comboTipo.getSelectedIndex()) {
                    case 0 -> {
                        List<LocalDate> dias = listaDias.getSelectedValuesList();
                        if (dias.size() != 3) {
                            labelError.setText("Debe seleccionar exactamente 3 días.");
                            return;
                        }
                        abono = ventana.getEntradaServicio().comprarAbonoTresDias(nombre, tarifa, new ArrayList<>(dias));
                    }
                    case 1 -> abono = ventana.getEntradaServicio().comprarAbonoFull(nombre, tarifa);
                    default -> abono = ventana.getEntradaServicio().comprarAbonoStreaming(nombre, tarifa);
                }
                cargarAbonos();
                seleccionarAbono(abono.getCodigo());
                ventana.setEstado("Abono vendido: " + abono.getCodigo()
                        + String.format(" - Precio: $ %.2f", abono.getPrecio()));
                dialog.dispose();
            } catch (Exception ex) {
                labelError.setText("Error: " + ex.getMessage());
            }
        });

        dialog.add(panelCentro, BorderLayout.CENTER);
        dialog.add(panelSur, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ========== RESERVAR FUNCIÓN ==========

    private void reservarFuncion() {
        Abono abono = getAbonoSeleccionado();
        if (abono == null) {
            JOptionPane.showMessageDialog(ventana,
                    "Seleccione un abono de la tabla.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Solo mostramos las funciones que cubre el abono y que todavía no pasaron
        List<Funcion> funcionesCubiertas = new ArrayList<>();
        for (Funcion f : ventana.getEntradaServicio().getFunciones()) {
            if (abono.cubre(f) && !f.getFecha().isBefore(LocalDate.now())) {
                funcionesCubiertas.add(f);
            }
        }
        if (funcionesCubiertas.isEmpty()) {
            JOptionPane.showMessageDialog(ventana,
                    "No hay funciones disponibles para este abono.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] nombresFunciones = funcionesCubiertas.stream()
                .map(f -> f.getPelicula().getTitulo() + " — " + f.getFecha() + " " + f.getHorario()
                        + (f instanceof FuncionPresencial ? " — " + ((FuncionPresencial) f).getSala().getNombre() : " — Streaming"))
                .toArray(String[]::new);

        JDialog dialog = new JDialog(ventana, "Reservar con " + abono.getCodigo(), true);
        dialog.setSize(520, 280);
        dialog.setLocationRelativeTo(ventana);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel panelCampos = new JPanel(new GridLayout(4, 2, 10, 10));
        panelCampos.setBorder(BorderFactory.createEmptyBorder(20, 20, 5, 20));

        JComboBox<String> comboFuncion = new JComboBox<>(nombresFunciones);
        JLabel labelOcupadas = new JLabel();
        labelOcupadas.setForeground(new Color(150, 50, 50));
        labelOcupadas.setFont(new Font("SansSerif", Font.ITALIC, 12));
        JTextField campoButaca = new JTextField();

        panelCampos.add(new JLabel("Espectador:"));
        panelCampos.add(new JLabel(abono.getEspectador().getNombre() + " (" + abono.getTipo() + ")"));
        panelCampos.add(new JLabel("Función:"));
        panelCampos.add(comboFuncion);
        panelCampos.add(new JLabel("Butacas ocupadas:"));
        panelCampos.add(labelOcupadas);
        panelCampos.add(new JLabel("Número de butaca:"));
        panelCampos.add(campoButaca);

        Runnable actualizar = () -> {
            Funcion f = funcionesCubiertas.get(comboFuncion.getSelectedIndex());
            if (f instanceof FuncionPresencial) {
                List<Integer> ocupadas = ((FuncionPresencial) f).getButacasOcupadas();
                labelOcupadas.setText(ocupadas.isEmpty() ? "ninguna" : ocupadas.toString());
                campoButaca.setEnabled(true);
            } else {
                labelOcupadas.setText("(streaming, sin butaca)");
                campoButaca.setText("");
                campoButaca.setEnabled(false);
            }
        };
        comboFuncion.addActionListener(e -> actualizar.run());
        actualizar.run();

        JLabel labelError = new JLabel(" ");
        labelError.setForeground(Color.RED);
        labelError.setFont(new Font("SansSerif", Font.PLAIN, 12));
        labelError.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAceptar = new JButton("Reservar");
        JButton btnCancelar = new JButton("Cancelar");
        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);

        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.add(labelError, BorderLayout.CENTER);
        panelSur.add(panelBotones, BorderLayout.SOUTH);

        campoButaca.addActionListener(e -> btnAceptar.doClick());
        btnCancelar.addActionListener(e -> dialog.dispose());

        btnAceptar.addActionListener(e -> {
            Funcion funcion = funcionesCubiertas.get(comboFuncion.getSelectedIndex());
            try {
                Entrada reserva;
                if (funcion instanceof FuncionPresencial) {
                    String butacaStr = campoButaca.getText().trim();
                    if (butacaStr.isEmpty()) {
                        labelError.setText("El número de butaca es obligatorio.");
                        campoButaca.requestFocus();
                        return;
                    }
                    int butaca;
                    try {
                        butaca = Integer.parseInt(butacaStr);
                    } catch (NumberFormatException ex) {
                        labelError.setText("El número de butaca debe ser un entero.");
                        campoButaca.requestFocus();
                        return;
                    }
                    reserva = ventana.getEntradaServicio()
                            .reservarPresencialConAbono(abono.getCodigo(), (FuncionPresencial) funcion, butaca);
                } else {
                    reserva = ventana.getEntradaServicio()
                            .reservarStreamingConAbono(abono.getCodigo(), (FuncionStreaming) funcion);
                }
                cargarAbonos();
                seleccionarAbono(abono.getCodigo());
                ventana.setEstado("Reserva realizada: " + reserva.getCodigo());
                dialog.dispose();
            } catch (Exception ex) {
                labelError.setText("Error: " + ex.getMessage());
            }
        });

        dialog.add(panelCampos, BorderLayout.CENTER);
        dialog.add(panelSur, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ========== CANCELAR RESERVA ==========

    private void cancelarReserva() {
        Abono abono = getAbonoSeleccionado();
        if (abono == null || abono.getReservas().isEmpty()) {
            JOptionPane.showMessageDialog(ventana,
                    "Seleccione un abono que tenga reservas.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] opciones = abono.getReservas().stream()
                .map(e -> e.getCodigo() + " — " + e.getFuncion().getPelicula().getTitulo()
                        + " — " + e.getFuncion().getFecha() + " " + e.getFuncion().getHorario())
                .toArray(String[]::new);

        String seleccion = (String) JOptionPane.showInputDialog(ventana,
                "Reserva a cancelar:", "Cancelar reserva",
                JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
        if (seleccion == null) return;

        String codigo = seleccion.split(" — ")[0];
        try {
            ventana.getEntradaServicio().cancelarReserva(codigo);
            cargarAbonos();
            seleccionarAbono(abono.getCodigo());
            ventana.setEstado("Reserva cancelada: " + codigo);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(ventana, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

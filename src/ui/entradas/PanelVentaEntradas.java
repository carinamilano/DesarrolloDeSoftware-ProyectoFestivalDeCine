package ui.entradas;

import modelo.*;
import ui.VentanaPrincipal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelVentaEntradas extends JPanel {

    private VentanaPrincipal ventana;
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public PanelVentaEntradas(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        construirPanelSuperior();
        construirTabla();
        construirPanelBotones();

        cargarDatos();
    }

    private void construirPanelSuperior() {
        JLabel titulo = new JLabel("Venta de Entradas");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(titulo, BorderLayout.NORTH);
    }

    private void construirTabla() {
        String[] columnas = {"Código", "Película", "Tipo", "Fecha", "Espectador", "Detalle", "Precio"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(tabla);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void construirPanelBotones() {
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JButton btnVenderPresencial = new JButton("Vender entrada presencial");
        btnVenderPresencial.addActionListener(e -> venderEntradaPresencial());

        JButton btnVenderStreaming = new JButton("Vender entrada streaming");
        btnVenderStreaming.addActionListener(e -> venderEntradaStreaming());

        panelBotones.add(btnVenderPresencial);
        panelBotones.add(btnVenderStreaming);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        List<Entrada> entradas = ventana.getEntradaServicio().getEntradas();
        for (Entrada entrada : entradas) {
            String tipo;
            String detalle;
            if (entrada instanceof EntradaPresencial) {
                tipo = "Presencial";
                detalle = "Butaca: " + ((EntradaPresencial) entrada).getButaca();
            } else {
                tipo = "Streaming";
                detalle = "Enlace: " + ((EntradaStreaming) entrada).getEnlaceAcceso();
            }
            if (entrada.esReservaDeAbono()) {
                tipo += " (abono " + entrada.getAbono().getCodigo() + ")";
            }
            modeloTabla.addRow(new Object[]{
                    entrada.getCodigo(),
                    entrada.getFuncion().getPelicula().getTitulo(),
                    tipo,
                    entrada.getFuncion().getFecha().toString(),
                    entrada.getEspectador().getNombre(),
                    detalle,
                    String.format("$ %.2f", entrada.getPrecio())
            });
        }
    }

    private void venderEntradaPresencial() {
        List<Funcion> funciones = ventana.getEntradaServicio().getFunciones();

        List<FuncionPresencial> funcionesPresenciales = new java.util.ArrayList<>();
        for (Funcion f : funciones) {
            if (f instanceof FuncionPresencial) {
                funcionesPresenciales.add((FuncionPresencial) f);
            }
        }

        if (funcionesPresenciales.isEmpty()) {
            JOptionPane.showMessageDialog(ventana,
                    "No hay funciones presenciales disponibles.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!PanelEspectadores.asegurarEspectadores(ventana, "Vender entrada presencial")) return;

        String[] nombresFunciones = funcionesPresenciales.stream()
                .map(f -> f.getPelicula().getTitulo() + " — " + f.getFecha() + " " + f.getHorario() + " — " + f.getSala().getNombre() + " (" + f.getSala().getCantidadButacas() + " butacas)")
                .toArray(String[]::new);

        JDialog dialog = new JDialog(ventana, "Vender entrada presencial", true);
        dialog.setSize(500, 360);
        dialog.setLocationRelativeTo(ventana);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel panelCampos = new JPanel(new GridLayout(5, 2, 10, 10));
        panelCampos.setBorder(BorderFactory.createEmptyBorder(20, 20, 5, 20));

        JComboBox<String> comboEspectador = new JComboBox<>();
        JComboBox<String> comboFuncion = new JComboBox<>(nombresFunciones);
        JTextField campoButaca = new JTextField();
        JComboBox<TipoTarifa> comboTarifa = new JComboBox<>(TipoTarifa.values());

        JLabel labelOcupadas = new JLabel("ninguna");
        labelOcupadas.setForeground(new Color(150, 50, 50));
        labelOcupadas.setFont(new Font("SansSerif", Font.ITALIC, 12));

        panelCampos.add(new JLabel("Espectador:"));
        panelCampos.add(PanelEspectadores.crearSelectorEspectador(ventana, comboEspectador));
        panelCampos.add(new JLabel("Función:"));
        panelCampos.add(comboFuncion);
        panelCampos.add(new JLabel("Butacas ocupadas:"));
        panelCampos.add(labelOcupadas);
        panelCampos.add(new JLabel("Número de butaca:"));
        panelCampos.add(campoButaca);
        panelCampos.add(new JLabel("Tarifa:"));
        panelCampos.add(comboTarifa);

        // Actualizar butacas ocupadas cuando cambia la función
        comboFuncion.addActionListener(e -> {
            int idx = comboFuncion.getSelectedIndex();
            FuncionPresencial fp = funcionesPresenciales.get(idx);
            List<Integer> ocupadas = fp.getButacasOcupadas();
            labelOcupadas.setText(ocupadas.isEmpty() ? "ninguna" : ocupadas.toString());
        });

        // Disparar una vez al abrir
        if (!funcionesPresenciales.isEmpty()) {
            List<Integer> ocupadas = funcionesPresenciales.get(0).getButacasOcupadas();
            labelOcupadas.setText(ocupadas.isEmpty() ? "ninguna" : ocupadas.toString());
        }

        JLabel labelError = new JLabel(" ");
        labelError.setForeground(Color.RED);
        labelError.setFont(new Font("SansSerif", Font.PLAIN, 12));
        labelError.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);

        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.add(labelError, BorderLayout.CENTER);
        panelSur.add(panelBotones, BorderLayout.SOUTH);

        campoButaca.addActionListener(e -> btnAceptar.doClick());
        btnCancelar.addActionListener(e -> dialog.dispose());

        btnAceptar.addActionListener(e -> {
            String nombreEspectador = (String) comboEspectador.getSelectedItem();

            String butacaStr = campoButaca.getText().trim();
            if (butacaStr.isEmpty()) {
                labelError.setText("El número de butaca es obligatorio.");
                campoButaca.requestFocus();
                return;
            }

            int butaca;
            try {
                butaca = Integer.parseInt(butacaStr);
                if (butaca <= 0) {
                    labelError.setText("El número de butaca debe ser mayor a 0.");
                    campoButaca.requestFocus();
                    return;
                }
            } catch (NumberFormatException ex) {
                labelError.setText("El número de butaca debe ser un entero.");
                campoButaca.requestFocus();
                return;
            }

            int indiceFuncion = comboFuncion.getSelectedIndex();
            FuncionPresencial funcion = funcionesPresenciales.get(indiceFuncion);

            if (butaca > funcion.getSala().getCantidadButacas()) {
                labelError.setText("Butaca inválida. La sala tiene " + funcion.getSala().getCantidadButacas() + " butacas.");
                campoButaca.requestFocus();
                return;
            }

            try {
                EntradaPresencial entrada = ventana.getEntradaServicio()
                        .venderEntradaPresencial(nombreEspectador, funcion, butaca, (TipoTarifa) comboTarifa.getSelectedItem());
                cargarDatos();
                ventana.setEstado("Entrada vendida: " + entrada.getCodigo() + String.format(" - Precio: $ %.2f", entrada.getPrecio()));

                // Actualizar butacas ocupadas
                List<Integer> ocupadas = funcion.getButacasOcupadas();
                labelOcupadas.setText(ocupadas.isEmpty() ? "ninguna" : ocupadas.toString());

                int otraEntrada = JOptionPane.showConfirmDialog(
                        dialog,
                        "Entrada vendida correctamente.\n¿Desea comprar otra butaca para esta función?",
                        "Entrada vendida",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (otraEntrada == JOptionPane.YES_OPTION) {
                    campoButaca.setText("");
                    labelError.setText(" ");
                    campoButaca.requestFocus();
                } else {
                    dialog.dispose();
                }

            } catch (Exception ex) {
                labelError.setText("Error: " + ex.getMessage());
            }
        });

        dialog.add(panelCampos, BorderLayout.CENTER);
        dialog.add(panelSur, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void venderEntradaStreaming() {
        List<Funcion> funciones = ventana.getEntradaServicio().getFunciones();

        List<FuncionStreaming> funcionesStreaming = new java.util.ArrayList<>();
        for (Funcion f : funciones) {
            if (f instanceof FuncionStreaming) {
                funcionesStreaming.add((FuncionStreaming) f);
            }
        }

        if (funcionesStreaming.isEmpty()) {
            JOptionPane.showMessageDialog(ventana,
                    "No hay funciones streaming disponibles.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!PanelEspectadores.asegurarEspectadores(ventana, "Vender entrada streaming")) return;

        String[] nombresFunciones = funcionesStreaming.stream()
                .map(f -> f.getPelicula().getTitulo() + " — " + f.getFecha() + " " + f.getHorario())
                .toArray(String[]::new);

        JDialog dialog = new JDialog(ventana, "Vender entrada streaming", true);
        dialog.setSize(450, 260);
        dialog.setLocationRelativeTo(ventana);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel panelCampos = new JPanel(new GridLayout(3, 2, 10, 10));
        panelCampos.setBorder(BorderFactory.createEmptyBorder(20, 20, 5, 20));

        JComboBox<String> comboEspectador = new JComboBox<>();
        JComboBox<String> comboFuncion = new JComboBox<>(nombresFunciones);
        JComboBox<TipoTarifa> comboTarifaStreaming = new JComboBox<>(TipoTarifa.values());

        panelCampos.add(new JLabel("Espectador:"));
        panelCampos.add(PanelEspectadores.crearSelectorEspectador(ventana, comboEspectador));
        panelCampos.add(new JLabel("Función:"));
        panelCampos.add(comboFuncion);
        panelCampos.add(new JLabel("Tarifa:"));
        panelCampos.add(comboTarifaStreaming);

        JLabel labelError = new JLabel(" ");
        labelError.setForeground(Color.RED);
        labelError.setFont(new Font("SansSerif", Font.PLAIN, 12));
        labelError.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);

        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.add(labelError, BorderLayout.CENTER);
        panelSur.add(panelBotones, BorderLayout.SOUTH);

        btnCancelar.addActionListener(e -> dialog.dispose());

        btnAceptar.addActionListener(e -> {
            String nombreEspectador = (String) comboEspectador.getSelectedItem();

            try {
                int indiceFuncion = comboFuncion.getSelectedIndex();
                FuncionStreaming funcion = funcionesStreaming.get(indiceFuncion);
                EntradaStreaming entrada = ventana.getEntradaServicio()
                        .venderEntradaStreaming(nombreEspectador, funcion, (TipoTarifa) comboTarifaStreaming.getSelectedItem());
                cargarDatos();
                ventana.setEstado("Entrada streaming vendida: " + entrada.getCodigo() + String.format(" - Precio: $ %.2f", entrada.getPrecio()));
                dialog.dispose();
            } catch (Exception ex) {
                labelError.setText("Error: " + ex.getMessage());
            }
        });

        dialog.add(panelCampos, BorderLayout.CENTER);
        dialog.add(panelSur, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
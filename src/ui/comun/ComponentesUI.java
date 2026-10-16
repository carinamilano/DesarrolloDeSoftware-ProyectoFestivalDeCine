package ui.comun;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

// Piezas de interfaz que antes se construían copiando las mismas líneas en cada panel
public final class ComponentesUI {
    public static final Font FUENTE_TITULO = new Font("SansSerif", Font.BOLD, 16);
    public static final Font FUENTE_MENSAJE = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font FUENTE_AYUDA = new Font("SansSerif", Font.ITALIC, 12);
    public static final Font FUENTE_RESULTADOS = new Font("SansSerif", Font.PLAIN, 13);
    public static final Color COLOR_AVISO = new Color(150, 50, 50);

    private ComponentesUI() {
    }

    public static void configurarPanel(JPanel panel) {
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    public static JLabel crearTitulo(String texto) {
        JLabel titulo = new JLabel(texto);
        titulo.setFont(FUENTE_TITULO);
        return titulo;
    }

    public static DefaultTableModel crearModeloTabla(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    public static JTable crearTabla(DefaultTableModel modelo) {
        JTable tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);
        return tabla;
    }

    public static JPanel crearPanelConBorde(String titulo, JComponent contenido) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(titulo));
        panel.add(new JScrollPane(contenido), BorderLayout.CENTER);
        return panel;
    }

    public static JTextArea crearAreaResultados() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(FUENTE_RESULTADOS);
        area.setMargin(new Insets(8, 8, 8, 8));
        return area;
    }

    public static JButton crearBoton(String texto, Runnable accion) {
        JButton boton = new JButton(texto);
        boton.addActionListener(e -> accion.run());
        return boton;
    }

    public static JPanel crearPanelBotones(JButton... botones) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        for (JButton boton : botones) {
            panel.add(boton);
        }
        return panel;
    }

    public static void alCambiarTexto(JTextField campo, Runnable accion) {
        campo.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { accion.run(); }
            public void removeUpdate(DocumentEvent e) { accion.run(); }
            public void changedUpdate(DocumentEvent e) { accion.run(); }
        });
    }

    public static String formatearPrecio(double precio) {
        return String.format("$ %.2f", precio);
    }

    public static void avisar(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    public static void informar(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Aviso", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void mostrarError(Component padre, String titulo, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, titulo, JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirmar(Component padre, String mensaje, String titulo) {
        return JOptionPane.showConfirmDialog(padre, mensaje, titulo,
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }
}

package ui.comun;

import excepciones.FestivalException;
import modelo.*;
import servicios.PersonaServicio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;
import java.util.regex.Pattern;

/* CORRECCION S8/S9: piezas de la interfaz que se repetían en todos los paneles y diálogos.
También reúne los métodos static que antes estaban en PanelEspectadores y usaban otros paneles.*/
public final class ComponentesUI {

    private ComponentesUI() {
    }


    public static JLabel crearTitulo(String texto) {
        JLabel titulo = new JLabel(texto);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        return titulo;
    }

    public static JPanel crearEncabezado(String titulo, JComponent derecha) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.add(crearTitulo(titulo), BorderLayout.WEST);
        if (derecha != null) panel.add(derecha, BorderLayout.EAST);
        return panel;
    }

    public static DefaultTableModel crearModeloTabla(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
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


    /* CORRECCION B2: el texto se busca literal (Pattern.quote); antes escribir "(" o "[" lanzaba
    PatternSyntaxException porque se usaba como expresión regular.*/
    public static JPanel crearBuscador(TableRowSorter<DefaultTableModel> sorter) {
        JTextField campo = new JTextField(20);
        campo.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrar(sorter, campo.getText()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrar(sorter, campo.getText()); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(sorter, campo.getText()); }
        });
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(new JLabel("Buscar:"));
        panel.add(campo);
        return panel;
    }

    private static void filtrar(TableRowSorter<DefaultTableModel> sorter, String texto) {
        if (texto.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto.trim())));
        }
    }

    public static JPanel crearPanelCampos(int filas) {
        JPanel panel = new JPanel(new GridLayout(filas, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 5, 20));
        return panel;
    }

    public static JLabel crearLabelError() {
        JLabel labelError = new JLabel(" ");
        labelError.setForeground(Color.RED);
        labelError.setFont(new Font("SansSerif", Font.PLAIN, 12));
        labelError.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        return labelError;
    }

    public static JPanel crearPanelSur(JLabel labelError, JButton... botones) {
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        for (JButton b : botones) panelBotones.add(b);
        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.add(labelError, BorderLayout.CENTER);
        panelSur.add(panelBotones, BorderLayout.SOUTH);
        return panelSur;
    }

    public static String formatearPrecio(double precio) {
        return String.format("$ %.2f", precio);
    }

    public static String describir(Funcion f) {
        return f.getTituloPelicula() + " — " + f.getFecha() + " " + f.getHorario() + " — " + f.getLugar();
    }

    public static String describir(Edicion e) {
        return e.getAnio() + " - " + e.getCiudad();
    }

    /* CORRECCION S6: los errores de negocio muestran su mensaje; cualquier otro error (un bug) se muestra
    como "inesperado" y se imprime en la consola. Antes un catch (Exception) mostraba "Error: null".*/
    public static String errorInesperado(RuntimeException ex) {
        ex.printStackTrace();
        return "Error inesperado: " + ex;
    }

    public static void mostrarError(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void mostrarAviso(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    /* CORRECCION S10/I10: los combos guardan los objetos (no textos ni posiciones), así lo elegido
    se usa directamente y no se vuelve a buscar por nombre.*/

    public static <T extends Persona> JComboBox<T> crearComboPersonas(List<T> personas) {
        JComboBox<T> combo = new JComboBox<>();
        for (T p : personas) combo.addItem(p);
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) setText(((Persona) value).getNombreYEmail());
                return this;
            }
        });
        return combo;
    }

    public static JComboBox<Pelicula> crearComboPeliculas(List<Pelicula> peliculas) {
        JComboBox<Pelicula> combo = new JComboBox<>();
        for (Pelicula p : peliculas) combo.addItem(p);
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) setText(((Pelicula) value).getTitulo());
                return this;
            }
        });
        return combo;
    }

    public static <T extends Funcion> JComboBox<T> crearComboFunciones(List<T> funciones) {
        JComboBox<T> combo = new JComboBox<>();
        for (T f : funciones) combo.addItem(f);
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) setText(describir((Funcion) value));
                return this;
            }
        });
        return combo;
    }

    public static JComboBox<Edicion> crearComboEdiciones(List<Edicion> ediciones) {
        JComboBox<Edicion> combo = new JComboBox<>();
        for (Edicion e : ediciones) combo.addItem(e);
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) setText(describir((Edicion) value));
                return this;
            }
        });
        return combo;
    }

    public static <T> T seleccionado(JComboBox<T> combo) {
        return combo.getSelectedIndex() < 0 ? null : combo.getItemAt(combo.getSelectedIndex());
    }


    /* CORRECCION S9: antes eran métodos static de PanelEspectadores que usaban otros paneles.
    Si no hay espectadores registrados, ofrece dar de alta uno en el momento.
    Devuelve true si hay al menos un espectador para continuar con la venta.*/
    public static boolean asegurarEspectadores(Window padre, PersonaServicio personaServicio, String titulo) {
        if (!personaServicio.getEspectadores().isEmpty()) return true;
        int opcion = JOptionPane.showConfirmDialog(padre,
                "No hay espectadores registrados. ¿Desea dar de alta uno ahora?",
                titulo, JOptionPane.YES_NO_OPTION);
        return opcion == JOptionPane.YES_OPTION && darDeAltaEspectador(padre, personaServicio) != null;
    }


    public static Espectador darDeAltaEspectador(Window padre, PersonaServicio personaServicio) {
        DialogoPersona dialogo = new DialogoPersona(padre, "Agregar espectador");
        Espectador registrado = null;
        while (registrado == null && dialogo.mostrar()) {
            try {
                registrado = personaServicio.registrarEspectador(dialogo.getNombre(), dialogo.getEmail(), dialogo.getNacionalidad());
            } catch (FestivalException ex) {
                dialogo.mostrarError(ex.getMessage());
            } catch (RuntimeException ex) {
                dialogo.mostrarError(errorInesperado(ex));
            }
        }
        dialogo.dispose();
        return registrado;
    }

    public static JPanel crearSelectorEspectador(Window padre, PersonaServicio personaServicio, JComboBox<Espectador> combo) {
        JButton btnNuevo = new JButton("Nuevo...");
        btnNuevo.setToolTipText("Dar de alta un espectador nuevo");
        btnNuevo.addActionListener(e -> {
            Espectador nuevo = darDeAltaEspectador(SwingUtilities.getWindowAncestor(btnNuevo), personaServicio);
            if (nuevo != null) {
                combo.addItem(nuevo);
                combo.setSelectedItem(nuevo);
            }
        });
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.add(combo, BorderLayout.CENTER);
        panel.add(btnNuevo, BorderLayout.EAST);
        return panel;
    }
}

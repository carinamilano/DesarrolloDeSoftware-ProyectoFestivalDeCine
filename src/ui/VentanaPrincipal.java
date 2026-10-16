package ui;

import persistencia.Persistencia;
import servicios.SistemaFestival;
import ui.entradas.PanelAbonos;
import ui.entradas.PanelEspectadores;
import ui.entradas.PanelVentaEntradas;
import ui.evaluaciones.PanelEvaluaciones;
import ui.evaluaciones.PanelVotoPublico;
import ui.festival.PanelEdiciones;
import ui.festival.PanelSalas;
import ui.festival.PanelSecciones;
import ui.funciones.PanelFunciones;
import ui.peliculas.PanelActores;
import ui.peliculas.PanelDirectores;
import ui.peliculas.PanelPeliculas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.function.Supplier;

public class VentanaPrincipal extends JFrame {
    private static final Color COLOR_FONDO = new Color(245, 245, 245);

    private final SistemaFestival sistema;
    private final Persistencia persistencia;
    private JPanel panelCentral;
    private JLabel barraEstado;

    // Depende de la abstracción Persistencia, no de cómo se guardan los datos (DIP)
    public VentanaPrincipal(SistemaFestival sistema, Persistencia persistencia) {
        this.sistema = sistema;
        this.persistencia = persistencia;

        configurarVentana();
        construirBarraMenu();
        construirPanelCentral();
        construirBarraEstado();
    }

    private void configurarVentana() {
        setTitle("Festival Internacional de Cine");
        setSize(950, 650);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmarSalida();
            }
        });
    }

    private void construirBarraMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuArchivo = new JMenu("Archivo");
        menuArchivo.add(crearItem("Guardar datos", this::guardarEnSegundoPlano));
        menuArchivo.addSeparator();
        menuArchivo.add(crearItem("Salir", this::confirmarSalida));

        JMenu menuFestival = new JMenu("Festival");
        agregarOpcion(menuFestival, "Ediciones", "Gestión de ediciones", () -> new PanelEdiciones(this));
        agregarOpcion(menuFestival, "Secciones", "Gestión de secciones", () -> new PanelSecciones(this));
        agregarOpcion(menuFestival, "Salas", "Gestión de salas", () -> new PanelSalas(this));

        JMenu menuPeliculas = new JMenu("Películas");
        agregarOpcion(menuPeliculas, "Gestionar películas", "Gestión de películas", () -> new PanelPeliculas(this));
        agregarOpcion(menuPeliculas, "Directores", "Gestión de directores", () -> new PanelDirectores(this));
        agregarOpcion(menuPeliculas, "Actores", "Gestión de actores", () -> new PanelActores(this));

        JMenu menuFunciones = new JMenu("Funciones");
        agregarOpcion(menuFunciones, "Gestionar funciones", "Gestión de funciones", () -> new PanelFunciones(this));

        JMenu menuEntradas = new JMenu("Entradas");
        agregarOpcion(menuEntradas, "Venta de entradas", "Venta de entradas", () -> new PanelVentaEntradas(this));
        agregarOpcion(menuEntradas, "Abonos", "Abonos del festival", () -> new PanelAbonos(this));
        agregarOpcion(menuEntradas, "Espectadores", "Gestión de espectadores", () -> new PanelEspectadores(this));

        JMenu menuEvaluaciones = new JMenu("Evaluaciones");
        agregarOpcion(menuEvaluaciones, "Evaluaciones y ganadores", "Evaluaciones y ganadores", () -> new PanelEvaluaciones(this));
        agregarOpcion(menuEvaluaciones, "Voto del público", "Voto del público", () -> new PanelVotoPublico(this));

        JMenu menuAyuda = new JMenu("Ayuda");
        menuAyuda.add(crearItem("Acerca de...", this::mostrarAcercaDe));

        menuBar.add(menuArchivo);
        menuBar.add(menuFestival);
        menuBar.add(menuPeliculas);
        menuBar.add(menuFunciones);
        menuBar.add(menuEntradas);
        menuBar.add(menuEvaluaciones);
        menuBar.add(menuAyuda);
        setJMenuBar(menuBar);
    }

    private JMenuItem crearItem(String texto, Runnable accion) {
        JMenuItem item = new JMenuItem(texto);
        item.addActionListener(e -> accion.run());
        return item;
    }

    // Cada opción del menú muestra un panel y actualiza la barra de estado (antes eran 12 bloques iguales)
    private void agregarOpcion(JMenu menu, String texto, String estado, Supplier<JPanel> crearPanel) {
        menu.add(crearItem(texto, () -> {
            mostrarPanel(crearPanel.get());
            setEstado(estado);
        }));
    }

    private void construirPanelCentral() {
        panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setBackground(COLOR_FONDO);

        JPanel panelBienvenida = new JPanel();
        panelBienvenida.setLayout(new BoxLayout(panelBienvenida, BoxLayout.Y_AXIS));
        panelBienvenida.setBackground(COLOR_FONDO);

        JLabel labelTitulo = crearEtiquetaCentrada("Festival Internacional de Cine", new Font("SansSerif", Font.BOLD, 26), new Color(30, 30, 30));
        JLabel labelSubtitulo = crearEtiquetaCentrada("Sistema de Gestión", new Font("SansSerif", Font.PLAIN, 16), new Color(100, 100, 100));

        JSeparator separador = new JSeparator();
        separador.setMaximumSize(new Dimension(400, 2));
        separador.setForeground(new Color(180, 180, 180));
        separador.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelBienvenida.add(crearImagen());
        panelBienvenida.add(Box.createRigidArea(new Dimension(0, 15)));
        panelBienvenida.add(labelTitulo);
        panelBienvenida.add(Box.createRigidArea(new Dimension(0, 10)));
        panelBienvenida.add(labelSubtitulo);
        panelBienvenida.add(Box.createRigidArea(new Dimension(0, 15)));
        panelBienvenida.add(separador);

        panelCentral.add(panelBienvenida);
        add(panelCentral, BorderLayout.CENTER);
    }

    private JLabel crearImagen() {
        JLabel labelImagen = new JLabel();
        URL urlImagen = getClass().getClassLoader().getResource("cine.jpg");
        if (urlImagen != null) {
            Image imagen = new ImageIcon(urlImagen).getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
            labelImagen.setIcon(new ImageIcon(imagen));
        }
        labelImagen.setAlignmentX(Component.CENTER_ALIGNMENT);
        return labelImagen;
    }

    private JLabel crearEtiquetaCentrada(String texto, Font fuente, Color color) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(fuente);
        etiqueta.setForeground(color);
        etiqueta.setAlignmentX(Component.CENTER_ALIGNMENT);
        return etiqueta;
    }

    private void construirBarraEstado() {
        barraEstado = new JLabel(" Sistema listo.");
        barraEstado.setBorder(BorderFactory.createEtchedBorder());
        barraEstado.setFont(new Font("SansSerif", Font.PLAIN, 12));
        add(barraEstado, BorderLayout.SOUTH);
    }

    public void setEstado(String mensaje) {
        barraEstado.setText(" " + mensaje);
    }

    public void mostrarPanel(JPanel panel) {
        panelCentral.removeAll();
        panelCentral.setLayout(new BorderLayout());
        panelCentral.add(panel, BorderLayout.CENTER);
        panelCentral.revalidate();
        panelCentral.repaint();
    }

    public SistemaFestival getSistema() {
        return sistema;
    }

    // Guarda en un hilo secundario para que la interfaz no se congele
    public void guardarEnSegundoPlano() {
        setEstado("Guardando datos...");
        Thread hiloGuardado = new Thread(() -> {
            try {
                persistencia.guardar();
                SwingUtilities.invokeLater(() -> setEstado("Datos guardados correctamente."));
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> setEstado("Error al guardar: " + ex.getMessage()));
            }
        });
        hiloGuardado.start();
    }

    private void mostrarAcercaDe() {
        JOptionPane.showMessageDialog(this,
                "Sistema de Gestión de Festival de Cine\nVersión 2.0\nDesarrollado con Java Swing\n\n© 2026 - Milano, Carina Victoria",
                "Acerca de", JOptionPane.INFORMATION_MESSAGE);
    }

    private void confirmarSalida() {
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Desea guardar los datos antes de salir?", "Confirmar salida",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opcion == JOptionPane.YES_OPTION) {
            guardarEnSegundoPlano();
            dispose();
        } else if (opcion == JOptionPane.NO_OPTION) {
            dispose();
        }
        // cancel no hace nada, vuelve a la app
    }
}

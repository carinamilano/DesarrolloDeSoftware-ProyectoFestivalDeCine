package ui;

import excepciones.PersistenciaException;
import persistencia.Persistencia;
import servicios.SistemaFestival;
import ui.comun.ComponentesUI;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class VentanaPrincipal extends JFrame {
    private static final Color COLOR_FONDO = new Color(245, 245, 245);

    private final SistemaFestival sistema;
    private final Persistencia persistencia;
    // Un solo hilo de guardado: si se guarda dos veces seguidas, los guardados se escriben en orden.
    // Es daemon para que no impida que el programa termine
    private final ExecutorService hiloGuardado = Executors.newSingleThreadExecutor(tarea -> {
        Thread hilo = new Thread(tarea, "guardado-de-datos");
        hilo.setDaemon(true);
        return hilo;
    });
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

    // Esta clase es la única que conoce a SistemaFestival: a cada panel le pasa solo los servicios que usa
    // (antes los paneles hacían ventana.getSistema().getXServicio())
    private void construirBarraMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuArchivo = new JMenu("Archivo");
        menuArchivo.add(crearItem("Guardar datos", this::guardarEnSegundoPlano));
        menuArchivo.addSeparator();
        menuArchivo.add(crearItem("Salir", this::confirmarSalida));

        JMenu menuFestival = new JMenu("Festival");
        agregarOpcion(menuFestival, "Ediciones", "Gestión de ediciones",
                () -> new PanelEdiciones(this, sistema.getFestivalServicio()));
        agregarOpcion(menuFestival, "Secciones", "Gestión de secciones",
                () -> new PanelSecciones(this, sistema.getFestivalServicio()));
        agregarOpcion(menuFestival, "Salas", "Gestión de salas",
                () -> new PanelSalas(this, sistema.getFestivalServicio()));

        JMenu menuPeliculas = new JMenu("Películas");
        agregarOpcion(menuPeliculas, "Gestionar películas", "Gestión de películas",
                () -> new PanelPeliculas(this, sistema.getPeliculaServicio(), sistema.getPersonaServicio()));
        agregarOpcion(menuPeliculas, "Directores", "Gestión de directores",
                () -> new PanelDirectores(this, sistema.getPersonaServicio()));
        agregarOpcion(menuPeliculas, "Actores", "Gestión de actores",
                () -> new PanelActores(this, sistema.getPersonaServicio(), sistema.getPeliculaServicio()));

        JMenu menuFunciones = new JMenu("Funciones");
        agregarOpcion(menuFunciones, "Gestionar funciones", "Gestión de funciones",
                () -> new PanelFunciones(this, sistema.getFuncionServicio(), sistema.getFestivalServicio()));

        JMenu menuEntradas = new JMenu("Entradas");
        agregarOpcion(menuEntradas, "Venta de entradas", "Venta de entradas",
                () -> new PanelVentaEntradas(this, sistema.getVentaServicio(), sistema.getFuncionServicio(),
                        sistema.getPersonaServicio()));
        agregarOpcion(menuEntradas, "Abonos", "Abonos del festival",
                () -> new PanelAbonos(this, sistema.getAbonoServicio(), sistema.getFuncionServicio(),
                        sistema.getPersonaServicio()));
        agregarOpcion(menuEntradas, "Espectadores", "Gestión de espectadores",
                () -> new PanelEspectadores(this, sistema.getPersonaServicio()));

        JMenu menuEvaluaciones = new JMenu("Evaluaciones");
        agregarOpcion(menuEvaluaciones, "Evaluaciones y ganadores", "Evaluaciones y ganadores",
                () -> new PanelEvaluaciones(this, sistema.getEvaluacionServicio(), sistema.getPeliculaServicio(),
                        sistema.getPersonaServicio(), sistema.getFestivalServicio()));
        agregarOpcion(menuEvaluaciones, "Voto del público", "Voto del público",
                () -> new PanelVotoPublico(this, sistema.getEvaluacionServicio(), sistema.getPeliculaServicio(),
                        sistema.getPersonaServicio(), sistema.getFestivalServicio()));

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

    // Cada opción del menú muestra un panel y actualiza la barra de estado
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

    // ========== GUARDADO ==========

    public void guardarEnSegundoPlano() {
        guardar(() -> setEstado("Datos guardados correctamente."),
                ex -> setEstado("Error al guardar: " + ex.getMessage()));
    }

    // La copia de los datos se toma acá, en el hilo de la UI (el único que modifica el modelo);
    // el hilo de guardado solo escribe esa copia. Antes el hilo leía las listas del modelo
    // mientras la UI las podía estar modificando
    private void guardar(Runnable alTerminar, Consumer<PersistenciaException> alFallar) {
        setEstado("Guardando datos...");
        Persistencia.Guardado guardado = persistencia.prepararGuardado();
        hiloGuardado.execute(() -> {
            try {
                guardado.escribir();
                SwingUtilities.invokeLater(alTerminar);
            } catch (PersistenciaException ex) {
                SwingUtilities.invokeLater(() -> alFallar.accept(ex));
            }
        });
    }

    private void mostrarAcercaDe() {
        JOptionPane.showMessageDialog(this,
                "Sistema de Gestión de Festival de Cine\nVersión 2.0\nDesarrollado con Java Swing\n\n© 2026 - Milano, Carina Victoria",
                "Acerca de", JOptionPane.INFORMATION_MESSAGE);
    }

    // Si se elige guardar, la ventana espera a que termine el guardado y, si falló, lo avisa
    // y pregunta si salir igual (antes se cerraba sin esperar y el error no se veía)
    private void confirmarSalida() {
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Desea guardar los datos antes de salir?", "Confirmar salida",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opcion == JOptionPane.YES_OPTION) {
            setEnabled(false);
            guardar(this::salir, this::avisarErrorAlSalir);
        } else if (opcion == JOptionPane.NO_OPTION) {
            salir();
        }
        // cancel no hace nada, vuelve a la app
    }

    private void avisarErrorAlSalir(PersistenciaException ex) {
        setEnabled(true);
        setEstado("Error al guardar: " + ex.getMessage());
        boolean salirIgual = ComponentesUI.confirmar(this,
                "No se pudieron guardar los datos:\n" + String.join("\n", ex.getErrores())
                        + "\n\n¿Desea salir de todos modos? Se perderán los cambios no guardados.",
                "Error al guardar");
        if (salirIgual) salir();
    }

    private void salir() {
        hiloGuardado.shutdown();
        dispose();
    }
}

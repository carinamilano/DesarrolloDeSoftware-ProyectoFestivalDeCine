package ui;

import persistencia.Persistencia;
import servicios.EntradaServicio;
import servicios.FestivalServicio;
import servicios.PeliculaServicio;
import javax.swing.*;
import java.awt.*;
import ui.festival.PanelEdiciones;
import ui.entradas.PanelVentaEntradas;

public class VentanaPrincipal extends JFrame {

    private FestivalServicio festivalServicio;
    private PeliculaServicio peliculaServicio;
    private EntradaServicio entradaServicio;
    private Persistencia persistencia;
    private JPanel panelCentral;
    private JLabel barraEstado;

    public VentanaPrincipal(FestivalServicio festivalServicio,
                            PeliculaServicio peliculaServicio,
                            EntradaServicio entradaServicio,
                            Persistencia persistencia) {
        this.festivalServicio = festivalServicio;
        this.peliculaServicio = peliculaServicio;
        this.entradaServicio = entradaServicio;
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

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                confirmarSalida();
            }
        });
    }

    private void construirBarraMenu() {
        JMenuBar menuBar = new JMenuBar();

        // archivo
        JMenu menuArchivo = new JMenu("Archivo");

        JMenuItem itemGuardar = new JMenuItem("Guardar datos");
        itemGuardar.addActionListener(e -> guardarEnSegundoPlano());

        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.addActionListener(e -> confirmarSalida());

        menuArchivo.add(itemGuardar);
        menuArchivo.addSeparator();
        menuArchivo.add(itemSalir);

        // festival
        JMenu menuFestival = new JMenu("Festival");

        JMenuItem itemEdiciones = new JMenuItem("Ediciones");
        itemEdiciones.addActionListener(e -> {
            mostrarPanel(new PanelEdiciones(this));
            setEstado("Gestión de ediciones");
        });

        JMenuItem itemSecciones = new JMenuItem("Secciones");
        itemSecciones.addActionListener(e -> {
            mostrarPanel(new ui.festival.PanelSecciones(this));
            setEstado("Gestión de secciones");
        });

        JMenuItem itemSalas = new JMenuItem("Salas");
        itemSalas.addActionListener(e -> {
            mostrarPanel(new ui.festival.PanelSalas(this));
            setEstado("Gestión de salas");
        });

        menuFestival.add(itemEdiciones);
        menuFestival.add(itemSecciones);
        menuFestival.add(itemSalas);

        // peliculas
        JMenu menuPeliculas = new JMenu("Películas");

        JMenuItem itemPeliculas = new JMenuItem("Gestionar películas");
        itemPeliculas.addActionListener(e -> {
            mostrarPanel(new ui.peliculas.PanelPeliculas(this));
            setEstado("Gestión de películas");
        });

        JMenuItem itemDirectores = new JMenuItem("Directores");
        itemDirectores.addActionListener(e -> {
            mostrarPanel(new ui.peliculas.PanelDirectores(this));
            setEstado("Gestión de directores");
        });

        JMenuItem itemActores = new JMenuItem("Actores");
        itemActores.addActionListener(e -> {
            mostrarPanel(new ui.peliculas.PanelActores(this));
            setEstado("Gestión de actores");
        });

        menuPeliculas.add(itemPeliculas);
        menuPeliculas.add(itemDirectores);
        menuPeliculas.add(itemActores);

        // funciones
        JMenu menuFunciones = new JMenu("Funciones");

        JMenuItem itemFunciones = new JMenuItem("Gestionar funciones");
        itemFunciones.addActionListener(e -> {
            mostrarPanel(new ui.funciones.PanelFunciones(this));
            setEstado("Gestión de funciones");
        });

        menuFunciones.add(itemFunciones);

        // entradas
        JMenu menuEntradas = new JMenu("Entradas");

        JMenuItem itemVentaEntradas = new JMenuItem("Venta de entradas");
        itemVentaEntradas.addActionListener(e -> {
            mostrarPanel(new PanelVentaEntradas(this));
            setEstado("Venta de entradas");
        });

        JMenuItem itemEspectadores = new JMenuItem("Espectadores");
        itemEspectadores.addActionListener(e -> {
            mostrarPanel(new ui.entradas.PanelEspectadores(this));
            setEstado("Gestión de espectadores");
        });

        JMenuItem itemAbonos = new JMenuItem("Abonos");
        itemAbonos.addActionListener(e -> {
            mostrarPanel(new ui.entradas.PanelAbonos(this));
            setEstado("Abonos del festival");
        });

        menuEntradas.add(itemVentaEntradas);
        menuEntradas.add(itemAbonos);
        menuEntradas.add(itemEspectadores);

        // evaluaciones
        JMenu menuEvaluaciones = new JMenu("Evaluaciones");

        JMenuItem itemEvaluaciones = new JMenuItem("Evaluaciones y ganadores");
        itemEvaluaciones.addActionListener(e -> {
            mostrarPanel(new ui.evaluaciones.PanelEvaluaciones(this));
            setEstado("Evaluaciones y ganadores");
        });

        JMenuItem itemVotoPublico = new JMenuItem("Voto del público");
        itemVotoPublico.addActionListener(e -> {
            mostrarPanel(new ui.evaluaciones.PanelVotoPublico(this));
            setEstado("Voto del público");
        });

        menuEvaluaciones.add(itemEvaluaciones);
        menuEvaluaciones.add(itemVotoPublico);

        // ayuda
        JMenu menuAyuda = new JMenu("Ayuda");

        JMenuItem itemAcercaDe = new JMenuItem("Acerca de...");
        itemAcercaDe.addActionListener(e -> mostrarAcercaDe());

        menuAyuda.add(itemAcercaDe);

        menuBar.add(menuArchivo);
        menuBar.add(menuFestival);
        menuBar.add(menuPeliculas);
        menuBar.add(menuFunciones);
        menuBar.add(menuEntradas);
        menuBar.add(menuEvaluaciones);
        menuBar.add(menuAyuda);

        setJMenuBar(menuBar);
    }

    private void construirPanelCentral() {
        panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setBackground(new Color(245, 245, 245));

        JPanel panelBienvenida = new JPanel();
        panelBienvenida.setLayout(new BoxLayout(panelBienvenida, BoxLayout.Y_AXIS));
        panelBienvenida.setBackground(new Color(245, 245, 245));

        // Imagen
        JLabel labelImagen = new JLabel();
        try {
            java.net.URL imgUrl = getClass().getClassLoader().getResource("cine.jpg");
            if (imgUrl != null) {
                ImageIcon iconOriginal = new ImageIcon(imgUrl);
                Image imgEscalada = iconOriginal.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
                labelImagen.setIcon(new ImageIcon(imgEscalada));
            }
        } catch (Exception e) {
            System.out.println("No se pudo cargar la imagen: " + e.getMessage());
        }
        labelImagen.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel labelTitulo = new JLabel("Festival Internacional de Cine");
        labelTitulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        labelTitulo.setForeground(new Color(30, 30, 30));
        labelTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel labelSubtitulo = new JLabel("Sistema de Gestión");
        labelSubtitulo.setFont(new Font("SansSerif", Font.PLAIN, 16));
        labelSubtitulo.setForeground(new Color(100, 100, 100));
        labelSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator separador = new JSeparator();
        separador.setMaximumSize(new Dimension(400, 2));
        separador.setForeground(new Color(180, 180, 180));
        separador.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelBienvenida.add(labelImagen);
        panelBienvenida.add(Box.createRigidArea(new Dimension(0, 15)));
        panelBienvenida.add(labelTitulo);
        panelBienvenida.add(Box.createRigidArea(new Dimension(0, 10)));
        panelBienvenida.add(labelSubtitulo);
        panelBienvenida.add(Box.createRigidArea(new Dimension(0, 15)));
        panelBienvenida.add(separador);

        panelCentral.add(panelBienvenida);
        add(panelCentral, BorderLayout.CENTER);
    }

    private void construirBarraEstado() {
        barraEstado = new JLabel(" Sistema listo.");
        barraEstado.setBorder(BorderFactory.createEtchedBorder());
        barraEstado.setFont(new Font("SansSerif", Font.PLAIN, 12));
        add(barraEstado, BorderLayout.SOUTH);
    }

    // metodos paneles

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

    public FestivalServicio getFestivalServicio() { return festivalServicio; }
    public PeliculaServicio getPeliculaServicio() { return peliculaServicio; }
    public EntradaServicio getEntradaServicio() { return entradaServicio; }

    // thread para guardaren segundo plano

    public void guardarEnSegundoPlano() {
        setEstado("Guardando datos...");

        Thread hiloGuardado = new Thread(() -> {
            try {
                persistencia.guardarSalas(festivalServicio.getSalas());
                persistencia.guardarDirectores(peliculaServicio.getDirectores());
                persistencia.guardarActores(peliculaServicio.getActores());
                persistencia.guardarJurados(peliculaServicio.getJurados());
                persistencia.guardarEspectadores(entradaServicio.getEspectadores());
                persistencia.guardarPeliculas(peliculaServicio.getPeliculas());
                persistencia.guardarEvaluaciones(peliculaServicio.getEvaluaciones());

                SwingUtilities.invokeLater(() ->
                        setEstado("Datos guardados correctamente.")
                );
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() ->
                        setEstado("Error al guardar: " + ex.getMessage())
                );
            }
        });

        hiloGuardado.start();
    }

    // dialogos

    private void mostrarAcercaDe() {
        JOptionPane.showMessageDialog(
                this,
                "Sistema de Gestión de Festival de Cine\nVersión 2.0\nDesarrollado con Java Swing\n\n© 2026 - Milano, Carina Victoria",
                "Acerca de",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void confirmarSalida() {
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Desea guardar los datos antes de salir?",
                "Confirmar salida",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (opcion == JOptionPane.YES_OPTION) {
            guardarEnSegundoPlano();
            dispose();
        } else if (opcion == JOptionPane.NO_OPTION) {
            dispose();
        }
        // cancel no hace nada, vuelve a la app
    }
}
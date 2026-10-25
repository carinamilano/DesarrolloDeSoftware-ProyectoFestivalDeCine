package ui;

import excepciones.PersistenciaException;
import persistencia.Persistencia;
import servicios.*;
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
import java.util.List;
import java.util.Map;

public class VentanaPrincipal extends JFrame {

    private PersonaServicio personaServicio;
    private PeliculaServicio peliculaServicio;
    private FestivalServicio festivalServicio;
    private FuncionServicio funcionServicio;
    private VentaServicio ventaServicio;
    private AbonoServicio abonoServicio;
    private EvaluacionServicio evaluacionServicio;
    private Persistencia persistencia;
    private JPanel panelCentral;
    private JLabel barraEstado;
    private JMenuItem itemGuardar;
    private Thread hiloGuardado;
    private boolean erroresDeCargaConfirmados;

    public VentanaPrincipal(PersonaServicio personaServicio, PeliculaServicio peliculaServicio, FestivalServicio festivalServicio, FuncionServicio funcionServicio, VentaServicio ventaServicio, AbonoServicio abonoServicio, EvaluacionServicio evaluacionServicio, Persistencia persistencia) {
        this.personaServicio = personaServicio;
        this.peliculaServicio = peliculaServicio;
        this.festivalServicio = festivalServicio;
        this.funcionServicio = funcionServicio;
        this.ventaServicio = ventaServicio;
        this.abonoServicio = abonoServicio;
        this.evaluacionServicio = evaluacionServicio;
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

    /* CORRECCION S9: cada panel recibe solo los servicios que usa, antes recibía la ventana entera
    y le pedía los servicios con getters.*/
    private void construirBarraMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuArchivo = new JMenu("Archivo");

        itemGuardar = new JMenuItem("Guardar datos");
        itemGuardar.addActionListener(e -> guardarEnSegundoPlano());

        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.addActionListener(e -> confirmarSalida());

        menuArchivo.add(itemGuardar);
        menuArchivo.addSeparator();
        menuArchivo.add(itemSalir);

        JMenu menuFestival = new JMenu("Festival");

        JMenuItem itemEdiciones = new JMenuItem("Ediciones");
        itemEdiciones.addActionListener(e -> {
            mostrarPanel(new PanelEdiciones(this, festivalServicio));
            setEstado("Gestión de ediciones");
        });

        JMenuItem itemSecciones = new JMenuItem("Secciones");
        itemSecciones.addActionListener(e -> {
            mostrarPanel(new PanelSecciones(this, festivalServicio));
            setEstado("Gestión de secciones");
        });

        JMenuItem itemSalas = new JMenuItem("Salas");
        itemSalas.addActionListener(e -> {
            mostrarPanel(new PanelSalas(this, festivalServicio));
            setEstado("Gestión de salas");
        });

        menuFestival.add(itemEdiciones);
        menuFestival.add(itemSecciones);
        menuFestival.add(itemSalas);

        JMenu menuPeliculas = new JMenu("Películas");

        JMenuItem itemPeliculas = new JMenuItem("Gestionar películas");
        itemPeliculas.addActionListener(e -> {
            mostrarPanel(new PanelPeliculas(this, peliculaServicio, personaServicio));
            setEstado("Gestión de películas");
        });

        JMenuItem itemDirectores = new JMenuItem("Directores");
        itemDirectores.addActionListener(e -> {
            mostrarPanel(new PanelDirectores(this, personaServicio));
            setEstado("Gestión de directores");
        });

        JMenuItem itemActores = new JMenuItem("Actores");
        itemActores.addActionListener(e -> {
            mostrarPanel(new PanelActores(this, personaServicio, peliculaServicio));
            setEstado("Gestión de actores");
        });

        menuPeliculas.add(itemPeliculas);
        menuPeliculas.add(itemDirectores);
        menuPeliculas.add(itemActores);

        JMenu menuFunciones = new JMenu("Funciones");

        JMenuItem itemFunciones = new JMenuItem("Gestionar funciones");
        itemFunciones.addActionListener(e -> {
            mostrarPanel(new PanelFunciones(this, funcionServicio, festivalServicio, peliculaServicio));
            setEstado("Gestión de funciones");
        });

        menuFunciones.add(itemFunciones);

        JMenu menuEntradas = new JMenu("Entradas");

        JMenuItem itemVentaEntradas = new JMenuItem("Venta de entradas");
        itemVentaEntradas.addActionListener(e -> {
            mostrarPanel(new PanelVentaEntradas(this, ventaServicio, funcionServicio, personaServicio));
            setEstado("Venta de entradas");
        });

        JMenuItem itemEspectadores = new JMenuItem("Espectadores");
        itemEspectadores.addActionListener(e -> {
            mostrarPanel(new PanelEspectadores(this, personaServicio));
            setEstado("Gestión de espectadores");
        });

        JMenuItem itemAbonos = new JMenuItem("Abonos");
        itemAbonos.addActionListener(e -> {
            mostrarPanel(new PanelAbonos(this, abonoServicio, funcionServicio, personaServicio));
            setEstado("Abonos del festival");
        });

        menuEntradas.add(itemVentaEntradas);
        menuEntradas.add(itemAbonos);
        menuEntradas.add(itemEspectadores);

        JMenu menuEvaluaciones = new JMenu("Evaluaciones");

        JMenuItem itemEvaluaciones = new JMenuItem("Evaluaciones y ganadores");
        itemEvaluaciones.addActionListener(e -> {
            mostrarPanel(new PanelEvaluaciones(this, evaluacionServicio, peliculaServicio, personaServicio, festivalServicio));
            setEstado("Evaluaciones y ganadores");
        });

        JMenuItem itemVotoPublico = new JMenuItem("Voto del público");
        itemVotoPublico.addActionListener(e -> {
            mostrarPanel(new PanelVotoPublico(this, evaluacionServicio, peliculaServicio, personaServicio, festivalServicio));
            setEstado("Voto del público");
        });

        menuEvaluaciones.add(itemEvaluaciones);
        menuEvaluaciones.add(itemVotoPublico);

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

        JLabel labelImagen = new JLabel();
        java.net.URL imgUrl = getClass().getClassLoader().getResource("cine.jpg");
        if (imgUrl != null) {
            ImageIcon iconOriginal = new ImageIcon(imgUrl);
            Image imgEscalada = iconOriginal.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
            labelImagen.setIcon(new ImageIcon(imgEscalada));
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


    /* CORRECCION P9: el texto de los archivos se arma acá, en el hilo de la interfaz, y el hilo
    secundario solo lo escribe; así no recorre listas que la interfaz puede estar modificando.
    Mientras guarda, "Guardar datos" queda deshabilitado para no lanzar dos guardados a la vez.*/
    public void guardarEnSegundoPlano() {
        if (guardadoEnCurso() || !confirmarGuardadoConErroresDeCarga()) return;
        Map<String, List<String>> archivos = persistencia.prepararGuardado();
        itemGuardar.setEnabled(false);
        setEstado("Guardando datos...");

        hiloGuardado = new Thread(() -> {
            String error = escribir(archivos);
            SwingUtilities.invokeLater(() -> {
                itemGuardar.setEnabled(true);
                setEstado(error == null ? "Datos guardados correctamente." : "Error al guardar: " + error);
            });
        });
        hiloGuardado.start();
    }

    private boolean guardadoEnCurso() {
        return hiloGuardado != null && hiloGuardado.isAlive();
    }

    private String escribir(Map<String, List<String>> archivos) {
        try {
            persistencia.escribir(archivos);
            return null;
        } catch (PersistenciaException ex) {
            return ex.getMessage() + " " + ex.getErrores();
        }
    }

    // CORRECCION P3: si al abrir hubo líneas que no se pudieron cargar, se avisa que al guardar se pierden.
    private boolean confirmarGuardadoConErroresDeCarga() {
        if (!persistencia.huboErroresDeCarga() || erroresDeCargaConfirmados) return true;
        int opcion = JOptionPane.showConfirmDialog(this,
                "Al abrir el sistema hubo líneas de los archivos que no se pudieron cargar.\n"
                        + "Si guarda ahora, esas líneas se van a perder. ¿Desea guardar igual?",
                "Guardar datos", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        erroresDeCargaConfirmados = opcion == JOptionPane.YES_OPTION;
        return erroresDeCargaConfirmados;
    }


    private void mostrarAcercaDe() {
        JOptionPane.showMessageDialog(
                this,
                "Sistema de Gestión de Festival de Cine\nVersión 2.0\nDesarrollado con Java Swing\n\n© 2026 - Milano, Carina Victoria",
                "Acerca de",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /* CORRECCION P4: al salir se espera a que termine el guardado y, si falla, se muestra el error
    y se puede elegir no salir. Antes la ventana se cerraba enseguida y el error no se veía.*/
    private void confirmarSalida() {
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Desea guardar los datos antes de salir?",
                "Confirmar salida",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (opcion == JOptionPane.YES_OPTION) {
            esperarGuardadoEnCurso();
            if (!confirmarGuardadoConErroresDeCarga()) return;
            String error = escribir(persistencia.prepararGuardado());
            if (error != null) {
                int salir = JOptionPane.showConfirmDialog(this,
                        "No se pudieron guardar los datos:\n" + error + "\n\n¿Desea salir igual?",
                        "Error al guardar", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                if (salir != JOptionPane.YES_OPTION) return;
            }
            dispose();
        } else if (opcion == JOptionPane.NO_OPTION) {
            esperarGuardadoEnCurso();
            dispose();
        }
    }

    private void esperarGuardadoEnCurso() {
        if (!guardadoEnCurso()) return;
        try {
            hiloGuardado.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

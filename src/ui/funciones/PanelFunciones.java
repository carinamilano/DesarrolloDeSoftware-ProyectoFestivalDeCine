package ui.funciones;

import modelo.*;
import ui.VentanaPrincipal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class PanelFunciones extends JPanel {

    private VentanaPrincipal ventana;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JProgressBar progressBar;

    public PanelFunciones(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        construirPanelSuperior();
        construirTabla();
        construirPanelInferior();

        cargarDatos();
    }

    private void construirPanelSuperior() {
        JLabel titulo = new JLabel("Gestión de Funciones");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(titulo, BorderLayout.NORTH);
    }

    private void construirTabla() {
        String[] columnas = {"Película", "Tipo", "Fecha", "Horario", "Sala / Enlace", "Ocupación %"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarProgressBar();
            }
        });

        JScrollPane scrollPane = new JScrollPane(tabla);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void construirPanelInferior() {
        JPanel panelInferior = new JPanel(new BorderLayout(10, 5));
        panelInferior.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        JPanel panelOcupacion = new JPanel(new BorderLayout(10, 0));
        panelOcupacion.setBorder(BorderFactory.createTitledBorder("Ocupación de la función seleccionada"));

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("Seleccione una función");
        progressBar.setPreferredSize(new Dimension(0, 30));

        panelOcupacion.add(progressBar, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JButton btnAgregarPresencial = new JButton("Agregar función presencial");
        btnAgregarPresencial.addActionListener(e -> agregarFuncionPresencial());

        JButton btnAgregarStreaming = new JButton("Agregar función streaming");
        btnAgregarStreaming.addActionListener(e -> agregarFuncionStreaming());

        panelBotones.add(btnAgregarPresencial);
        panelBotones.add(btnAgregarStreaming);

        panelInferior.add(panelOcupacion, BorderLayout.CENTER);
        panelInferior.add(panelBotones, BorderLayout.SOUTH);

        add(panelInferior, BorderLayout.SOUTH);
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        List<Funcion> funciones = ventana.getEntradaServicio().getFunciones();

        for (Funcion f : funciones) {
            String tipo;
            String salaOEnlace;

            if (f instanceof FuncionPresencial) {
                tipo = "Presencial";
                salaOEnlace = ((FuncionPresencial) f).getSala().getNombre();
            } else {
                tipo = "Streaming";
                salaOEnlace = ((FuncionStreaming) f).getEnlace();
            }

            modeloTabla.addRow(new Object[]{
                    f.getPelicula().getTitulo(),
                    tipo,
                    f.getFecha().toString(),
                    f.getHorario().toString(),
                    salaOEnlace,
                    String.format("%.1f%%", f.calcularOcupacion())
            });
        }

        progressBar.setValue(0);
        progressBar.setString("Seleccione una función");
    }

    private void actualizarProgressBar() {
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada == -1) {
            progressBar.setValue(0);
            progressBar.setString("Seleccione una función");
            return;
        }

        List<Funcion> funciones = ventana.getEntradaServicio().getFunciones();
        Funcion funcion = funciones.get(filaSeleccionada);
        int ocupacion = (int) funcion.calcularOcupacion();

        progressBar.setValue(ocupacion);
        progressBar.setString(ocupacion + "% ocupado — " + funcion.getPelicula().getTitulo());

        if (ocupacion >= 90) {
            progressBar.setForeground(new Color(200, 50, 50));
        } else if (ocupacion >= 60) {
            progressBar.setForeground(new Color(220, 150, 0));
        } else {
            progressBar.setForeground(new Color(50, 160, 50));
        }
    }

    private void agregarFuncionPresencial() {
        List<Pelicula> peliculas = ventana.getPeliculaServicio().getPeliculas();
        List<Sala> salas = ventana.getFestivalServicio().getSalas();

        if (peliculas.isEmpty()) {
            JOptionPane.showMessageDialog(ventana,
                    "No hay películas registradas.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (salas.isEmpty()) {
            JOptionPane.showMessageDialog(ventana,
                    "No hay salas registradas.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] nombresPeliculas = peliculas.stream()
                .map(Pelicula::getTitulo).toArray(String[]::new);
        String[] nombresSalas = salas.stream()
                .map(Sala::getNombre).toArray(String[]::new);

        JDialog dialog = new JDialog(ventana, "Agregar función presencial", true);
        dialog.setSize(420, 280);
        dialog.setLocationRelativeTo(ventana);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel panelCampos = new JPanel(new GridLayout(4, 2, 10, 10));
        panelCampos.setBorder(BorderFactory.createEmptyBorder(20, 20, 5, 20));

        JComboBox<String> comboPelicula = new JComboBox<>(nombresPeliculas);
        JComboBox<String> comboSala = new JComboBox<>(nombresSalas);
        JTextField campoFecha = new JTextField();
        JTextField campoHorario = new JTextField();

        panelCampos.add(new JLabel("Película:"));
        panelCampos.add(comboPelicula);
        panelCampos.add(new JLabel("Sala:"));
        panelCampos.add(comboSala);
        panelCampos.add(new JLabel("Fecha (YYYY-MM-DD):"));
        panelCampos.add(campoFecha);
        panelCampos.add(new JLabel("Horario (HH:MM):"));
        panelCampos.add(campoHorario);

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

        campoFecha.addActionListener(e -> {
            String fechaStr = campoFecha.getText().trim();
            if (fechaStr.isEmpty()) {
                labelError.setText("La fecha es obligatoria.");
                return;
            }
            LocalDate fecha;
            try {
                fecha = LocalDate.parse(fechaStr);
            } catch (DateTimeParseException ex) {
                labelError.setText("Formato de fecha incorrecto. Use YYYY-MM-DD.");
                return;
            }
            if (fecha.isBefore(LocalDate.now())) {
                labelError.setText("La fecha no puede ser anterior a hoy (" + LocalDate.now() + ").");
                return;
            }
            labelError.setText(" ");
            campoHorario.requestFocus();
        });

        campoHorario.addActionListener(e -> {
            String horarioStr = campoHorario.getText().trim();
            if (horarioStr.isEmpty()) {
                labelError.setText("El horario es obligatorio.");
                return;
            }
            try {
                LocalTime.parse(horarioStr);
            } catch (DateTimeParseException ex) {
                labelError.setText("Formato de horario incorrecto. Use HH:MM.");
                return;
            }
            labelError.setText(" ");
            btnAceptar.doClick();
        });

        btnCancelar.addActionListener(e -> dialog.dispose());

        btnAceptar.addActionListener(e -> {
            String fechaStr = campoFecha.getText().trim();
            String horarioStr = campoHorario.getText().trim();

            if (fechaStr.isEmpty()) {
                labelError.setText("La fecha es obligatoria.");
                campoFecha.requestFocus();
                return;
            }

            LocalDate fecha;
            try {
                fecha = LocalDate.parse(fechaStr);
            } catch (DateTimeParseException ex) {
                labelError.setText("Formato de fecha incorrecto. Use YYYY-MM-DD.");
                campoFecha.requestFocus();
                return;
            }

            if (fecha.isBefore(LocalDate.now())) {
                labelError.setText("La fecha no puede ser anterior a hoy (" + LocalDate.now() + ").");
                campoFecha.requestFocus();
                return;
            }

            if (horarioStr.isEmpty()) {
                labelError.setText("El horario es obligatorio.");
                campoHorario.requestFocus();
                return;
            }

            LocalTime horario;
            try {
                horario = LocalTime.parse(horarioStr);
            } catch (DateTimeParseException ex) {
                labelError.setText("Formato de horario incorrecto. Use HH:MM.");
                campoHorario.requestFocus();
                return;
            }

            try {
                String tituloPelicula = (String) comboPelicula.getSelectedItem();
                String nombreSala = (String) comboSala.getSelectedItem();
                Pelicula pelicula = ventana.getPeliculaServicio().buscarPelicula(tituloPelicula);
                Sala sala = ventana.getFestivalServicio().buscarSala(nombreSala);
                ventana.getEntradaServicio().programarFuncionPresencial(pelicula, sala, fecha, horario);
                cargarDatos();
                ventana.setEstado("Función presencial agregada: " + tituloPelicula);
                dialog.dispose();
            } catch (Exception ex) {
                labelError.setText("Error: " + ex.getMessage());
            }
        });

        dialog.add(panelCampos, BorderLayout.CENTER);
        dialog.add(panelSur, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void agregarFuncionStreaming() {
        List<Pelicula> peliculas = ventana.getPeliculaServicio().getPeliculas();

        if (peliculas.isEmpty()) {
            JOptionPane.showMessageDialog(ventana,
                    "No hay películas registradas.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] nombresPeliculas = peliculas.stream()
                .map(Pelicula::getTitulo).toArray(String[]::new);

        JDialog dialog = new JDialog(ventana, "Agregar función streaming", true);
        dialog.setSize(420, 300);
        dialog.setLocationRelativeTo(ventana);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel panelCampos = new JPanel(new GridLayout(5, 2, 10, 10));
        panelCampos.setBorder(BorderFactory.createEmptyBorder(20, 20, 5, 20));

        JComboBox<String> comboPelicula = new JComboBox<>(nombresPeliculas);
        JTextField campoEnlace = new JTextField();
        JTextField campoCapacidad = new JTextField();
        JTextField campoFecha = new JTextField();
        JTextField campoHorario = new JTextField();

        panelCampos.add(new JLabel("Película:"));
        panelCampos.add(comboPelicula);
        panelCampos.add(new JLabel("Enlace:"));
        panelCampos.add(campoEnlace);
        panelCampos.add(new JLabel("Capacidad virtual (max 1000):"));
        panelCampos.add(campoCapacidad);
        panelCampos.add(new JLabel("Fecha (YYYY-MM-DD):"));
        panelCampos.add(campoFecha);
        panelCampos.add(new JLabel("Horario (HH:MM):"));
        panelCampos.add(campoHorario);

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

        campoEnlace.addActionListener(e -> {
            if (campoEnlace.getText().trim().isEmpty()) {
                labelError.setText("El enlace es obligatorio.");
                return;
            }
            labelError.setText(" ");
            campoCapacidad.requestFocus();
        });

        campoCapacidad.addActionListener(e -> {
            String capacidadStr = campoCapacidad.getText().trim();
            if (capacidadStr.isEmpty()) {
                labelError.setText("La capacidad es obligatoria.");
                return;
            }
            try {
                int capacidad = Integer.parseInt(capacidadStr);
                if (capacidad <= 0 || capacidad > 1000) {
                    labelError.setText("La capacidad debe estar entre 1 y 1000.");
                    return;
                }
            } catch (NumberFormatException ex) {
                labelError.setText("La capacidad debe ser un número entero.");
                return;
            }
            labelError.setText(" ");
            campoFecha.requestFocus();
        });

        campoFecha.addActionListener(e -> {
            String fechaStr = campoFecha.getText().trim();
            if (fechaStr.isEmpty()) {
                labelError.setText("La fecha es obligatoria.");
                return;
            }
            LocalDate fecha;
            try {
                fecha = LocalDate.parse(fechaStr);
            } catch (DateTimeParseException ex) {
                labelError.setText("Formato de fecha incorrecto. Use YYYY-MM-DD.");
                return;
            }
            if (fecha.isBefore(LocalDate.now())) {
                labelError.setText("La fecha no puede ser anterior a hoy (" + LocalDate.now() + ").");
                return;
            }
            labelError.setText(" ");
            campoHorario.requestFocus();
        });

        campoHorario.addActionListener(e -> {
            String horarioStr = campoHorario.getText().trim();
            if (horarioStr.isEmpty()) {
                labelError.setText("El horario es obligatorio.");
                return;
            }
            try {
                LocalTime.parse(horarioStr);
            } catch (DateTimeParseException ex) {
                labelError.setText("Formato de horario incorrecto. Use HH:MM.");
                return;
            }
            labelError.setText(" ");
            btnAceptar.doClick();
        });

        btnCancelar.addActionListener(e -> dialog.dispose());

        btnAceptar.addActionListener(e -> {
            String enlace = campoEnlace.getText().trim();
            String capacidadStr = campoCapacidad.getText().trim();
            String fechaStr = campoFecha.getText().trim();
            String horarioStr = campoHorario.getText().trim();

            if (enlace.isEmpty()) {
                labelError.setText("El enlace es obligatorio.");
                campoEnlace.requestFocus();
                return;
            }

            if (capacidadStr.isEmpty()) {
                labelError.setText("La capacidad es obligatoria.");
                campoCapacidad.requestFocus();
                return;
            }

            int capacidad;
            try {
                capacidad = Integer.parseInt(capacidadStr);
                if (capacidad <= 0 || capacidad > 1000) {
                    labelError.setText("La capacidad debe estar entre 1 y 1000.");
                    campoCapacidad.requestFocus();
                    return;
                }
            } catch (NumberFormatException ex) {
                labelError.setText("La capacidad debe ser un número entero.");
                campoCapacidad.requestFocus();
                return;
            }

            if (fechaStr.isEmpty()) {
                labelError.setText("La fecha es obligatoria.");
                campoFecha.requestFocus();
                return;
            }

            LocalDate fecha;
            try {
                fecha = LocalDate.parse(fechaStr);
            } catch (DateTimeParseException ex) {
                labelError.setText("Formato de fecha incorrecto. Use YYYY-MM-DD.");
                campoFecha.requestFocus();
                return;
            }

            if (fecha.isBefore(LocalDate.now())) {
                labelError.setText("La fecha no puede ser anterior a hoy (" + LocalDate.now() + ").");
                campoFecha.requestFocus();
                return;
            }

            if (horarioStr.isEmpty()) {
                labelError.setText("El horario es obligatorio.");
                campoHorario.requestFocus();
                return;
            }

            LocalTime horario;
            try {
                horario = LocalTime.parse(horarioStr);
            } catch (DateTimeParseException ex) {
                labelError.setText("Formato de horario incorrecto. Use HH:MM.");
                campoHorario.requestFocus();
                return;
            }

            try {
                String tituloPelicula = (String) comboPelicula.getSelectedItem();
                Pelicula pelicula = ventana.getPeliculaServicio().buscarPelicula(tituloPelicula);
                ventana.getEntradaServicio().programarFuncionStreaming(pelicula, enlace, capacidad, fecha, horario);
                cargarDatos();
                ventana.setEstado("Función streaming agregada: " + tituloPelicula);
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
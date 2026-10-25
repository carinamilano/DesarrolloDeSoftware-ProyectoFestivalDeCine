package ui.peliculas;

import excepciones.FestivalException;
import modelo.Director;
import modelo.Genero;
import modelo.Pelicula;
import servicios.PeliculaServicio;
import servicios.PersonaServicio;
import ui.VentanaPrincipal;
import ui.comun.CampoInvalidoException;
import ui.comun.ComponentesUI;
import ui.comun.LectorCampos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class PanelPeliculas extends JPanel {

    private VentanaPrincipal ventana;
    private PeliculaServicio peliculaServicio;
    private PersonaServicio personaServicio;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;

    public PanelPeliculas(VentanaPrincipal ventana, PeliculaServicio peliculaServicio, PersonaServicio personaServicio) {
        this.ventana = ventana;
        this.peliculaServicio = peliculaServicio;
        this.personaServicio = personaServicio;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        construirTabla();
        construirPanelBotones();
        cargarDatos();
    }

    private void construirTabla() {
        modeloTabla = ComponentesUI.crearModeloTabla("Título", "Género", "Duración (min)", "Director", "Promedio");
        tabla = ComponentesUI.crearTabla(modeloTabla);
        sorter = new TableRowSorter<>(modeloTabla);
        tabla.setRowSorter(sorter);
        add(ComponentesUI.crearEncabezado("Gestión de Películas", ComponentesUI.crearBuscador(sorter)), BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    private void construirPanelBotones() {
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JButton btnAgregar = new JButton("Agregar película");
        btnAgregar.addActionListener(e -> agregarPelicula());

        JButton btnEliminar = new JButton("Eliminar película");
        btnEliminar.addActionListener(e -> eliminarPelicula());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEliminar);

        add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        for (Pelicula p : peliculaServicio.getPeliculas()) {
            modeloTabla.addRow(new Object[]{
                    p.getTitulo(),
                    p.getGenero(),
                    p.getDuracion(),
                    p.getDirector().getNombre(),
                    String.format("%.2f", p.calcularPromedio())
            });
        }
    }

    private void agregarPelicula() {
        if (personaServicio.getDirectores().isEmpty()) {
            ComponentesUI.mostrarAviso(ventana, "No hay directores registrados. Agregue un director primero.");
            return;
        }

        JDialog dialog = new JDialog(ventana, "Agregar película", true);
        dialog.setSize(420, 300);
        dialog.setLocationRelativeTo(ventana);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel panelCampos = ComponentesUI.crearPanelCampos(4);
        JTextField campoTitulo = new JTextField();
        // CORRECCION S4: el combo usa el enum Genero.
        JComboBox<Genero> comboGenero = new JComboBox<>(Genero.values());
        JTextField campoDuracion = new JTextField();
        // CORRECCION I10/S10: el combo guarda los directores y muestra "nombre <email>".
        JComboBox<Director> comboDirector = ComponentesUI.crearComboPersonas(personaServicio.getDirectores());

        panelCampos.add(new JLabel("Título:"));
        panelCampos.add(campoTitulo);
        panelCampos.add(new JLabel("Género:"));
        panelCampos.add(comboGenero);
        panelCampos.add(new JLabel("Duración (min):"));
        panelCampos.add(campoDuracion);
        panelCampos.add(new JLabel("Director:"));
        panelCampos.add(comboDirector);

        JLabel labelError = ComponentesUI.crearLabelError();
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");

        campoTitulo.addActionListener(e -> campoDuracion.requestFocus());
        campoDuracion.addActionListener(e -> btnAceptar.doClick());
        btnCancelar.addActionListener(e -> dialog.dispose());

        // CORRECCION I9/S7: que la duración sea mayor a 0 lo valida Pelicula, acá solo el formato.
        btnAceptar.addActionListener(e -> {
            try {
                String titulo = LectorCampos.texto(campoTitulo, "Título");
                int duracion = LectorCampos.entero(campoDuracion, "Duración");
                peliculaServicio.registrarPelicula(titulo, ComponentesUI.seleccionado(comboGenero), duracion,
                        ComponentesUI.seleccionado(comboDirector));
                cargarDatos();
                ventana.setEstado("Película agregada: " + titulo);
                dialog.dispose();
            } catch (CampoInvalidoException ex) {
                labelError.setText(ex.getMessage());
                ex.getCampo().requestFocus();
            } catch (FestivalException ex) {
                labelError.setText(ex.getMessage());
            } catch (RuntimeException ex) {
                labelError.setText(ComponentesUI.errorInesperado(ex));
            }
        });

        dialog.add(panelCampos, BorderLayout.CENTER);
        dialog.add(ComponentesUI.crearPanelSur(labelError, btnAceptar, btnCancelar), BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void eliminarPelicula() {
        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada == -1) {
            ComponentesUI.mostrarAviso(ventana, "Seleccione una película de la tabla.");
            return;
        }

        int filaModelo = tabla.convertRowIndexToModel(filaSeleccionada);
        String titulo = (String) modeloTabla.getValueAt(filaModelo, 0);

        int confirmacion = JOptionPane.showConfirmDialog(ventana,
                "¿Está seguro que desea eliminar la película \"" + titulo + "\"?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                /* CORRECCION P1: borrar una película ya no impide abrir la aplicación, porque la demo
                solo corre la primera vez (ver InicializadorDatos).*/
                peliculaServicio.eliminarPelicula(peliculaServicio.buscarPelicula(titulo));
                cargarDatos();
                ventana.setEstado("Película eliminada: " + titulo);
            } catch (FestivalException ex) {
                JOptionPane.showMessageDialog(ventana, ex.getMessage(),
                        "No se puede eliminar", JOptionPane.WARNING_MESSAGE);
            } catch (RuntimeException ex) {
                ComponentesUI.mostrarError(ventana, ComponentesUI.errorInesperado(ex));
            }
        }
    }
}

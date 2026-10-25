package ui.peliculas;

import excepciones.FestivalException;
import modelo.Actor;
import modelo.Pelicula;
import servicios.PeliculaServicio;
import servicios.PersonaServicio;
import ui.VentanaPrincipal;
import ui.comun.ComponentesUI;
import ui.comun.DialogoPersona;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class PanelActores extends JPanel {

    private VentanaPrincipal ventana;
    private PersonaServicio personaServicio;
    private PeliculaServicio peliculaServicio;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;

    public PanelActores(VentanaPrincipal ventana, PersonaServicio personaServicio, PeliculaServicio peliculaServicio) {
        this.ventana = ventana;
        this.personaServicio = personaServicio;
        this.peliculaServicio = peliculaServicio;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        construirTabla();
        construirPanelBotones();
        cargarDatos();
    }

    private void construirTabla() {
        modeloTabla = ComponentesUI.crearModeloTabla("Nombre", "Email", "Nacionalidad");
        JTable tabla = ComponentesUI.crearTabla(modeloTabla);
        sorter = new TableRowSorter<>(modeloTabla);
        tabla.setRowSorter(sorter);
        add(ComponentesUI.crearEncabezado("Gestión de Actores", ComponentesUI.crearBuscador(sorter)), BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    private void construirPanelBotones() {
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JButton btnAgregar = new JButton("Agregar actor");
        btnAgregar.addActionListener(e -> agregarActor());

        JButton btnAsociar = new JButton("Asociar actor a película");
        btnAsociar.addActionListener(e -> asociarActorAPelicula());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnAsociar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        for (Actor a : personaServicio.getActores()) {
            modeloTabla.addRow(new Object[]{
                    a.getNombre(),
                    a.getEmail(),
                    a.getNacionalidad()
            });
        }
    }

    // CORRECCION S8: usa el formulario común DialogoPersona.
    private void agregarActor() {
        DialogoPersona dialogo = new DialogoPersona(ventana, "Agregar actor");
        while (dialogo.mostrar()) {
            try {
                Actor a = personaServicio.registrarActor(dialogo.getNombre(), dialogo.getEmail(), dialogo.getNacionalidad());
                cargarDatos();
                ventana.setEstado("Actor agregado: " + a.getNombre());
                break;
            } catch (FestivalException ex) {
                dialogo.mostrarError(ex.getMessage());
            } catch (RuntimeException ex) {
                dialogo.mostrarError(ComponentesUI.errorInesperado(ex));
            }
        }
        dialogo.dispose();
    }

    private void asociarActorAPelicula() {
        if (personaServicio.getActores().isEmpty() || peliculaServicio.getPeliculas().isEmpty()) {
            ComponentesUI.mostrarAviso(ventana, "Se necesitan actores y películas registrados.");
            return;
        }

        JDialog dialog = new JDialog(ventana, "Asociar actor a película", true);
        dialog.setSize(420, 220);
        dialog.setLocationRelativeTo(ventana);
        dialog.setLayout(new BorderLayout(10, 10));

        // CORRECCION I10/S10: los combos guardan los objetos, el actor se muestra con su email.
        JComboBox<Actor> comboActor = ComponentesUI.crearComboPersonas(personaServicio.getActores());
        JComboBox<Pelicula> comboPelicula = ComponentesUI.crearComboPeliculas(peliculaServicio.getPeliculas());

        JPanel panelCampos = ComponentesUI.crearPanelCampos(2);
        panelCampos.add(new JLabel("Actor:"));
        panelCampos.add(comboActor);
        panelCampos.add(new JLabel("Película:"));
        panelCampos.add(comboPelicula);

        JLabel labelError = ComponentesUI.crearLabelError();
        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");

        btnCancelar.addActionListener(e -> dialog.dispose());

        btnAceptar.addActionListener(e -> {
            Actor actor = ComponentesUI.seleccionado(comboActor);
            Pelicula pelicula = ComponentesUI.seleccionado(comboPelicula);
            try {
                // CORRECCION I8: si el actor ya está en el reparto, Pelicula lo rechaza.
                peliculaServicio.asociarActorAPelicula(actor, pelicula);
                cargarDatos();
                ventana.setEstado("Actor " + actor.getNombre() + " asociado a " + pelicula.getTitulo());
                dialog.dispose();
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
}

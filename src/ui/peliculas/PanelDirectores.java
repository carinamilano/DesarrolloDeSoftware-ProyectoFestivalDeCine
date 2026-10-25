package ui.peliculas;

import excepciones.FestivalException;
import modelo.Director;
import servicios.PersonaServicio;
import ui.VentanaPrincipal;
import ui.comun.ComponentesUI;
import ui.comun.DialogoPersona;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class PanelDirectores extends JPanel {

    private VentanaPrincipal ventana;
    private PersonaServicio personaServicio;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;

    public PanelDirectores(VentanaPrincipal ventana, PersonaServicio personaServicio) {
        this.ventana = ventana;
        this.personaServicio = personaServicio;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        construirTabla();
        construirPanelBotones();
        cargarDatos();
    }

    private void construirTabla() {
        modeloTabla = ComponentesUI.crearModeloTabla("Nombre", "Email", "Nacionalidad", "Películas");
        JTable tabla = ComponentesUI.crearTabla(modeloTabla);
        sorter = new TableRowSorter<>(modeloTabla);
        tabla.setRowSorter(sorter);
        add(ComponentesUI.crearEncabezado("Gestión de Directores", ComponentesUI.crearBuscador(sorter)), BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    private void construirPanelBotones() {
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JButton btnAgregar = new JButton("Agregar director");
        btnAgregar.addActionListener(e -> agregarDirector());

        panelBotones.add(btnAgregar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        for (Director d : personaServicio.getDirectores()) {
            modeloTabla.addRow(new Object[]{
                    d.getNombre(),
                    d.getEmail(),
                    d.getNacionalidad(),
                    d.getPeliculasDirigidas().size()
            });
        }
    }

    /* CORRECCION S8: usa el formulario común DialogoPersona. Si el email ya existe (I8),
    se muestra el error y el formulario vuelve a abrirse con lo escrito.*/
    private void agregarDirector() {
        DialogoPersona dialogo = new DialogoPersona(ventana, "Agregar director");
        while (dialogo.mostrar()) {
            try {
                Director d = personaServicio.registrarDirector(dialogo.getNombre(), dialogo.getEmail(), dialogo.getNacionalidad());
                cargarDatos();
                ventana.setEstado("Director agregado: " + d.getNombre());
                break;
            } catch (FestivalException ex) {
                dialogo.mostrarError(ex.getMessage());
            } catch (RuntimeException ex) {
                dialogo.mostrarError(ComponentesUI.errorInesperado(ex));
            }
        }
        dialogo.dispose();
    }
}

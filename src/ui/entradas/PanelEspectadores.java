package ui.entradas;

import modelo.Espectador;
import servicios.PersonaServicio;
import ui.VentanaPrincipal;
import ui.comun.ComponentesUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

/* CORRECCIÓN S9: los métodos static que usaban otros paneles (asegurarEspectadores, crearSelectorEspectador
y el alta) pasaron a ComponentesUI; el formulario de alta es el DialogoPersona común (S8).*/
public class PanelEspectadores extends JPanel {

    private VentanaPrincipal ventana;
    private PersonaServicio personaServicio;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;

    public PanelEspectadores(VentanaPrincipal ventana, PersonaServicio personaServicio) {
        this.ventana = ventana;
        this.personaServicio = personaServicio;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        construirTabla();
        construirPanelBotones();

        cargarDatos();
    }

    private void construirTabla() {
        modeloTabla = ComponentesUI.crearModeloTabla("Nombre", "Email", "Nacionalidad", "Entradas compradas");
        JTable tabla = ComponentesUI.crearTabla(modeloTabla);
        sorter = new TableRowSorter<>(modeloTabla);
        tabla.setRowSorter(sorter);

        add(ComponentesUI.crearEncabezado("Gestión de Espectadores", ComponentesUI.crearBuscador(sorter)), BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    private void construirPanelBotones() {
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JButton btnAgregar = new JButton("Agregar espectador");
        btnAgregar.addActionListener(e -> agregarEspectador());

        panelBotones.add(btnAgregar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        for (Espectador e : personaServicio.getEspectadores()) {
            modeloTabla.addRow(new Object[]{
                    e.getNombre(),
                    e.getEmail(),
                    e.getNacionalidad(),
                    e.getEntradas().size()
            });
        }
    }

    private void agregarEspectador() {
        Espectador nuevo = ComponentesUI.darDeAltaEspectador(ventana, personaServicio);
        if (nuevo != null) {
            cargarDatos();
            ventana.setEstado("Espectador agregado: " + nuevo.getNombre());
        }
    }
}

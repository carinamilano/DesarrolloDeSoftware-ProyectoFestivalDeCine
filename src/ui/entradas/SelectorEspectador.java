package ui.entradas;

import modelo.Espectador;
import ui.VentanaPrincipal;
import ui.comun.DialogoPersona;

import javax.swing.*;
import java.awt.BorderLayout;

// Selección y alta de espectadores desde las pantallas de venta de entradas y abonos.
// Antes eran métodos static dentro de PanelEspectadores
public final class SelectorEspectador {

    private SelectorEspectador() {
    }

    // Si no hay espectadores registrados, ofrece dar de alta uno. Devuelve true si se puede seguir
    public static boolean asegurarEspectadores(VentanaPrincipal ventana, String titulo) {
        if (!ventana.getSistema().getPersonaServicio().getEspectadores().isEmpty()) return true;
        int opcion = JOptionPane.showConfirmDialog(ventana,
                "No hay espectadores registrados. ¿Desea dar de alta uno ahora?",
                titulo, JOptionPane.YES_NO_OPTION);
        return opcion == JOptionPane.YES_OPTION && darDeAlta(ventana) != null;
    }

    // Combo de espectadores con un botón "Nuevo..." para dar de alta uno sin salir de la venta
    public static JPanel crear(VentanaPrincipal ventana, JComboBox<String> combo) {
        for (Espectador espectador : ventana.getSistema().getPersonaServicio().getEspectadores()) {
            combo.addItem(espectador.getNombre());
        }
        JButton botonNuevo = new JButton("Nuevo...");
        botonNuevo.setToolTipText("Dar de alta un espectador nuevo");
        botonNuevo.addActionListener(e -> {
            String nuevo = darDeAlta(ventana);
            if (nuevo != null) {
                combo.addItem(nuevo);
                combo.setSelectedItem(nuevo);
            }
        });
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.add(combo, BorderLayout.CENTER);
        panel.add(botonNuevo, BorderLayout.EAST);
        return panel;
    }

    // Devuelve el nombre del espectador registrado, o null si se canceló
    public static String darDeAlta(VentanaPrincipal ventana) {
        String nombre = DialogoPersona.mostrar(ventana, "Agregar espectador",
                ventana.getSistema().getPersonaServicio()::registrarEspectador);
        if (nombre != null) {
            ventana.setEstado("Espectador agregado: " + nombre);
        }
        return nombre;
    }
}

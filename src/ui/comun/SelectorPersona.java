package ui.comun;

import modelo.personas.Persona;

import javax.swing.*;
import java.awt.BorderLayout;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

// Combos de personas. Muestran nombre y email porque ahora puede haber dos personas con el mismo nombre
public final class SelectorPersona {

    private SelectorPersona() {
    }

    public static <T extends Persona> JComboBox<T> crearCombo(List<T> personas) {
        return ComponentesUI.crearCombo(personas, Persona::getNombreYEmail);
    }

    // Combo con un botón "Nuevo..." para dar de alta una persona sin salir del formulario
    public static <T extends Persona> JPanel conBotonNuevo(JComboBox<T> combo, String ayuda, Supplier<Optional<T>> alta) {
        JButton botonNuevo = new JButton("Nuevo...");
        botonNuevo.setToolTipText(ayuda);
        botonNuevo.addActionListener(e -> alta.get().ifPresent(nueva -> {
            combo.addItem(nueva);
            combo.setSelectedItem(nueva);
        }));
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.add(combo, BorderLayout.CENTER);
        panel.add(botonNuevo, BorderLayout.EAST);
        return panel;
    }
}

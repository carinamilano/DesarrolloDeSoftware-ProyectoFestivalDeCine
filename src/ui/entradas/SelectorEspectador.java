package ui.entradas;

import modelo.personas.Espectador;
import servicios.PersonaServicio;
import ui.VentanaPrincipal;
import ui.comun.DialogoPersona;
import ui.comun.SelectorPersona;

import javax.swing.*;
import java.util.List;
import java.util.Optional;

// Selección y alta de espectadores desde las pantallas de venta de entradas y abonos
public final class SelectorEspectador {

    private SelectorEspectador() {
    }

    // Si no hay espectadores registrados, ofrece dar de alta uno. Devuelve true si se puede seguir
    public static boolean asegurarEspectadores(VentanaPrincipal ventana, PersonaServicio personas, String titulo) {
        if (!personas.getEspectadores().isEmpty()) return true;
        int opcion = JOptionPane.showConfirmDialog(ventana,
                "No hay espectadores registrados. ¿Desea dar de alta uno ahora?",
                titulo, JOptionPane.YES_NO_OPTION);
        return opcion == JOptionPane.YES_OPTION && darDeAlta(ventana, personas).isPresent();
    }

    // Combo de espectadores con un botón "Nuevo..." para dar de alta uno sin salir de la venta
    public static JPanel crear(VentanaPrincipal ventana, PersonaServicio personas, JComboBox<Espectador> combo) {
        personas.getEspectadores().forEach(combo::addItem);
        return SelectorPersona.conBotonNuevo(combo, "Dar de alta un espectador nuevo", () -> darDeAlta(ventana, personas));
    }

    // Combo vacío: lo llena crear(...)
    public static JComboBox<Espectador> crearCombo() {
        return SelectorPersona.crearCombo(List.of());
    }

    public static Optional<Espectador> darDeAlta(VentanaPrincipal ventana, PersonaServicio personas) {
        Optional<Espectador> nuevo = DialogoPersona.mostrar(ventana, "Agregar espectador", personas::registrarEspectador);
        nuevo.ifPresent(espectador -> ventana.setEstado("Espectador agregado: " + espectador.getNombre()));
        return nuevo;
    }
}

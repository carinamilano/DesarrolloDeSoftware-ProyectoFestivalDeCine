package app;

import excepciones.PersistenciaException;
import inicializacion.InicializadorDatos;
import persistencia.Persistencia;
import persistencia.PersistenciaArchivos;
import repositorio.Catalogo;
import servicios.SistemaFestival;
import ui.VentanaPrincipal;

import javax.swing.*;

// Punto de entrada: arma las piezas y las conecta. Es el único lugar que conoce las clases concretas
public class Main {
    public static void main(String[] args) {
        Catalogo catalogo = new Catalogo("Festival Internacional de Cine");
        Persistencia persistencia = new PersistenciaArchivos(catalogo);

        try {
            persistencia.cargar();
        } catch (PersistenciaException e) {
            JOptionPane.showMessageDialog(null,
                    "Advertencia al cargar datos: " + e.getMessage(),
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
        }

        SistemaFestival sistema = new SistemaFestival(catalogo);
        new InicializadorDatos(sistema).inicializar();

        SwingUtilities.invokeLater(() -> new VentanaPrincipal(sistema, persistencia).setVisible(true));
    }
}

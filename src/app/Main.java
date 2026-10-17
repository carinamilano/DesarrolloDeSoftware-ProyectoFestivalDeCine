package app;

import excepciones.FestivalException;
import excepciones.PersistenciaException;
import inicializacion.InicializadorDatos;
import persistencia.Persistencia;
import persistencia.PersistenciaArchivos;
import repositorio.Catalogo;
import servicios.SistemaFestival;
import ui.VentanaPrincipal;

import javax.swing.*;
import java.time.Clock;
import java.util.List;

// Punto de entrada: arma las piezas y las conecta. Es el único lugar que conoce las clases concretas
// y el único que decide qué reloj se usa (el del sistema)
public class Main {
    private static final int ERRORES_A_MOSTRAR = 15;

    public static void main(String[] args) {
        Clock reloj = Clock.systemDefaultZone();
        Catalogo catalogo = new Catalogo("Festival Internacional de Cine");
        Persistencia persistencia = new PersistenciaArchivos(catalogo, reloj);

        try {
            persistencia.cargar();
        } catch (PersistenciaException e) {
            advertir("Advertencia al cargar datos", e.getMessage(), e.getErrores());
        }

        SistemaFestival sistema = new SistemaFestival(catalogo, reloj);
        try {
            new InicializadorDatos(sistema, reloj).inicializar();
        } catch (FestivalException e) {
            advertir("Datos de demostración", "No se pudieron generar todos los datos de demostración.", List.of(e.getMessage()));
        }

        SwingUtilities.invokeLater(() -> new VentanaPrincipal(sistema, persistencia).setVisible(true));
    }

    private static void advertir(String titulo, String mensaje, List<String> errores) {
        StringBuilder texto = new StringBuilder(mensaje);
        errores.stream().limit(ERRORES_A_MOSTRAR).forEach(error -> texto.append("\n• ").append(error));
        if (errores.size() > ERRORES_A_MOSTRAR) {
            texto.append("\n… y ").append(errores.size() - ERRORES_A_MOSTRAR).append(" más.");
        }
        JOptionPane.showMessageDialog(null, texto.toString(), titulo, JOptionPane.WARNING_MESSAGE);
    }
}

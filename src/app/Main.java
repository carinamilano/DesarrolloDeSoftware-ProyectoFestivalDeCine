package app;

import excepciones.FestivalException;
import excepciones.PersistenciaException;
import modelo.Festival;
import persistencia.Persistencia;
import servicios.*;
import ui.VentanaPrincipal;

import javax.swing.*;
import java.util.List;

public class Main {
    private static final int MAXIMO_ERRORES_A_MOSTRAR = 15;

    public static void main(String[] args) {

        Festival festival = new Festival("Festival Internacional de Cine");

        // CORRECCION S1/S2: los servicios quedaron divididos por tema para aligerar las responsbilidades.

        PersonaServicio personaServicio = new PersonaServicio();
        PeliculaServicio peliculaServicio = new PeliculaServicio(festival);
        FestivalServicio festivalServicio = new FestivalServicio(festival, peliculaServicio);
        FuncionServicio funcionServicio = new FuncionServicio(festival);
        VentaServicio ventaServicio = new VentaServicio();
        AbonoServicio abonoServicio = new AbonoServicio(funcionServicio, ventaServicio);
        EvaluacionServicio evaluacionServicio = new EvaluacionServicio(peliculaServicio);

        Persistencia persistencia = new Persistencia(Persistencia.CARPETA_DATOS, personaServicio, peliculaServicio, festivalServicio, funcionServicio, ventaServicio, abonoServicio, evaluacionServicio);

        // CORRECCION P2: las lineas con errores se saltean y se muestran todas juntas. La aplicación abre igual.

        try {
            persistencia.cargar();
        } catch (PersistenciaException e) {
            JOptionPane.showMessageDialog(null,
                    e.getMessage() + "\n\n" + resumirErrores(e.getErrores())
                            + "\n\nCarpeta de datos: " + persistencia.getCarpeta(),
                    "Advertencia al cargar datos", JOptionPane.WARNING_MESSAGE);
        }

        // CORRECCION P1: Antes si un archivo faltaba o no se podia inicializar no abria directamente el programa. Ahora, si la demo falla, se avisa y la aplicación abre igual

        InicializadorDatos inicializador = new InicializadorDatos(personaServicio, peliculaServicio, festivalServicio,
                funcionServicio, ventaServicio, abonoServicio, evaluacionServicio);
        try {
            inicializador.inicializar();
        } catch (FestivalException e) {
            JOptionPane.showMessageDialog(null, "No se pudieron cargar todos los datos de ejemplo: " + e.getMessage(), "Advertencia", JOptionPane.WARNING_MESSAGE);
        }

        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal(personaServicio, peliculaServicio, festivalServicio, funcionServicio, ventaServicio, abonoServicio, evaluacionServicio, persistencia);
            ventana.setVisible(true);
        });
    }

    private static String resumirErrores(List<String> errores) {
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < errores.size() && i < MAXIMO_ERRORES_A_MOSTRAR; i++) {
            texto.append("• ").append(errores.get(i)).append("\n");
        }
        if (errores.size() > MAXIMO_ERRORES_A_MOSTRAR) {
            texto.append("... y ").append(errores.size() - MAXIMO_ERRORES_A_MOSTRAR).append(" más.");
        }
        return texto.toString();
    }
}

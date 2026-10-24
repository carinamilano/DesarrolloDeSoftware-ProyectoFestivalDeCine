package app;

import excepciones.PersistenciaException;
import modelo.Festival;
import persistencia.Persistencia;
import servicios.EntradaServicio;
import servicios.FestivalServicio;
import servicios.InicializadorDatos;
import servicios.PeliculaServicio;
import ui.VentanaPrincipal;
import javax.swing.*;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        Festival festival = new Festival("Festival Internacional de Cine");
        Persistencia persistencia = new Persistencia();
        FestivalServicio festivalServicio = new FestivalServicio(festival, new ArrayList<>());
        PeliculaServicio peliculaServicio = new PeliculaServicio();
        EntradaServicio entradaServicio = new EntradaServicio();

        try {
            persistencia.cargarSalas(festivalServicio.getSalas());
            persistencia.cargarDirectores(peliculaServicio.getDirectores());
            persistencia.cargarActores(peliculaServicio.getActores());
            persistencia.cargarJurados(peliculaServicio.getJurados());
            persistencia.cargarEspectadores(entradaServicio.getEspectadores());
            persistencia.cargarPeliculas(peliculaServicio.getPeliculas(), peliculaServicio.getDirectores());
            persistencia.cargarEvaluaciones(peliculaServicio.getEvaluaciones(), peliculaServicio.getPeliculas(), peliculaServicio.getJurados());
        } catch (PersistenciaException e) {
            JOptionPane.showMessageDialog(null,
                    "Advertencia al cargar datos: " + e.getMessage(),
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
        }

        InicializadorDatos inicializador = new InicializadorDatos(festivalServicio, peliculaServicio, entradaServicio);
        inicializador.inicializar();

        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal(
                    festivalServicio, peliculaServicio, entradaServicio, persistencia
            );
            ventana.setVisible(true);
        });
    }
}
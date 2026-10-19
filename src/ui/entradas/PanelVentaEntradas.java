package ui.entradas;

import modelo.abonos.Abono;
import modelo.funciones.Entrada;
import modelo.funciones.Funcion;
import servicios.FuncionServicio;
import servicios.PersonaServicio;
import servicios.VentaServicio;
import ui.VentanaPrincipal;
import ui.comun.ComponentesUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelVentaEntradas extends JPanel {
    private final VentanaPrincipal ventana;
    private final VentaServicio ventaServicio;
    private final FuncionServicio funcionServicio;
    private final PersonaServicio personaServicio;
    private final DefaultTableModel modeloTabla;

    public PanelVentaEntradas(VentanaPrincipal ventana, VentaServicio ventaServicio, FuncionServicio funcionServicio,
                              PersonaServicio personaServicio) {
        this.ventana = ventana;
        this.ventaServicio = ventaServicio;
        this.funcionServicio = funcionServicio;
        this.personaServicio = personaServicio;
        ComponentesUI.configurarPanel(this);

        modeloTabla = ComponentesUI.crearModeloTabla("Código", "Película", "Tipo", "Fecha", "Espectador", "Detalle", "Precio");
        add(ComponentesUI.crearTitulo("Venta de Entradas"), BorderLayout.NORTH);
        add(new JScrollPane(ComponentesUI.crearTabla(modeloTabla)), BorderLayout.CENTER);
        // Solo se ofrecen las funciones que todavía no comenzaron
        add(ComponentesUI.crearPanelBotones(
                ComponentesUI.crearBoton("Vender entrada presencial", () -> venderEntrada("Vender entrada presencial",
                        "No hay funciones presenciales a la venta.", funcionServicio.getFuncionesPresencialesEnVenta())),
                ComponentesUI.crearBoton("Vender entrada streaming", () -> venderEntrada("Vender entrada streaming",
                        "No hay funciones streaming a la venta.", funcionServicio.getFuncionesStreamingEnVenta()))),
                BorderLayout.SOUTH);

        cargarDatos();
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        for (Entrada entrada : ventaServicio.getEntradas()) {
            Funcion funcion = entrada.getFuncion();
            String tipo = funcion.getModalidad() + entrada.getAbono().map(Abono::getCodigo).map(codigo -> " (abono " + codigo + ")").orElse("");
            modeloTabla.addRow(new Object[]{
                    entrada.getCodigo(),
                    funcion.getTituloPelicula(),
                    tipo,
                    funcion.getFecha().toString(),
                    entrada.getEspectador().getNombre(),
                    entrada.getDetalle(),
                    ComponentesUI.formatearPrecio(entrada.getPrecio())
            });
        }
    }

    private void venderEntrada(String titulo, String siNoHayFunciones, List<? extends Funcion> funciones) {
        if (funciones.isEmpty()) {
            ComponentesUI.avisar(ventana, siNoHayFunciones);
            return;
        }
        if (!SelectorEspectador.asegurarEspectadores(ventana, personaServicio, titulo)) return;
        new DialogoVentaEntrada(ventana, ventaServicio, personaServicio, titulo, funciones, this::cargarDatos).mostrar();
    }
}

package ui.entradas;

import modelo.Entrada;
import servicios.FuncionServicio;
import servicios.PersonaServicio;
import servicios.VentaServicio;
import ui.VentanaPrincipal;
import ui.comun.ComponentesUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

// CORRECCION S8: los diálogos de venta pasaron a DialogoVentaPresencial y DialogoVentaStreaming.
public class PanelVentaEntradas extends JPanel {

    private VentanaPrincipal ventana;
    private VentaServicio ventaServicio;
    private FuncionServicio funcionServicio;
    private PersonaServicio personaServicio;
    private DefaultTableModel modeloTabla;

    public PanelVentaEntradas(VentanaPrincipal ventana, VentaServicio ventaServicio,
                              FuncionServicio funcionServicio, PersonaServicio personaServicio) {
        this.ventana = ventana;
        this.ventaServicio = ventaServicio;
        this.funcionServicio = funcionServicio;
        this.personaServicio = personaServicio;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(ComponentesUI.crearEncabezado("Venta de Entradas", null), BorderLayout.NORTH);
        construirTabla();
        construirPanelBotones();
        cargarDatos();
    }

    private void construirTabla() {
        modeloTabla = ComponentesUI.crearModeloTabla("Código", "Película", "Tipo", "Fecha", "Espectador", "Detalle", "Precio");
        add(new JScrollPane(ComponentesUI.crearTabla(modeloTabla)), BorderLayout.CENTER);
    }

    private void construirPanelBotones() {
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JButton btnVenderPresencial = new JButton("Vender entrada presencial");
        btnVenderPresencial.addActionListener(e -> venderEntradaPresencial());

        JButton btnVenderStreaming = new JButton("Vender entrada streaming");
        btnVenderStreaming.addActionListener(e -> venderEntradaStreaming());

        panelBotones.add(btnVenderPresencial);
        panelBotones.add(btnVenderStreaming);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        for (Entrada entrada : ventaServicio.getEntradas()) {
            // CORRECCION S3/S11: el tipo y el detalle los responde la entrada, sin instanceof ni cadenas de getters.
            String tipo = entrada.getTipoFuncion().toString();
            if (entrada.esReservaDeAbono()) {
                tipo += " (abono " + entrada.getAbono().getCodigo() + ")";
            }
            modeloTabla.addRow(new Object[]{
                    entrada.getCodigo(),
                    entrada.getTituloPelicula(),
                    tipo,
                    entrada.getFuncion().getFecha().toString(),
                    entrada.getEspectador().getNombre(),
                    entrada.getDetalle(),
                    ComponentesUI.formatearPrecio(entrada.getPrecio())
            });
        }
    }

    private void venderEntradaPresencial() {
        if (funcionServicio.getFuncionesPresencialesEnVenta().isEmpty()) {
            ComponentesUI.mostrarAviso(ventana, "No hay funciones presenciales disponibles.");
            return;
        }
        if (!ComponentesUI.asegurarEspectadores(ventana, personaServicio, "Vender entrada presencial")) return;

        DialogoVentaPresencial dialogo = new DialogoVentaPresencial(ventana, ventaServicio, funcionServicio, personaServicio);
        if (dialogo.mostrar()) {
            cargarDatos();
            Entrada entrada = dialogo.getUltimaEntrada();
            ventana.setEstado("Entrada vendida: " + entrada.getCodigo() + " - Precio: " + ComponentesUI.formatearPrecio(entrada.getPrecio()));
        }
    }

    private void venderEntradaStreaming() {
        if (funcionServicio.getFuncionesStreamingEnVenta().isEmpty()) {
            ComponentesUI.mostrarAviso(ventana, "No hay funciones streaming disponibles.");
            return;
        }
        if (!ComponentesUI.asegurarEspectadores(ventana, personaServicio, "Vender entrada streaming")) return;

        DialogoVentaStreaming dialogo = new DialogoVentaStreaming(ventana, ventaServicio, funcionServicio, personaServicio);
        if (dialogo.mostrar()) {
            cargarDatos();
            Entrada entrada = dialogo.getEntradaVendida();
            ventana.setEstado("Entrada streaming vendida: " + entrada.getCodigo() + " - Precio: " + ComponentesUI.formatearPrecio(entrada.getPrecio()));
        }
    }
}

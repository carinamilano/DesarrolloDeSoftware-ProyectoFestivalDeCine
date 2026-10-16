package ui.entradas;

import modelo.*;
import servicios.SistemaFestival;
import ui.VentanaPrincipal;
import ui.comun.ComponentesUI;
import ui.comun.FormularioDialogo;
import ui.comun.LectorCampos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelVentaEntradas extends JPanel {
    private final VentanaPrincipal ventana;
    private final SistemaFestival sistema;
    private final DefaultTableModel modeloTabla;

    public PanelVentaEntradas(VentanaPrincipal ventana) {
        this.ventana = ventana;
        this.sistema = ventana.getSistema();
        ComponentesUI.configurarPanel(this);

        modeloTabla = ComponentesUI.crearModeloTabla("Código", "Película", "Tipo", "Fecha", "Espectador", "Detalle", "Precio");
        add(ComponentesUI.crearTitulo("Venta de Entradas"), BorderLayout.NORTH);
        add(new JScrollPane(ComponentesUI.crearTabla(modeloTabla)), BorderLayout.CENTER);
        add(ComponentesUI.crearPanelBotones(
                ComponentesUI.crearBoton("Vender entrada presencial", () -> venderEntrada("Vender entrada presencial",
                        "No hay funciones presenciales disponibles.", sistema.getFuncionServicio().getFuncionesPresenciales())),
                ComponentesUI.crearBoton("Vender entrada streaming", () -> venderEntrada("Vender entrada streaming",
                        "No hay funciones streaming disponibles.", sistema.getFuncionServicio().getFuncionesStreaming()))),
                BorderLayout.SOUTH);

        cargarDatos();
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        for (Entrada entrada : sistema.getVentaServicio().getEntradas()) {
            Funcion funcion = entrada.getFuncion();
            String tipo = funcion.getModalidad();
            if (entrada.esReservaDeAbono()) {
                tipo += " (abono " + entrada.getAbono().getCodigo() + ")";
            }
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

    // Un solo diálogo para las dos modalidades (antes eran dos métodos casi iguales):
    // si la función requiere butaca se agregan los campos de butaca
    private void venderEntrada(String titulo, String siNoHayFunciones, List<? extends Funcion> funciones) {
        if (funciones.isEmpty()) {
            ComponentesUI.avisar(ventana, siNoHayFunciones);
            return;
        }
        if (!SelectorEspectador.asegurarEspectadores(ventana, titulo)) return;
        boolean conButaca = funciones.get(0).requiereButaca();

        FormularioDialogo formulario = new FormularioDialogo(ventana, titulo, 500, conButaca ? 360 : 260);
        JComboBox<String> comboEspectador = new JComboBox<>();
        formulario.agregarCampo("Espectador:", SelectorEspectador.crear(ventana, comboEspectador));
        JComboBox<String> comboFuncion = formulario.agregarCampo("Función:", new JComboBox<>(
                funciones.stream().map(this::describir).toArray(String[]::new)));
        JLabel labelOcupadas = new JLabel();
        labelOcupadas.setForeground(ComponentesUI.COLOR_AVISO);
        labelOcupadas.setFont(ComponentesUI.FUENTE_AYUDA);
        JTextField campoButaca = new JTextField();
        if (conButaca) {
            formulario.agregarCampo("Butacas ocupadas:", labelOcupadas);
            formulario.agregarCampo("Número de butaca:", campoButaca);
            formulario.encadenarEnter(campoButaca);
        }
        JComboBox<TipoTarifa> comboTarifa = formulario.agregarCampo("Tarifa:", new JComboBox<>(TipoTarifa.values()));

        Runnable mostrarOcupacion = () -> labelOcupadas.setText(
                funciones.get(comboFuncion.getSelectedIndex()).getEstadoOcupacion());
        comboFuncion.addActionListener(e -> mostrarOcupacion.run());
        mostrarOcupacion.run();

        formulario.alAceptar(() -> {
            Funcion funcion = funciones.get(comboFuncion.getSelectedIndex());
            int butaca = conButaca
                    ? LectorCampos.entero(campoButaca, "El número de butaca es obligatorio.", "El número de butaca debe ser un entero.")
                    : SolicitudEntrada.SIN_BUTACA;
            Entrada entrada = sistema.getVentaServicio().venderEntrada((String) comboEspectador.getSelectedItem(),
                    funcion, (TipoTarifa) comboTarifa.getSelectedItem(), butaca);
            cargarDatos();
            ventana.setEstado("Entrada vendida: " + entrada.getCodigo() + " - Precio: " + ComponentesUI.formatearPrecio(entrada.getPrecio()));

            if (conButaca && quiereOtraButaca(formulario)) {
                mostrarOcupacion.run();
                campoButaca.setText("");
                formulario.limpiarError();
                campoButaca.requestFocus();
            } else {
                formulario.cerrar();
            }
        });
        formulario.mostrar();
    }

    private String describir(Funcion funcion) {
        return funcion.getResumen() + " — " + funcion.getLugar();
    }

    private boolean quiereOtraButaca(FormularioDialogo formulario) {
        return JOptionPane.showConfirmDialog(formulario.getDialogo(),
                "Entrada vendida correctamente.\n¿Desea comprar otra butaca para esta función?",
                "Entrada vendida", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }
}

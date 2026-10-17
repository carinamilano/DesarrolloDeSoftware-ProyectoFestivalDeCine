package ui.entradas;

import modelo.funciones.Butaca;
import modelo.funciones.Entrada;
import modelo.funciones.Funcion;
import modelo.personas.Espectador;
import modelo.precios.TipoTarifa;
import servicios.PersonaServicio;
import servicios.VentaServicio;
import ui.VentanaPrincipal;
import ui.comun.ComponentesUI;
import ui.comun.FormularioDialogo;
import ui.comun.LectorCampos;

import javax.swing.*;
import java.util.List;
import java.util.Optional;

// Venta de entradas sueltas (antes era un método largo dentro de PanelVentaEntradas).
// Un solo diálogo para las dos modalidades: si la función requiere butaca se agregan los campos de butaca
class DialogoVentaEntrada {
    private final VentanaPrincipal ventana;
    private final VentaServicio ventaServicio;
    private final PersonaServicio personaServicio;
    private final String titulo;
    private final List<? extends Funcion> funciones;
    private final Runnable alVender;

    DialogoVentaEntrada(VentanaPrincipal ventana, VentaServicio ventaServicio, PersonaServicio personaServicio,
                        String titulo, List<? extends Funcion> funciones, Runnable alVender) {
        this.ventana = ventana;
        this.ventaServicio = ventaServicio;
        this.personaServicio = personaServicio;
        this.titulo = titulo;
        this.funciones = funciones;
        this.alVender = alVender;
    }

    void mostrar() {
        boolean conButaca = funciones.get(0).requiereButaca();

        FormularioDialogo formulario = new FormularioDialogo(ventana, titulo, 540, conButaca ? 380 : 290);
        JComboBox<Espectador> comboEspectador = SelectorEspectador.crearCombo();
        formulario.agregarCampo("Espectador:", SelectorEspectador.crear(ventana, personaServicio, comboEspectador));
        JComboBox<Funcion> comboFuncion = formulario.agregarCampo("Función:",
                ComponentesUI.crearCombo(funciones, funcion -> funcion.getResumen() + " — " + funcion.getLugar()));
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
        JLabel labelPrecio = formulario.agregarCampo("Precio:", new JLabel());

        Runnable actualizar = () -> {
            Funcion funcion = ComponentesUI.seleccionado(comboFuncion);
            labelOcupadas.setText(funcion.getEstadoOcupacion());
            labelPrecio.setText(ComponentesUI.formatearPrecio(
                    ventaServicio.cotizar(funcion, ComponentesUI.seleccionado(comboTarifa))));
        };
        comboFuncion.addActionListener(e -> actualizar.run());
        comboTarifa.addActionListener(e -> actualizar.run());
        actualizar.run();

        formulario.alAceptar(() -> {
            Funcion funcion = ComponentesUI.seleccionado(comboFuncion);
            Optional<Butaca> butaca = conButaca
                    ? Optional.of(new Butaca(LectorCampos.entero(campoButaca,
                    "El número de butaca es obligatorio.", "El número de butaca debe ser un entero.")))
                    : Optional.empty();
            Entrada entrada = ventaServicio.venderEntrada(ComponentesUI.seleccionado(comboEspectador),
                    funcion, ComponentesUI.seleccionado(comboTarifa), butaca);
            alVender.run();
            ventana.setEstado("Entrada vendida: " + entrada.getCodigo() + " - Precio: " + ComponentesUI.formatearPrecio(entrada.getPrecio()));

            if (conButaca && quiereOtraButaca(formulario)) {
                actualizar.run();
                campoButaca.setText("");
                formulario.limpiarError();
                campoButaca.requestFocus();
            } else {
                formulario.cerrar();
            }
        });
        formulario.mostrar();
    }

    private boolean quiereOtraButaca(FormularioDialogo formulario) {
        return JOptionPane.showConfirmDialog(formulario.getDialogo(),
                "Entrada vendida correctamente.\n¿Desea comprar otra butaca para esta función?",
                "Entrada vendida", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }
}

package ui.entradas;

import modelo.abonos.Abono;
import modelo.funciones.Butaca;
import modelo.funciones.Entrada;
import modelo.funciones.Funcion;
import servicios.AbonoServicio;
import ui.comun.ComponentesUI;
import ui.comun.FormularioDialogo;
import ui.comun.LectorCampos;

import javax.swing.*;
import java.awt.Window;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

// Reserva de una función con un abono (antes era un método largo dentro de PanelAbonos)
class DialogoReservaAbono {
    private final Window padre;
    private final AbonoServicio abonoServicio;
    private final Abono abono;
    private final List<Funcion> funciones;

    DialogoReservaAbono(Window padre, AbonoServicio abonoServicio, Abono abono, List<Funcion> funciones) {
        this.padre = padre;
        this.abonoServicio = abonoServicio;
        this.abono = abono;
        this.funciones = funciones;
    }

    // Devuelve la reserva hecha, o vacío si se canceló
    Optional<Entrada> mostrar() {
        FormularioDialogo formulario = new FormularioDialogo(padre, "Reservar con " + abono.getCodigo(), 560, 280);
        formulario.setTextoAceptar("Reservar");
        formulario.agregarCampo("Espectador:", new JLabel(abono.getNombreEspectador() + " (" + abono.getTipo() + ")"));
        JComboBox<Funcion> comboFuncion = formulario.agregarCampo("Función:", ComponentesUI.crearCombo(funciones,
                funcion -> funcion.getResumen() + " — " + funcion.getModalidad() + " (" + funcion.getLugar() + ")"));
        JLabel labelOcupadas = formulario.agregarCampo("Butacas ocupadas:", new JLabel());
        labelOcupadas.setForeground(ComponentesUI.COLOR_AVISO);
        labelOcupadas.setFont(ComponentesUI.FUENTE_AYUDA);
        JTextField campoButaca = formulario.agregarCampo("Número de butaca:", new JTextField());
        formulario.encadenarEnter(campoButaca);

        Runnable actualizar = () -> {
            Funcion funcion = ComponentesUI.seleccionado(comboFuncion);
            labelOcupadas.setText(funcion.getEstadoOcupacion());
            campoButaca.setEnabled(funcion.requiereButaca());
            if (!funcion.requiereButaca()) campoButaca.setText("");
        };
        comboFuncion.addActionListener(e -> actualizar.run());
        actualizar.run();

        AtomicReference<Entrada> reservada = new AtomicReference<>();
        formulario.alAceptar(() -> {
            Funcion funcion = ComponentesUI.seleccionado(comboFuncion);
            Optional<Butaca> butaca = funcion.requiereButaca()
                    ? Optional.of(new Butaca(LectorCampos.entero(campoButaca,
                    "El número de butaca es obligatorio.", "El número de butaca debe ser un entero.")))
                    : Optional.empty();
            reservada.set(abonoServicio.reservar(abono, funcion, butaca));
            formulario.cerrar();
        });
        formulario.mostrar();
        return Optional.ofNullable(reservada.get());
    }
}

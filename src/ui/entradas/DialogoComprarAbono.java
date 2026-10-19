package ui.entradas;

import modelo.abonos.Abono;
import modelo.abonos.TipoAbono;
import modelo.personas.Espectador;
import modelo.precios.TipoTarifa;
import servicios.AbonoServicio;
import servicios.FuncionServicio;
import servicios.PersonaServicio;
import ui.VentanaPrincipal;
import ui.comun.ComponentesUI;
import ui.comun.FormularioDialogo;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

// Compra de un abono (antes era un método de 50 líneas dentro de PanelAbonos)
class DialogoComprarAbono {
    private final VentanaPrincipal ventana;
    private final AbonoServicio abonoServicio;
    private final FuncionServicio funcionServicio;
    private final PersonaServicio personaServicio;

    DialogoComprarAbono(VentanaPrincipal ventana, AbonoServicio abonoServicio, FuncionServicio funcionServicio,
                        PersonaServicio personaServicio) {
        this.ventana = ventana;
        this.abonoServicio = abonoServicio;
        this.funcionServicio = funcionServicio;
        this.personaServicio = personaServicio;
    }

    // Devuelve el abono comprado, o vacío si se canceló
    Optional<Abono> mostrar() {
        FormularioDialogo formulario = new FormularioDialogo(ventana, "Comprar abono", 520, 420);
        formulario.setTextoAceptar("Comprar");
        JComboBox<Espectador> comboEspectador = SelectorEspectador.crearCombo();
        formulario.agregarCampo("Espectador:", SelectorEspectador.crear(ventana, personaServicio, comboEspectador));
        JComboBox<TipoAbono> comboTipo = formulario.agregarCampo("Tipo de abono:", new JComboBox<>(TipoAbono.values()));
        JComboBox<TipoTarifa> comboTarifa = formulario.agregarCampo("Tarifa:", new JComboBox<>(TipoTarifa.values()));
        JLabel labelPrecio = formulario.agregarCampo("Precio:", new JLabel());
        labelPrecio.setFont(new Font("SansSerif", Font.BOLD, 13));
        JList<LocalDate> listaDias = crearListaDias(formulario);

        // El precio lo calcula el Tarifario, el mismo que calcula el de las entradas
        Runnable actualizar = () -> {
            TipoAbono tipo = ComponentesUI.seleccionado(comboTipo);
            listaDias.setEnabled(tipo == TipoAbono.TRES_DIAS);
            labelPrecio.setText(ComponentesUI.formatearPrecio(abonoServicio.cotizar(tipo, ComponentesUI.seleccionado(comboTarifa))));
        };
        comboTipo.addActionListener(e -> actualizar.run());
        comboTarifa.addActionListener(e -> actualizar.run());
        actualizar.run();

        AtomicReference<Abono> comprado = new AtomicReference<>();
        formulario.alAceptar(() -> {
            Espectador espectador = ComponentesUI.seleccionado(comboEspectador);
            TipoTarifa tarifa = ComponentesUI.seleccionado(comboTarifa);
            comprado.set(switch (ComponentesUI.seleccionado(comboTipo)) {
                case TRES_DIAS -> abonoServicio.comprarAbonoTresDias(espectador, tarifa, listaDias.getSelectedValuesList());
                case FULL -> abonoServicio.comprarAbonoFull(espectador, tarifa);
                case STREAMING -> abonoServicio.comprarAbonoStreaming(espectador, tarifa);
            });
            formulario.cerrar();
        });
        formulario.mostrar();
        return Optional.ofNullable(comprado.get());
    }

    // Lista de días (con funciones presenciales) para el abono de 3 días
    private JList<LocalDate> crearListaDias(FormularioDialogo formulario) {
        List<LocalDate> fechas = funcionServicio.getFechasConFuncionesPresenciales();
        JList<LocalDate> listaDias = new JList<>(fechas.toArray(new LocalDate[0]));
        listaDias.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listaDias.setVisibleRowCount(5);
        JPanel panelDias = new JPanel(new BorderLayout());
        panelDias.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 20, 0, 20),
                BorderFactory.createTitledBorder("Elegí 3 días (Ctrl + clic)")));
        panelDias.add(new JScrollPane(listaDias), BorderLayout.CENTER);
        formulario.agregarDebajo(panelDias);
        return listaDias;
    }
}

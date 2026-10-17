package ui.funciones;

import modelo.festival.Sala;
import modelo.funciones.Funcion;
import modelo.peliculas.Pelicula;
import servicios.FuncionServicio;
import ui.comun.ComponentesUI;
import ui.comun.FormularioDialogo;

import javax.swing.JComboBox;
import java.awt.Window;
import java.time.LocalDateTime;
import java.util.List;

class DialogoFuncionPresencial extends DialogoFuncion {
    private final List<Sala> salas;
    private JComboBox<Sala> comboSala;

    DialogoFuncionPresencial(Window padre, FuncionServicio funcionServicio, List<Sala> salas) {
        super(padre, funcionServicio);
        this.salas = salas;
    }

    @Override
    protected String getTitulo() {
        return "Agregar función presencial";
    }

    @Override
    protected int getAlto() {
        return 300;
    }

    @Override
    protected void agregarCamposPropios(FormularioDialogo formulario) {
        comboSala = formulario.agregarCampo("Sala:", ComponentesUI.crearCombo(salas, Sala::getNombre));
    }

    // La superposición con otras funciones de la sala (según la duración de la película) la valida el servicio
    @Override
    protected Funcion programar(Pelicula pelicula, LocalDateTime fechaHora) {
        return funcionServicio.programarFuncionPresencial(pelicula, ComponentesUI.seleccionado(comboSala), fechaHora);
    }
}

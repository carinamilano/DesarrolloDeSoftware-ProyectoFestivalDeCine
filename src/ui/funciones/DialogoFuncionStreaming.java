package ui.funciones;

import modelo.funciones.Funcion;
import modelo.peliculas.Pelicula;
import servicios.FuncionServicio;
import ui.comun.CampoInvalidoException;
import ui.comun.FormularioDialogo;
import ui.comun.LectorCampos;

import javax.swing.JTextField;
import java.awt.Window;
import java.time.LocalDateTime;

class DialogoFuncionStreaming extends DialogoFuncion {
    private JTextField campoEnlace;
    private JTextField campoCapacidad;

    DialogoFuncionStreaming(Window padre, FuncionServicio funcionServicio) {
        super(padre, funcionServicio);
    }

    @Override
    protected String getTitulo() {
        return "Agregar función streaming";
    }

    @Override
    protected int getAlto() {
        return 330;
    }

    @Override
    protected void agregarCamposPropios(FormularioDialogo formulario) {
        campoEnlace = formulario.agregarCampo("Enlace:", new JTextField());
        campoCapacidad = formulario.agregarCampo("Capacidad virtual (max 1000):", new JTextField());
        formulario.encadenarEnter(campoEnlace, campoCapacidad);
    }

    // El rango de la capacidad lo valida FuncionStreaming; la fecha y la edición, el servicio
    @Override
    protected Funcion programar(Pelicula pelicula, LocalDateTime fechaHora) throws CampoInvalidoException {
        String enlace = LectorCampos.texto(campoEnlace, "El enlace es obligatorio.");
        int capacidad = LectorCampos.entero(campoCapacidad,
                "La capacidad es obligatoria.", "La capacidad debe ser un número entero.");
        return funcionServicio.programarFuncionStreaming(pelicula, enlace, capacidad, fechaHora);
    }
}

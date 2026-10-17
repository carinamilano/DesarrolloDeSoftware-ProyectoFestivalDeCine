package ui.comun;

import modelo.personas.Persona;

import javax.swing.JTextField;
import java.awt.Window;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

// Alta de una persona (nombre, email y nacionalidad). Lo usan directores, actores,
// espectadores y jurados; antes había cuatro copias del mismo diálogo
public final class DialogoPersona {

    @FunctionalInterface
    public interface Registro<T extends Persona> {
        T registrar(String nombre, String email, String nacionalidad);
    }

    private DialogoPersona() {
    }

    // Devuelve la persona registrada, o vacío si se canceló
    public static <T extends Persona> Optional<T> mostrar(Window padre, String titulo, Registro<T> registro) {
        FormularioDialogo formulario = new FormularioDialogo(padre, titulo, 400, 250);
        JTextField campoNombre = formulario.agregarCampo("Nombre:", new JTextField());
        JTextField campoEmail = formulario.agregarCampo("Email:", new JTextField());
        JTextField campoNacionalidad = formulario.agregarCampo("Nacionalidad:", new JTextField());
        formulario.encadenarEnter(campoNombre, campoEmail, campoNacionalidad);

        AtomicReference<T> registrada = new AtomicReference<>();
        formulario.alAceptar(() -> {
            String nombre = LectorCampos.texto(campoNombre, "El nombre es obligatorio.");
            String email = LectorCampos.texto(campoEmail, "El email es obligatorio.");
            String nacionalidad = LectorCampos.texto(campoNacionalidad, "La nacionalidad es obligatoria.");
            registrada.set(registro.registrar(nombre, email, nacionalidad));
            formulario.cerrar();
        });
        formulario.mostrar();
        return Optional.ofNullable(registrada.get());
    }
}

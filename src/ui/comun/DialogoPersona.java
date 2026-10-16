package ui.comun;

import javax.swing.JTextField;
import java.awt.Window;
import java.util.concurrent.atomic.AtomicReference;

// Alta de una persona (nombre, email y nacionalidad). Lo usan directores, actores,
// espectadores y jurados; antes había cuatro copias del mismo diálogo
public final class DialogoPersona {

    @FunctionalInterface
    public interface Registro {
        void registrar(String nombre, String email, String nacionalidad);
    }

    private DialogoPersona() {
    }

    // Devuelve el nombre de la persona registrada, o null si se canceló
    public static String mostrar(Window padre, String titulo, Registro registro) {
        FormularioDialogo formulario = new FormularioDialogo(padre, titulo, 400, 250);
        JTextField campoNombre = formulario.agregarCampo("Nombre:", new JTextField());
        JTextField campoEmail = formulario.agregarCampo("Email:", new JTextField());
        JTextField campoNacionalidad = formulario.agregarCampo("Nacionalidad:", new JTextField());
        formulario.encadenarEnter(campoNombre, campoEmail, campoNacionalidad);

        AtomicReference<String> registrado = new AtomicReference<>();
        formulario.alAceptar(() -> {
            String nombre = LectorCampos.texto(campoNombre, "El nombre es obligatorio.");
            String email = LectorCampos.texto(campoEmail, "El email es obligatorio.");
            String nacionalidad = LectorCampos.texto(campoNacionalidad, "La nacionalidad es obligatoria.");
            registro.registrar(nombre, email, nacionalidad);
            registrado.set(nombre);
            formulario.cerrar();
        });
        formulario.mostrar();
        return registrado.get();
    }
}

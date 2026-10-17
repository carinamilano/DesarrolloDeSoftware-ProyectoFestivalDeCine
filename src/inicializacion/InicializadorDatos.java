package inicializacion;

import servicios.FestivalServicio;
import servicios.SistemaFestival;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

// Ejecuta en orden los inicializadores de cada tipo de dato.
// Para agregar otra precarga se suma un Inicializador a la lista, sin tocar los demás (OCP)
public class InicializadorDatos implements Inicializador {
    private final FestivalServicio festivalServicio;
    private final List<Inicializador> pasos;

    public InicializadorDatos(SistemaFestival sistema, Clock reloj) {
        this.festivalServicio = sistema.getFestivalServicio();
        CalendarioDemo calendario = new CalendarioDemo(LocalDate.now(reloj), sistema.getFuncionServicio());
        PersonasDemo personas = new PersonasDemo(sistema.getPersonaServicio());
        this.pasos = List.of(
                new InicializadorEdiciones(sistema.getFestivalServicio(), calendario),
                new InicializadorSecciones(sistema.getFestivalServicio(), sistema.getPeliculaServicio(), calendario),
                new InicializadorReparto(sistema.getPeliculaServicio(), personas),
                new InicializadorFunciones(sistema, calendario),
                new InicializadorEntradas(sistema, calendario, personas),
                new InicializadorAbonos(sistema, calendario, personas),
                new InicializadorVotos(sistema, personas));
    }

    // Ahora que se guardan todos los datos, la demostración solo se carga la primera vez
    // (cuando no hay ediciones guardadas); después se usa lo que quedó en los archivos
    @Override
    public void inicializar() {
        if (!festivalServicio.getEdiciones().isEmpty()) return;
        pasos.forEach(Inicializador::inicializar);
    }
}

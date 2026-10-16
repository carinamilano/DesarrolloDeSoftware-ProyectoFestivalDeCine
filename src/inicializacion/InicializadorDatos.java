package inicializacion;

import servicios.SistemaFestival;

import java.time.LocalDate;
import java.util.List;

// Ejecuta en orden los inicializadores de cada tipo de dato.
// Para agregar otra precarga se suma un Inicializador a la lista, sin tocar los demás (OCP)
public class InicializadorDatos implements Inicializador {
    private final List<Inicializador> pasos;

    public InicializadorDatos(SistemaFestival sistema) {
        CalendarioDemo calendario = new CalendarioDemo(LocalDate.now(), sistema.getFuncionServicio());
        this.pasos = List.of(
                new InicializadorEdiciones(sistema.getFestivalServicio()),
                new InicializadorSecciones(sistema.getFestivalServicio(), sistema.getPeliculaServicio()),
                new InicializadorReparto(sistema.getPeliculaServicio()),
                new InicializadorFunciones(sistema, calendario),
                new InicializadorEntradas(sistema, calendario),
                new InicializadorAbonos(sistema, calendario),
                new InicializadorVotos(sistema.getEvaluacionServicio(), sistema.getVentaServicio()));
    }

    @Override
    public void inicializar() {
        pasos.forEach(Inicializador::inicializar);
    }
}

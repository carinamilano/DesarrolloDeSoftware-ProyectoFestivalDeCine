package inicializacion;

import modelo.funciones.Butaca;
import modelo.funciones.Funcion;
import modelo.precios.TipoTarifa;
import servicios.FuncionServicio;
import servicios.PersonaServicio;
import servicios.SistemaFestival;
import servicios.VentaServicio;

import java.util.Optional;

// Algunas entradas vendidas para demostrar la ocupación
class InicializadorEntradas implements Inicializador {
    private final VentaServicio ventaServicio;
    private final FuncionServicio funcionServicio;
    private final PersonaServicio personaServicio;
    private final CalendarioDemo calendario;
    private final PersonasDemo personas;

    InicializadorEntradas(SistemaFestival sistema, CalendarioDemo calendario, PersonasDemo personas) {
        this.ventaServicio = sistema.getVentaServicio();
        this.funcionServicio = sistema.getFuncionServicio();
        this.personaServicio = sistema.getPersonaServicio();
        this.calendario = calendario;
        this.personas = personas;
    }

    @Override
    public void inicializar() {
        if (!ventaServicio.getEntradas().isEmpty()) return;
        if (funcionServicio.getFunciones().isEmpty()) return;
        if (personaServicio.getEspectadores().isEmpty()) return;

        venderPresencial("The Irishman", 7, 15, 1,
                "Ana Lopez", "Carlos Garcia", "Maria Perez", "Juan Rodriguez", "Laura Martinez");
        venderPresencial("Roma", 8, 16, 1, "Diego Fernandez", "Sofia Gonzalez", "Pablo Sanchez");
        venderPresencial("Tar", 9, 17, 1, "Valentina Torres", "Matias Ramirez", "Ana Lopez", "Carlos Garcia");
        venderPresencial("Oppenheimer", 10, 14, 1,
                "Maria Perez", "Juan Rodriguez", "Laura Martinez", "Diego Fernandez", "Sofia Gonzalez", "Pablo Sanchez");
        venderPresencial("Parasite", 11, 16, 1, "Valentina Torres", "Matias Ramirez", "Ana Lopez");
        venderPresencial("Parasite", 11, 20, 5, "Carlos Garcia", "Maria Perez", "Juan Rodriguez", "Laura Martinez");
        venderPresencial("Volver", 12, 18, 1, "Diego Fernandez", "Sofia Gonzalez");
        venderPresencial("The Grand Budapest Hotel", 13, 17, 1, "Pablo Sanchez", "Valentina Torres", "Matias Ramirez");

        venderStreaming("Barbie", 14, 18, "Ana Lopez", "Carlos Garcia", "Maria Perez", "Juan Rodriguez", "Laura Martinez");
        venderStreaming("Dune", 15, 17, "Diego Fernandez", "Sofia Gonzalez", "Pablo Sanchez");
        venderStreaming("La La Land", 16, 18, "Valentina Torres", "Matias Ramirez", "Ana Lopez", "Carlos Garcia");
    }

    // Vende butacas consecutivas desde primeraButaca, una por espectador
    private void venderPresencial(String titulo, int diasDesdeBase, int hora, int primeraButaca, String... espectadores) {
        Funcion funcion = calendario.funcion(titulo, diasDesdeBase, hora);
        int butaca = primeraButaca;
        for (String espectador : espectadores) {
            ventaServicio.venderEntrada(personas.espectador(espectador), funcion, TipoTarifa.GENERAL,
                    Optional.of(new Butaca(butaca++)));
        }
    }

    private void venderStreaming(String titulo, int diasDesdeBase, int hora, String... espectadores) {
        Funcion funcion = calendario.funcion(titulo, diasDesdeBase, hora);
        for (String espectador : espectadores) {
            ventaServicio.venderEntrada(personas.espectador(espectador), funcion, TipoTarifa.GENERAL, Optional.empty());
        }
    }
}

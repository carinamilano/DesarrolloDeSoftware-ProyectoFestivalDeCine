package inicializacion;

import modelo.Abono;
import modelo.TipoTarifa;
import servicios.AbonoServicio;
import servicios.FuncionServicio;
import servicios.PersonaServicio;
import servicios.SistemaFestival;

import java.util.List;

// 3 abonos (uno de cada tipo) con sus reservas
class InicializadorAbonos implements Inicializador {
    private static final int BUTACA_RESERVADA = 10;

    private final AbonoServicio abonoServicio;
    private final FuncionServicio funcionServicio;
    private final PersonaServicio personaServicio;
    private final CalendarioDemo calendario;

    InicializadorAbonos(SistemaFestival sistema, CalendarioDemo calendario) {
        this.abonoServicio = sistema.getAbonoServicio();
        this.funcionServicio = sistema.getFuncionServicio();
        this.personaServicio = sistema.getPersonaServicio();
        this.calendario = calendario;
    }

    @Override
    public void inicializar() {
        if (!abonoServicio.getAbonos().isEmpty()) return;
        if (funcionServicio.getFunciones().isEmpty()) return;
        if (personaServicio.getEspectadores().isEmpty()) return;

        // Abono Full con tarifa de estudiante
        Abono full = abonoServicio.comprarAbonoFull("Ana Lopez", TipoTarifa.ESTUDIANTE);
        reservarPresencial(full, "Roma", 8, 21);
        reservarPresencial(full, "Oppenheimer", 10, 19);

        // Abono 3 días
        Abono tresDias = abonoServicio.comprarAbonoTresDias("Carlos Garcia", TipoTarifa.GENERAL,
                List.of(calendario.dia(7), calendario.dia(8), calendario.dia(9)));
        reservarPresencial(tresDias, "The Irishman", 7, 20);
        reservarPresencial(tresDias, "Roma", 8, 16);
        reservarPresencial(tresDias, "Tar", 9, 21);

        // Abono Streaming con tarifa de jubilado
        Abono streaming = abonoServicio.comprarAbonoStreaming("Valentina Torres", TipoTarifa.JUBILADO);
        abonoServicio.reservar(streaming.getCodigo(), calendario.funcion("Dune", 15, 21));
    }

    private void reservarPresencial(Abono abono, String titulo, int diasDesdeHoy, int hora) {
        abonoServicio.reservar(abono.getCodigo(), calendario.funcion(titulo, diasDesdeHoy, hora), BUTACA_RESERVADA);
    }
}

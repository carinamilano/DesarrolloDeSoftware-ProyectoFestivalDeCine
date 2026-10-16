package inicializacion;

import modelo.Funcion;
import servicios.FuncionServicio;

import java.time.LocalDate;
import java.time.LocalDateTime;

// Las funciones de demostración se programan a partir de hoy: "dentro de N días, a la hora H"
class CalendarioDemo {
    private final LocalDate fechaBase;
    private final FuncionServicio funciones;

    CalendarioDemo(LocalDate fechaBase, FuncionServicio funciones) {
        this.fechaBase = fechaBase;
        this.funciones = funciones;
    }

    LocalDate dia(int diasDesdeHoy) {
        return fechaBase.plusDays(diasDesdeHoy);
    }

    LocalDateTime momento(int diasDesdeHoy, int hora) {
        return dia(diasDesdeHoy).atTime(hora, 0);
    }

    // Busca con película, día y hora: dos funciones de la misma película el mismo día ya no se confunden
    Funcion funcion(String tituloPelicula, int diasDesdeHoy, int hora) {
        return funciones.buscarFuncion(tituloPelicula, momento(diasDesdeHoy, hora));
    }
}

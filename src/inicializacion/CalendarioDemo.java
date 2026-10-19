package inicializacion;

import modelo.funciones.Funcion;
import servicios.FuncionServicio;

import java.time.LocalDate;
import java.time.LocalDateTime;

// Las fechas de la demostración se calculan a partir de hoy: "dentro de N días, a la hora H".
// Como ahora las funciones tienen que caer dentro de su edición, la edición actual de la demo
// también es relativa a hoy: va desde la fecha base hasta 30 días después, sin pasarse de año
// (Edicion exige que sus dos fechas sean de su año)
class CalendarioDemo {
    private static final int DURACION_EDICION = 30;
    private static final int ULTIMO_DIA_CON_FUNCIONES = 16;

    private final LocalDate fechaBase;
    private final FuncionServicio funciones;

    CalendarioDemo(LocalDate hoy, FuncionServicio funciones) {
        // Si las funciones de la demo cruzaran al año siguiente, la edición arranca el 1 de enero
        boolean entraEnElAnio = hoy.plusDays(ULTIMO_DIA_CON_FUNCIONES).getYear() == hoy.getYear();
        this.fechaBase = entraEnElAnio ? hoy : LocalDate.of(hoy.getYear() + 1, 1, 1);
        this.funciones = funciones;
    }

    int anioEdicionActual() {
        return fechaBase.getYear();
    }

    LocalDate inicioEdicionActual() {
        return fechaBase;
    }

    LocalDate finEdicionActual() {
        LocalDate fin = fechaBase.plusDays(DURACION_EDICION);
        return fin.getYear() == fechaBase.getYear() ? fin : LocalDate.of(fechaBase.getYear(), 12, 31);
    }

    LocalDate dia(int diasDesdeBase) {
        return fechaBase.plusDays(diasDesdeBase);
    }

    LocalDateTime momento(int diasDesdeBase, int hora) {
        return dia(diasDesdeBase).atTime(hora, 0);
    }

    // Busca con película, día y hora: dos funciones de la misma película el mismo día no se confunden
    Funcion funcion(String tituloPelicula, int diasDesdeBase, int hora) {
        return funciones.buscarFuncion(tituloPelicula, momento(diasDesdeBase, hora));
    }
}

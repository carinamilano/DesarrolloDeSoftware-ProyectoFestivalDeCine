package persistencia;

import excepciones.DatoInvalidoException;
import modelo.Abono;
import modelo.Entrada;
import modelo.EntradaPresencial;
import modelo.EntradaStreaming;
import modelo.Espectador;
import modelo.FuncionPresencial;
import modelo.FuncionStreaming;
import modelo.Pelicula;
import modelo.TipoAbono;
import modelo.TipoTarifa;
import servicios.AbonoServicio;
import servicios.FestivalServicio;
import servicios.FuncionServicio;
import servicios.PeliculaServicio;
import servicios.PersonaServicio;
import servicios.VentaServicio;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// CORRECCION S17: funciones, abonos y entradas (antes en Persistencia).
class PersistenciaVentas extends ArchivosDeDominio {

    private static final String FUNCIONES = "funciones.txt";
    private static final String ABONOS = "abonos.txt";
    private static final String ENTRADAS = "entradas.txt";
    private static final String PRESENCIAL = "PRESENCIAL";
    private static final String STREAMING = "STREAMING";
    private final FuncionServicio funcionServicio;
    private final VentaServicio ventaServicio;
    private final AbonoServicio abonoServicio;
    private final PersonaServicio personaServicio;
    private final PeliculaServicio peliculaServicio;
    private final FestivalServicio festivalServicio;

    PersistenciaVentas(Path carpeta, List<String> erroresDeCarga, FuncionServicio funcionServicio,
                       VentaServicio ventaServicio, AbonoServicio abonoServicio, PersonaServicio personaServicio,
                       PeliculaServicio peliculaServicio, FestivalServicio festivalServicio) {
        super(carpeta, erroresDeCarga);
        this.funcionServicio = funcionServicio;
        this.ventaServicio = ventaServicio;
        this.abonoServicio = abonoServicio;
        this.personaServicio = personaServicio;
        this.peliculaServicio = peliculaServicio;
        this.festivalServicio = festivalServicio;
    }

    @Override
    void cargar() {
        cargarCada(FUNCIONES, r -> {
            String codigo = r.texto(0);
            String tipo = r.texto(1).toUpperCase();
            Pelicula pelicula = peliculaServicio.buscarPelicula(r.texto(2));
            if (tipo.equals(PRESENCIAL)) {
                funcionServicio.restaurarFuncionPresencial(codigo, pelicula, festivalServicio.buscarSala(r.texto(5)),
                        r.fecha(3), r.hora(4));
            } else if (tipo.equals(STREAMING)) {
                funcionServicio.restaurarFuncionStreaming(codigo, pelicula, r.texto(5), r.entero(6), r.fecha(3), r.hora(4));
            } else {
                throw new DatoInvalidoException("tipo de función desconocido: " + tipo + ".");
            }
        });
        cargarCada(ABONOS, r -> {
            Espectador espectador = personaServicio.buscarEspectador(r.texto(2));
            abonoServicio.restaurarAbono(r.texto(0), r.enumerado(1, TipoAbono.class), espectador,
                    r.enumerado(3, TipoTarifa.class), r.decimal(4), r.fecha(5), r.fechas(6));
        });
        cargarCada(ENTRADAS, r -> {
            String codigo = r.texto(0);
            String codigoFuncion = r.texto(1);
            Espectador espectador = personaServicio.buscarEspectador(r.texto(2));
            double precio = r.decimal(3);
            Abono abono = r.textoOpcional(4).isEmpty() ? null : abonoServicio.buscarAbono(r.texto(4));
            if (!r.textoOpcional(5).isEmpty()) {
                FuncionPresencial funcion = funcionServicio.buscarFuncionPresencial(codigoFuncion);
                ventaServicio.restaurarEntradaPresencial(codigo, funcion, espectador, r.entero(5), precio, abono);
            } else {
                FuncionStreaming funcion = funcionServicio.buscarFuncionStreaming(codigoFuncion);
                ventaServicio.restaurarEntradaStreaming(codigo, funcion, espectador, precio, abono);
            }
        });
    }

    @Override
    void agregarLineas(Map<String, List<String>> archivos) {
        archivos.put(FUNCIONES, lineasFunciones());
        archivos.put(ABONOS, lineasAbonos());
        archivos.put(ENTRADAS, lineasEntradas());
    }

    private List<String> lineasFunciones() {
        List<String> lineas = new ArrayList<>();
        for (FuncionPresencial f : funcionServicio.getFuncionesPresenciales()) {
            lineas.add(FormatoLinea.unir(f.getCodigo(), PRESENCIAL, f.getPelicula().getTitulo(),
                    f.getFecha(), f.getHorario(), f.getSala().getNombre()));
        }
        for (FuncionStreaming f : funcionServicio.getFuncionesStreaming()) {
            lineas.add(FormatoLinea.unir(f.getCodigo(), STREAMING, f.getPelicula().getTitulo(),
                    f.getFecha(), f.getHorario(), f.getEnlace(), f.getCapacidadVirtual()));
        }
        return lineas;
    }

    private List<String> lineasAbonos() {
        List<String> lineas = new ArrayList<>();
        for (Abono a : abonoServicio.getAbonos()) {
            lineas.add(FormatoLinea.unir(a.getCodigo(), a.getTipo().name(), a.getEspectador().getEmail(),
                    a.getTarifa().name(), a.getPrecio(), a.getFechaCompra(), unirFechas(a.getDiasElegidos())));
        }
        return lineas;
    }

    private List<String> lineasEntradas() {
        List<String> lineas = new ArrayList<>();
        for (EntradaPresencial e : ventaServicio.getEntradasPresenciales()) {
            lineas.add(FormatoLinea.unir(e.getCodigo(), e.getFuncion().getCodigo(), e.getEspectador().getEmail(),
                    e.getPrecio(), codigoAbono(e), e.getButaca()));
        }
        for (EntradaStreaming e : ventaServicio.getEntradasStreaming()) {
            lineas.add(FormatoLinea.unir(e.getCodigo(), e.getFuncion().getCodigo(), e.getEspectador().getEmail(),
                    e.getPrecio(), codigoAbono(e), ""));
        }
        return lineas;
    }

    private String codigoAbono(Entrada e) {
        return e.esReservaDeAbono() ? e.getAbono().getCodigo() : "";
    }

    private String unirFechas(List<LocalDate> fechas) {
        StringBuilder texto = new StringBuilder();
        for (LocalDate f : fechas) {
            if (texto.length() > 0) texto.append(",");
            texto.append(f);
        }
        return texto.toString();
    }
}

package persistencia;

import excepciones.EntidadDuplicadaException;
import excepciones.EntidadNoEncontradaException;
import excepciones.FestivalException;
import excepciones.PersistenciaException;
import excepciones.ReglaNegocioException;
import modelo.abonos.Abono;
import modelo.abonos.AbonoFull;
import modelo.abonos.AbonoStreaming;
import modelo.abonos.AbonoTresDias;
import modelo.abonos.TipoAbono;
import modelo.festival.Edicion;
import modelo.festival.Premio;
import modelo.festival.Sala;
import modelo.festival.Seccion;
import modelo.festival.TipoSeccion;
import modelo.funciones.Butaca;
import modelo.funciones.Funcion;
import modelo.funciones.FuncionPresencial;
import modelo.funciones.FuncionStreaming;
import modelo.funciones.SolicitudEntrada;
import modelo.peliculas.Genero;
import modelo.peliculas.Pelicula;
import modelo.personas.Actor;
import modelo.personas.Director;
import modelo.personas.Espectador;
import modelo.personas.Jurado;
import modelo.personas.Persona;
import modelo.precios.Dinero;
import modelo.precios.TipoTarifa;
import repositorio.Catalogo;
import repositorio.Repositorio;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static persistencia.ArchivoDatos.*;

// Lee los archivos en orden (cada tipo de dato después de aquellos a los que hace referencia).
// Una línea inválida se anota como error y se sigue con la siguiente: antes la primera línea mala
// cortaba la carga y todo lo que venía después quedaba sin cargar
final class CargaDeArchivos {
    private final Catalogo catalogo;
    private final Clock reloj;
    private final List<String> errores = new ArrayList<>();

    @FunctionalInterface
    private interface Conversion {
        void aplicar(Registro registro);
    }

    @FunctionalInterface
    private interface FabricaPersona<T extends Persona> {
        T crear(String nombre, String email, String nacionalidad);
    }

    CargaDeArchivos(Catalogo catalogo, Clock reloj) {
        this.catalogo = catalogo;
        this.reloj = reloj;
    }

    // Devuelve los errores encontrados (vacío si todo se cargó bien)
    List<String> cargar() {
        leer(SALAS, this::cargarSala);
        leer(DIRECTORES, registro -> cargarPersona(registro, catalogo.getDirectores(), Director::new));
        leer(ACTORES, registro -> cargarPersona(registro, catalogo.getActores(), Actor::new));
        leer(JURADOS, registro -> cargarPersona(registro, catalogo.getJurados(), Jurado::new));
        leer(ESPECTADORES, registro -> cargarPersona(registro, catalogo.getEspectadores(), Espectador::new));
        leer(PELICULAS, this::cargarPelicula);
        leer(REPARTO, this::cargarReparto);
        leer(EVALUACIONES, this::cargarEvaluacion);
        leer(EDICIONES, this::cargarEdicion);
        leer(SECCIONES, this::cargarSeccion);
        leer(PELICULAS_POR_SECCION, this::cargarPeliculaEnSeccion);
        leer(FUNCIONES, this::cargarFuncion);
        leer(ABONOS, this::cargarAbono);
        leer(ENTRADAS, this::cargarEntrada);
        leer(VOTOS, this::cargarVoto);
        return List.copyOf(errores);
    }

    private void leer(ArchivoDatos archivo, Conversion conversion) {
        List<Registro> registros;
        try {
            registros = archivo.archivo().leerRegistros();
        } catch (PersistenciaException ex) {
            errores.add(ex.getMessage());
            return;
        }
        for (Registro registro : registros) {
            try {
                conversion.aplicar(registro);
            } catch (FestivalException | IllegalArgumentException | DateTimeException ex) {
                // Datos inválidos: números o fechas mal escritos, campos que faltan o reglas del modelo
                errores.add(registro.describir() + " → " + ex.getMessage());
            }
        }
    }

    // ========== SALAS Y PERSONAS ==========

    private void cargarSala(Registro registro) {
        Sala sala = new Sala(registro.texto(0), registro.entero(1));
        agregarSinRepetir(catalogo.getSalas(), sala, "la " + sala.getNombre());
    }

    private <T extends Persona> void cargarPersona(Registro registro, Repositorio<T> destino, FabricaPersona<T> fabrica) {
        T persona = fabrica.crear(registro.texto(0), registro.texto(1), registro.texto(2));
        agregarSinRepetir(destino, persona, "el email " + persona.getEmail());
    }

    private static <T> void agregarSinRepetir(Repositorio<T> repositorio, T elemento, String descripcion) {
        if (repositorio.existe(elemento::equals)) {
            throw new EntidadDuplicadaException("Está repetido " + descripcion + ".");
        }
        repositorio.agregar(elemento);
    }

    // ========== PELÍCULAS ==========

    private void cargarPelicula(Registro registro) {
        String titulo = registro.texto(0);
        if (catalogo.getPeliculas().existe(pelicula -> pelicula.seTitula(titulo))) {
            throw new EntidadDuplicadaException("Está repetida la película " + titulo + ".");
        }
        Director director = persona(catalogo.getDirectores(), registro.texto(3), "Director");
        catalogo.getPeliculas().agregar(new Pelicula(titulo, Genero.desde(registro.texto(1)), registro.entero(2), director));
    }

    private void cargarReparto(Registro registro) {
        pelicula(registro.texto(0)).agregarActor(persona(catalogo.getActores(), registro.texto(1), "Actor"));
    }

    private void cargarEvaluacion(Registro registro) {
        Jurado jurado = persona(catalogo.getJurados(), registro.texto(0), "Jurado");
        pelicula(registro.texto(1)).recibirEvaluacion(jurado, registro.decimal(2));
    }

    // ========== EDICIONES Y SECCIONES ==========

    private void cargarEdicion(Registro registro) {
        catalogo.getFestival().agregarEdicion(new Edicion(registro.entero(0), registro.texto(1),
                registro.fecha(2), registro.fecha(3)));
    }

    private void cargarSeccion(Registro registro) {
        Premio premio = new Premio(registro.texto(2), registro.opcional(3).orElse(""));
        edicion(registro.entero(0)).agregarSeccion(registro.enumerado(1, TipoSeccion.class), premio);
    }

    // Una película compite en una sola edición
    private void cargarPeliculaEnSeccion(Registro registro) {
        Edicion edicion = edicion(registro.entero(0));
        TipoSeccion tipo = registro.enumerado(1, TipoSeccion.class);
        Pelicula pelicula = pelicula(registro.texto(2));
        Optional<Edicion> otra = catalogo.getFestival().buscarEdicionDe(pelicula).filter(e -> !e.equals(edicion));
        if (otra.isPresent()) {
            throw new ReglaNegocioException(pelicula.getTitulo() + " ya compite en la edición " + otra.get().getAnio() + ".");
        }
        Seccion seccion = edicion.buscarSeccion(tipo).orElseThrow(() -> new EntidadNoEncontradaException("Seccion", "tipo: " + tipo));
        seccion.agregarPelicula(pelicula);
    }

    // ========== FUNCIONES, ABONOS, ENTRADAS Y VOTOS ==========

    // Se restauran tal como se guardaron: una función que ya pasó se carga igual (no se vuelve a "programar")
    private void cargarFuncion(Registro registro) {
        String codigo = registro.texto(0);
        if (buscarFuncion(codigo).isPresent()) {
            throw new EntidadDuplicadaException("Está repetida la función " + codigo + ".");
        }
        Pelicula pelicula = pelicula(registro.texto(2));
        LocalDateTime fechaHora = registro.fechaHora(3);
        String modalidad = registro.texto(1).toUpperCase();
        if (modalidad.equals(PRESENCIAL)) {
            FuncionPresencial funcion = new FuncionPresencial(codigo, pelicula, sala(registro.texto(4)), fechaHora, reloj);
            catalogo.getFuncionesPresenciales().agregar(funcion);
            pelicula.agregarFuncion(funcion);
        } else if (modalidad.equals(STREAMING)) {
            FuncionStreaming funcion = new FuncionStreaming(codigo, pelicula, registro.texto(4), registro.entero(5), fechaHora, reloj);
            catalogo.getFuncionesStreaming().agregar(funcion);
            pelicula.agregarFuncion(funcion);
        } else {
            throw new IllegalArgumentException("modalidad desconocida: " + modalidad);
        }
        continuarNumeracion(codigo);
    }

    private void cargarAbono(Registro registro) {
        String codigo = registro.texto(0);
        if (catalogo.getAbonos().existe(abono -> abono.getCodigo().equalsIgnoreCase(codigo))) {
            throw new EntidadDuplicadaException("Está repetido el abono " + codigo + ".");
        }
        TipoAbono tipo = registro.enumerado(1, TipoAbono.class);
        Espectador espectador = persona(catalogo.getEspectadores(), registro.texto(2), "Espectador");
        TipoTarifa tarifa = registro.enumerado(3, TipoTarifa.class);
        Dinero precio = registro.dinero(4);
        Abono abono = switch (tipo) {
            case TRES_DIAS -> AbonoTresDias.restaurar(codigo, espectador, tarifa, precio, registro.fechas(5), reloj);
            case FULL -> AbonoFull.emitir(codigo, espectador, tarifa, precio);
            case STREAMING -> AbonoStreaming.emitir(codigo, espectador, tarifa, precio);
        };
        catalogo.getAbonos().agregar(abono);
        continuarNumeracion(codigo);
    }

    // La entrada vuelve a ocupar su butaca (o su lugar virtual) aunque la función ya haya pasado
    private void cargarEntrada(Registro registro) {
        String codigo = registro.texto(0);
        if (catalogo.getEntradas().existe(entrada -> entrada.getCodigo().equalsIgnoreCase(codigo))) {
            throw new EntidadDuplicadaException("Está repetida la entrada " + codigo + ".");
        }
        Funcion funcion = buscarFuncion(registro.texto(1))
                .orElseThrow(() -> new EntidadNoEncontradaException("Funcion", "codigo: " + registro.texto(1)));
        Espectador espectador = persona(catalogo.getEspectadores(), registro.texto(2), "Espectador");
        Optional<Butaca> butaca = registro.opcional(4).map(Integer::parseInt).map(Butaca::new);
        Optional<Abono> abono = registro.opcional(5).map(this::abono);
        if (abono.isPresent() && !abono.get().getEspectador().equals(espectador)) {
            throw new ReglaNegocioException("La reserva no es del titular del abono " + abono.get().getCodigo() + ".");
        }
        SolicitudEntrada solicitud = new SolicitudEntrada(espectador, registro.dinero(3), abono, butaca);
        catalogo.getEntradas().agregar(funcion.restaurarEntrada(codigo, solicitud));
        continuarNumeracion(codigo);
    }

    private void cargarVoto(Registro registro) {
        Espectador espectador = persona(catalogo.getEspectadores(), registro.texto(0), "Espectador");
        pelicula(registro.texto(1)).recibirVoto(espectador, registro.entero(2));
    }

    // ========== BÚSQUEDAS ==========

    private <T extends Persona> T persona(Repositorio<T> repositorio, String email, String entidad) {
        return repositorio.obtener(persona -> persona.tieneEmail(email), entidad, "email: " + email);
    }

    private Pelicula pelicula(String titulo) {
        return catalogo.getPeliculas().obtener(pelicula -> pelicula.seTitula(titulo), "Pelicula", "titulo: " + titulo);
    }

    private Sala sala(String nombre) {
        return catalogo.getSalas().obtener(sala -> sala.seLlama(nombre), "Sala", "nombre: " + nombre);
    }

    private Edicion edicion(int anio) {
        return catalogo.getFestival().buscarEdicion(anio)
                .orElseThrow(() -> new EntidadNoEncontradaException("Edicion", "anio: " + anio));
    }

    private Abono abono(String codigo) {
        return catalogo.getAbonos().obtener(abono -> abono.getCodigo().equalsIgnoreCase(codigo), "Abono", "codigo: " + codigo);
    }

    private Optional<Funcion> buscarFuncion(String codigo) {
        return Stream.<Funcion>concat(catalogo.getFuncionesPresenciales().listar().stream(),
                        catalogo.getFuncionesStreaming().listar().stream())
                .filter(funcion -> funcion.tieneCodigo(codigo))
                .findFirst();
    }

    // Los códigos nuevos tienen que seguir después del último cargado (F-12 -> el próximo es 13)
    private void continuarNumeracion(String codigo) {
        String numero = codigo.substring(codigo.lastIndexOf('-') + 1);
        if (!numero.isEmpty() && numero.chars().allMatch(Character::isDigit)) {
            catalogo.getNumerador().continuarDespuesDe(Integer.parseInt(numero));
        }
    }
}

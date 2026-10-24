package persistencia;

import modelo.*;
import excepciones.PersistenciaException;

import java.io.*;
import java.util.List;

public class Persistencia {

    private static final String ARCHIVO_SALAS = "salas.txt";
    private static final String ARCHIVO_DIRECTORES = "directores.txt";
    private static final String ARCHIVO_ACTORES = "actores.txt";
    private static final String ARCHIVO_JURADOS = "jurados.txt";
    private static final String ARCHIVO_ESPECTADORES = "espectadores.txt";
    private static final String ARCHIVO_PELICULAS = "peliculas.txt";
    private static final String ARCHIVO_EVALUACIONES = "evaluaciones.txt";

    // ========== GUARDAR ==========

    public void guardarSalas(List<Sala> salas) throws PersistenciaException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_SALAS))) {
            for (Sala s : salas) {
                bw.write(s.getNombre() + "," + s.getCantidadButacas());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al guardar salas.");
        }
    }

    public void guardarDirectores(List<Director> directores) throws PersistenciaException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_DIRECTORES))) {
            for (Director d : directores) {
                bw.write(d.getNombre() + "," + d.getEmail() + "," + d.getNacionalidad());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al guardar directores.");
        }
    }

    public void guardarActores(List<Actor> actores) throws PersistenciaException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_ACTORES))) {
            for (Actor a : actores) {
                bw.write(a.getNombre() + "," + a.getEmail() + "," + a.getNacionalidad());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al guardar actores.");
        }
    }

    public void guardarJurados(List<Jurado> jurados) throws PersistenciaException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_JURADOS))) {
            for (Jurado j : jurados) {
                bw.write(j.getNombre() + "," + j.getEmail() + "," + j.getNacionalidad());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al guardar jurados.");
        }
    }

    public void guardarEspectadores(List<Espectador> espectadores) throws PersistenciaException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_ESPECTADORES))) {
            for (Espectador e : espectadores) {
                bw.write(e.getNombre() + "," + e.getEmail() + "," + e.getNacionalidad());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al guardar espectadores.");
        }
    }

    public void guardarPeliculas(List<Pelicula> peliculas) throws PersistenciaException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_PELICULAS))) {
            for (Pelicula p : peliculas) {
                bw.write(p.getTitulo() + "," + p.getGenero() + "," + p.getDuracion() + "," + p.getDirector().getNombre());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al guardar peliculas.");
        }
    }

    public void guardarEvaluaciones(List<Evaluacion> evaluaciones) throws PersistenciaException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_EVALUACIONES))) {
            for (Evaluacion e : evaluaciones) {
                bw.write(e.getJurado().getNombre() + "," + e.getPelicula().getTitulo() + "," + e.getPuntaje());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al guardar evaluaciones.");
        }
    }

    // ========== CARGAR ==========

    public void cargarSalas(List<Sala> salas) throws PersistenciaException {
        File archivo = new File(ARCHIVO_SALAS);
        if (!archivo.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");
                salas.add(new Sala(partes[0], Integer.parseInt(partes[1])));
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al cargar salas.");
        }
    }

    public void cargarDirectores(List<Director> directores) throws PersistenciaException {
        File archivo = new File(ARCHIVO_DIRECTORES);
        if (!archivo.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");
                directores.add(new Director(partes[0], partes[1], partes[2]));
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al cargar directores.");
        }
    }

    public void cargarActores(List<Actor> actores) throws PersistenciaException {
        File archivo = new File(ARCHIVO_ACTORES);
        if (!archivo.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");
                actores.add(new Actor(partes[0], partes[1], partes[2]));
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al cargar actores.");
        }
    }

    public void cargarJurados(List<Jurado> jurados) throws PersistenciaException {
        File archivo = new File(ARCHIVO_JURADOS);
        if (!archivo.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");
                jurados.add(new Jurado(partes[0], partes[1], partes[2]));
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al cargar jurados.");
        }
    }

    public void cargarEspectadores(List<Espectador> espectadores) throws PersistenciaException {
        File archivo = new File(ARCHIVO_ESPECTADORES);
        if (!archivo.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");
                espectadores.add(new Espectador(partes[0], partes[1], partes[2]));
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al cargar espectadores.");
        }
    }

    public void cargarPeliculas(List<Pelicula> peliculas, List<Director> directores) throws PersistenciaException {
        File archivo = new File(ARCHIVO_PELICULAS);
        if (!archivo.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");
                Director director = null;
                for (Director d : directores) {
                    if (d.getNombre().equalsIgnoreCase(partes[3])) {
                        director = d;
                        break;
                    }
                }
                if (director != null) {
                    peliculas.add(new Pelicula(partes[0], partes[1], Integer.parseInt(partes[2]), director));
                }
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al cargar peliculas.");
        }
    }

    public void cargarEvaluaciones(List<Evaluacion> evaluaciones, List<Pelicula> peliculas, List<Jurado> jurados) throws PersistenciaException {
        File archivo = new File(ARCHIVO_EVALUACIONES);
        if (!archivo.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");
                Jurado jurado = null;
                Pelicula pelicula = null;
                for (Jurado j : jurados) {
                    if (j.getNombre().equalsIgnoreCase(partes[0])) {
                        jurado = j;
                        break;
                    }
                }
                for (Pelicula p : peliculas) {
                    if (p.getTitulo().equalsIgnoreCase(partes[1])) {
                        pelicula = p;
                        break;
                    }
                }
                if (jurado != null && pelicula != null) {
                    Evaluacion e = new Evaluacion(Double.parseDouble(partes[2]), pelicula, jurado);
                    jurado.agregarEvaluacion(e);
                    pelicula.agregarEvaluacion(e);
                    evaluaciones.add(e);
                }
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al cargar evaluaciones.");
        }
    }
}
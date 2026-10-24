package modelo;

import java.time.LocalDate;
import java.time.LocalTime;

public class FuncionStreaming extends Funcion {
    private String enlace;
    private int capacidadVirtual;
    private int espectadoresConectados;

    public FuncionStreaming(LocalDate fecha, LocalTime horario, Pelicula pelicula,
                            String enlace, int capacidadVirtual) {
        super(fecha, horario, pelicula);
        this.enlace = enlace;
        this.capacidadVirtual = capacidadVirtual;
        this.espectadoresConectados = 0;
    }

    @Override
    public boolean verificarDisponibilidad() {
        return espectadoresConectados < capacidadVirtual;
    }

    public void agregarEspectador() {
        espectadoresConectados++;
    }

    public void quitarEspectador() {
        if (espectadoresConectados > 0) espectadoresConectados--;
    }

    @Override
    public double calcularOcupacion() {
        return (double) espectadoresConectados / capacidadVirtual * 100;
    }

    public String getEnlace() {
        return enlace;
    }
    public int getCapacidadVirtual() {
        return capacidadVirtual;
    }
    public int getEspectadoresConectados() {
        return espectadoresConectados;
    }

    @Override
    public String toString() {
        return "Streaming - " + super.toString() + " - Enlace: " + enlace;
    }
}
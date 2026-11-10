package Modelos;

import Enums.EstadoEvento;
import java.time.LocalDate;

public class Evento {
    private int id;
    private Salon salon;
    private String organizador;
    private LocalDate fecha;
    private int asistentes;
    private EstadoEvento estado;

    // Un evento nuevo siempre arranca CONFIRMADO
    public Evento(int id, Salon salon, String organizador, LocalDate fecha, int asistentes)
    {
        this.id = id;
        this.salon = salon;
        this.organizador = organizador;
        this.fecha = fecha;
        this.asistentes = asistentes;
        this.estado = EstadoEvento.CONFIRMADO;
    }

    // Constructor completo, para cuando se carga desde la BDD con su estado guardado
    public Evento(int id, Salon salon, String organizador, LocalDate fecha,
                  int asistentes, EstadoEvento estado)
    {
        this.id = id;
        this.salon = salon;
        this.organizador = organizador;
        this.fecha = fecha;
        this.asistentes = asistentes;
        this.estado = estado;
    }

    public void cancelar()
    {
        this.estado = EstadoEvento.CANCELADO;
    }

    public int getId() { return id; }
    public Salon getSalon() { return salon; }
    public String getOrganizador() { return organizador; }
    public LocalDate getFecha() { return fecha; }
    public int getAsistentes() { return asistentes; }
    public EstadoEvento getEstado() { return estado; }

    public void setEstado(EstadoEvento estado) { this.estado = estado; }

    @Override
    public String toString()
    {
        return "Evento #" + id + " | " + salon.getNombre() + " | Organiza: " + organizador +
                " | Fecha: " + fecha + " | Asistentes: " + asistentes + " | Estado: " + estado;
    }
}

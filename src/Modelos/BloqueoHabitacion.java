package Modelos;

import Enums.MotivoBloqueo;
import java.time.LocalDate;

public class BloqueoHabitacion {
    private int id;
    private Habitacion habitacion;
    private MotivoBloqueo motivo;
    private String detalle;
    private LocalDate desde;
    private LocalDate hasta;

    public BloqueoHabitacion(int id, Habitacion habitacion, MotivoBloqueo motivo,
                             String detalle, LocalDate desde, LocalDate hasta)
    {
        this.id = id;
        this.habitacion = habitacion;
        this.motivo = motivo;
        this.detalle = detalle;
        this.desde = desde;
        this.hasta = hasta;
    }

    // Activo si hoy está dentro del período del bloqueo (incluye los extremos)
    public boolean estaActivo()
    {
        LocalDate hoy = LocalDate.now();
        return !hoy.isBefore(desde) && !hoy.isAfter(hasta);
    }

    public int getId() { return id; }
    public Habitacion getHabitacion() { return habitacion; }
    public MotivoBloqueo getMotivo() { return motivo; }
    public String getDetalle() { return detalle; }
    public LocalDate getDesde() { return desde; }
    public LocalDate getHasta() { return hasta; }

    public void setHasta(LocalDate hasta) { this.hasta = hasta; }

    @Override
    public String toString()
    {
        return "Bloqueo #" + id + " | Habitación " + habitacion.getNumeroHabitacion() +
                " | Motivo: " + motivo + " (" + detalle + ")" +
                " | Desde " + desde + " hasta " + hasta;
    }
}

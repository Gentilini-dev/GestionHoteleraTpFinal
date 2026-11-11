package Modelos;

import Enums.EstadoReserva;

import java.time.LocalDate;
import java.util.Objects;

public class Reserva
{
    private int id;
    private Pasajero titular;
   private Habitacion habitacion;
    private LocalDate desde;
    private LocalDate hasta;
    private EstadoReserva estado;
    
    public Reserva(int id, Pasajero titular, Habitacion habitacion, LocalDate desde, LocalDate hasta, EstadoReserva estado)
    {
        this.id = id;
        this.titular = titular;
        this.habitacion = habitacion;
        this.desde = desde;
        this.hasta = hasta;
        this.estado = estado;
    }
    
    public int getId()
    {
        return id;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    public Habitacion getHabitacion()
    {
       return habitacion;
    }
   
   public void setHabitacion(Habitacion habitacion)
    {
       this.habitacion = habitacion;
    }
    
    public Pasajero getTitular()
    {
        return titular;
    }
    
    public void setTitular(Pasajero titular)
    {
        this.titular = titular;
    }
    
    public LocalDate getDesde()
    {
        return desde;
    }
    
    public void setDesde(LocalDate desde)
    {
        this.desde = desde;
    }
    
    public LocalDate getHasta()
    {
        return hasta;
    }
    
    public void setHasta(LocalDate hasta)
    {
        this.hasta = hasta;
    }
    
    public EstadoReserva getEstado()
    {
        return estado;
    }
    
    public void setEstado(EstadoReserva estado)
    {
        this.estado = estado;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof Reserva reserva))
        {
            return false;
        }
        return id == reserva.id;
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hashCode(id)* 22;
    }
    
    @Override
    public String toString()
    {
        return "Reserva: " +
                "id: " + id +
                ", titular: " + titular +
                ", habitacion: " + ///habitacion +
                ", desde: " + desde +
                ", hasta: " + hasta +
                ", estado: " + estado + ".";
    }
}

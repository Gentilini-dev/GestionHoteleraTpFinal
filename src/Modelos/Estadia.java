package Modelos;

import Enums.EstadoEstadia;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

// Lo que pasa entre el check-in y el check-out de una habitación
public class Estadia
{
    private int id;
    private Habitacion habitacion;
    private ArrayList<Pasajero> ocupantes;
    private LocalDate fechaIngreso;
    private LocalDate fechaEgreso;
    private EstadoEstadia estado;
    
    
    // Check-in: arranca hoy y sin fecha de egreso
    public Estadia(int id, Habitacion habitacion, ArrayList<Pasajero> ocupantes)
    {
        this.id = id;
        this.habitacion = habitacion;
        this.ocupantes = ocupantes;
        this.fechaIngreso = LocalDate.now();
        this.fechaEgreso = null;
        this.estado = EstadoEstadia.ACTIVA;
    }
    
    
    // Para leer de la base. Los ocupantes se agregan después con agregarOcupante()
    public Estadia(int id, Habitacion habitacion, LocalDate fechaIngreso, LocalDate fechaEgreso, EstadoEstadia estado)
    {
        this.id = id;
        this.habitacion = habitacion;
        this.ocupantes = new ArrayList<>();
        this.fechaIngreso = fechaIngreso;
        this.fechaEgreso = fechaEgreso;
        this.estado = estado;
    }
    
    public void agregarOcupante(Pasajero p)
    {
        ocupantes.add(p);
    }
    
    
    // Check-out
    public void finalizar()
    {
        this.fechaEgreso = LocalDate.now();
        this.estado = EstadoEstadia.FINALIZADA;
    }
    
    public int getId()
    {
        return id;
    }
    
    public Habitacion getHabitacion()
    {
        return habitacion;
    }
    
    public ArrayList<Pasajero> getOcupantes()
    {
        return ocupantes;
    }
    
    public LocalDate getFechaIngreso()
    {
        return fechaIngreso;
    }
    
    public LocalDate getFechaEgreso()
    {
        return fechaEgreso;
    }
    
    public EstadoEstadia getEstado()
    {
        return estado;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof Estadia estadia))
        {
            return false;
        }
        return id == estadia.id;
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hashCode(id)*22;
    }
    
    @Override
    public String toString()
    {
        String egreso = (fechaEgreso == null) ? "en curso" : fechaEgreso.toString();
        return "Estadía #" + id + " | Habitación " + habitacion.getNumeroHabitacion() +
                " | Ingreso: " + fechaIngreso + " | Egreso: " + egreso +
                " | Ocupantes: " + ocupantes.size() + " | Estado: " + estado;
    }
}

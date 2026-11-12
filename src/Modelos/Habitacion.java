package Modelos;

import Enums.TipoHabitacion;
import Interfaz.IIdentificable;

import java.util.Objects;

public class Habitacion implements IIdentificable
{
    private int numeroHabitacion;
    private int piso;
    private TipoHabitacion tipoHabitacion;
   
    
    public Habitacion(int numeroHabitacion, int piso, TipoHabitacion tipoHabitacion)
    {
        this.numeroHabitacion = numeroHabitacion;
        this.piso = piso;
        this.tipoHabitacion = tipoHabitacion;
       
    }

    public int getNumeroHabitacion()
    {
        return numeroHabitacion;
    }

    @Override
    public int getId()
    {
        return numeroHabitacion;
    }
    
    public TipoHabitacion getTipoHabitacion()
    {
        return tipoHabitacion;
    }
    
    public void setTipoHabitacion(TipoHabitacion tipoHabitacion)
    {
        this.tipoHabitacion = tipoHabitacion;
    }
    
    public int getPiso()
    {
        return piso;
    }
    
    public void setPiso(int piso)
    {
        this.piso = piso;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof Habitacion that))
        {
            return false;
        }
        return numeroHabitacion == that.numeroHabitacion;
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hashCode(numeroHabitacion)*22;
    }
    
    @Override
    public String toString()
    {
        return "Habitacion: " +
                "numeroHabitacion: " + numeroHabitacion +
                ", piso: " + piso +
                ", tipoHabitacion: " + tipoHabitacion ;
    }
}

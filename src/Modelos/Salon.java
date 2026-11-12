package Modelos;

import Enums.TipoSalon;
import Interfaz.IIdentificable;

public class Salon implements IIdentificable
{
    private int id;
    private String nombre;
    private int capacidad;
    private TipoSalon tipo;
    
    public Salon(int id, String nombre, int capacidad, TipoSalon tipo)
    {
        this.id = id;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.tipo = tipo;
    }

    public int getId()
    {
        return id;
    }
    
    public String getNombre()
    {
        return nombre;
    }
    
    public int getCapacidad()
    {
        return capacidad;
    }
    
    public TipoSalon getTipo()
    {
        return tipo;
    }
    
    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }
    
    public void setCapacidad(int capacidad)
    {
        this.capacidad = capacidad;
    }
    
    public void setTipo(TipoSalon tipo)
    {
        this.tipo = tipo;
    }
    
    @Override
    public String toString()
    {
        return "Salón #" + id + " | " + nombre + " | Capacidad: " + capacidad + " | Tipo: " + tipo;
    }
}

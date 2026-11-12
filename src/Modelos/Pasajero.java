package Modelos;

import Interfaz.IIdentificable;

import java.util.Objects;

public class Pasajero implements IIdentificable
{
    private String nombre;
    private String apellido;
    private String dni;
    private String origen;
    private String domicilioOrigen;
    private int id;
    
    public Pasajero(String nombre, String apellido, String dni, String domicilioOrigen, String origen,int id)
    {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.domicilioOrigen = domicilioOrigen;
        this.origen = origen;
        this.id=id;
        
    }
    
    public String getNombre()
    {
        return nombre;
    }
    
    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }
    
    public String getApellido()
    {
        return apellido;
    }
    
    public void setApellido(String apellido)
    {
        this.apellido = apellido;
    }
    
    public String getDni()
    {
        return dni;
    }
    
    public void setDni(String dni)
    {
        this.dni = dni;
    }
    
    public String getOrigen()
    {
        return origen;
    }
    
    public void setOrigen(String origen)
    {
        this.origen = origen;
    }
    
    public String getDomicilioOrigen()
    {
        return domicilioOrigen;
    }
    
    public void setDomicilioOrigen(String domicilioOrigen)
    {
        this.domicilioOrigen = domicilioOrigen;
    }
    
    public int getId()
    {
        return id;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof Pasajero pasajero))
        {
            return false;
        }
        return Objects.equals(dni, pasajero.dni);
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hashCode(dni) * 22;
    }
    
    @Override
    public String toString()
    {
        return "Pasajero: " +
                "nombre:'" + nombre + '\'' +
                ", apellido: '" + apellido + '\'' +
                ", dni: '" + dni + '\'' +
                ", origen: '" + origen + '\'' +
                ", domicilioOrigen: '" + domicilioOrigen + '\'' +
                ", id: " + id + ".";
    }
}

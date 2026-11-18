package Excepciones;

import java.time.LocalDate;

// El salón ya tiene un evento ese día
public class SalonNoDisponibleException extends Exception
{
    public SalonNoDisponibleException(String nombreSalon, LocalDate fecha)
    {
        super("El salón " + nombreSalon + " ya tiene un evento confirmado el " + fecha + ".");
    }
}

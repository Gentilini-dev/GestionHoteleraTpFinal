package Excepciones;

import java.time.LocalDate;

public class SalonNoDisponibleException extends Exception
{
    public SalonNoDisponibleException(String nombreSalon, LocalDate fecha)
    {
        super("El salón " + nombreSalon + " ya tiene un evento confirmado el " + fecha + ".");
    }
}

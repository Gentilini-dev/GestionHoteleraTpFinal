package Excepciones;

import java.time.LocalDate;

// La habitación ya está reservada o bloqueada en esas fechas
public class HabitacionNoDisponibleException extends Exception {
    public HabitacionNoDisponibleException() {
        super("La habitación no se encuentra disponible");
    }

    public HabitacionNoDisponibleException(String message){
        super(message);
    }

    public HabitacionNoDisponibleException(int nro, LocalDate desde, LocalDate hasta)
    {
        super("La habitación " + nro + " no está disponible del " + desde + " al " + hasta + ".");
    }
}

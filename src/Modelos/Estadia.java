package Modelos;

import Enums.EstadoEstadia;

import java.time.LocalDate;
import java.util.ArrayList;

public class Estadia
{
    private int id;
    private Habitacion habitacion;
    private ArrayList<Pasajero> ocupantes;
    private LocalDate fechaIngreso;
    private EstadoEstadia estado;
    
    public Estadia(int id, Habitacion habitacion, ArrayList<Pasajero> ocupantes, LocalDate fechaIngreso, EstadoEstadia estado)
    {
        this.id = id;
        this.habitacion = habitacion;
        this.ocupantes = ocupantes;
        this.fechaIngreso = fechaIngreso;
        this.estado = estado;
    }
    
}

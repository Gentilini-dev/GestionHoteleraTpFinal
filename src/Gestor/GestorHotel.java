package Gestor;

import Modelos.*;
import Colecciones.Catalogo;

import java.util.ArrayList;
import java.util.HashMap;

public class GestorHotel
{
    private Catalogo<Pasajero> pasajeros;
    private Catalogo<Habitacion> habitaciones;
    private Catalogo<Salon> salones;
    private ArrayList<Reserva> reservas;
    private ArrayList<Estadia> estadias;
    private ArrayList<BloqueoHabitacion> bloqueos;
   private ArrayList<Evento> eventos;
    
    public GestorHotel()
    {
        this.pasajeros = new Catalogo<>();
        this.eventos = new ArrayList<>();
        this.bloqueos = new ArrayList<>();
        this.estadias = new ArrayList<>();
        this.reservas = new ArrayList<>();
        this.salones = new Catalogo<>();
        this.habitaciones = new Catalogo<>();
    }
}

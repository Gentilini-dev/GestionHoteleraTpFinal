package Gestor;

import Enums.*;
import Excepciones.*;
import Modelos.*;
import Colecciones.Catalogo;

import java.time.LocalDate;

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
    
    public void registrarPasajero(Pasajero p) throws ElementoDuplicadoException
    {
        pasajeros.agregar(p);
    }
    
    public void agregarHabitacion(Habitacion h) throws ElementoDuplicadoException
    {
        habitaciones.agregar(h);
    }
    
    public void agregarSalon(Salon s) throws ElementoDuplicadoException
    {
        salones.agregar(s);
    }
    
    private boolean seSuperponen(LocalDate desde1, LocalDate hasta1, LocalDate desde2, LocalDate hasta2)
    {
        return desde1.isBefore(hasta2) && desde2.isBefore(hasta1);
    }
    
    private boolean estaLibre(int nro, LocalDate desde, LocalDate hasta)
    {
        for (Reserva r : reservas)
        {
            if (r.getHabitacion().getId() == nro
                    && (r.getEstado() == EstadoReserva.CONFIRMADA || r.getEstado() == EstadoReserva.EN_CURSO)
                    && seSuperponen(r.getDesde(), r.getHasta(), desde, hasta))
            {
                return false;
            }
        }
        
        for (BloqueoHabitacion b : bloqueos)
        {
            if (b.getHabitacion().getId() == nro
                    && seSuperponen(b.getDesde(), b.getHasta(), desde, hasta))
            {
                return false;
            }
        }
        
        return true;
    }
    
    public boolean estaDisponible(int nro, LocalDate desde, LocalDate hasta) throws ElementoNoEncontradoException
    {
        habitaciones.buscar(nro);   // si no existe, lanza la excepción
        return estaLibre(nro, desde, hasta);
    }
    
    private int proximoIdReserva = 1;
    
    public Pasajero buscarPasajeroPorDni(String dni) throws ElementoNoEncontradoException
    {
        for (Pasajero p : pasajeros.listar())
        {
            if (p.getDni().equals(dni))
            {
                return p;
            }
        }
        throw new ElementoNoEncontradoException("No se encontró ningún pasajero con DNI " + dni + ".");
    }
    
    public Reserva crearReserva(String dni, int nro, LocalDate desde, int noches)
            throws DatosInvalidosException, ElementoNoEncontradoException, HabitacionNoDisponibleException
    {
        if (noches < 1)
        {
            throw new DatosInvalidosException("La reserva debe ser de al menos una noche.");
        }
        if (desde.isBefore(LocalDate.now()))
        {
            throw new DatosInvalidosException("No se puede reservar en una fecha pasada.");
        }
        
        LocalDate hasta = desde.plusDays(noches);
        Pasajero titular = buscarPasajeroPorDni(dni);
        Habitacion habitacion = habitaciones.buscar(nro);
        
        if (!estaDisponible(nro, desde, hasta))
        {
            throw new HabitacionNoDisponibleException(nro, desde, hasta);
        }
        
        Reserva r = new Reserva(proximoIdReserva, titular, habitacion, desde, hasta, EstadoReserva.CONFIRMADA);
        proximoIdReserva++;
        reservas.add(r);
        return r;
    }
    
    private Reserva buscarReserva(int idReserva) throws ElementoNoEncontradoException
    {
        for (Reserva r : reservas)
        {
            if (r.getId() == idReserva)
            {
                return r;
            }
        }
        throw new ElementoNoEncontradoException("No existe una reserva con id " + idReserva + ".");
    }
    
    public void cancelarReserva(int idReserva) throws ElementoNoEncontradoException, DatosInvalidosException
    {
        Reserva r = buscarReserva(idReserva);
        
        if (r.getEstado() != EstadoReserva.CONFIRMADA)
        {
            throw new DatosInvalidosException("La reserva " + idReserva +
                                                      " no se puede cancelar porque está " + r.getEstado() + ".");
        }
        
        r.setEstado(EstadoReserva.CANCELADA);
    }
    
    private int proximoIdEstadia = 1;
    
    private boolean tieneEstadiaActiva(int nro)
    {
        for (Estadia e : estadias)
        {
            if (e.getHabitacion().getId() == nro && e.getEstado() == EstadoEstadia.ACTIVA)
            {
                return true;
            }
        }
        return false;
    }
    
    private Estadia buscarEstadiaActiva(int nro) throws ElementoNoEncontradoException
    {
        for (Estadia e : estadias)
        {
            if (e.getHabitacion().getId() == nro && e.getEstado() == EstadoEstadia.ACTIVA)
            {
                return e;
            }
        }
        throw new ElementoNoEncontradoException("La habitación " + nro + " no tiene una estadía activa.");
    }
    
    public Estadia checkIn(int idReserva, ArrayList<String> dnisAcompanantes)
            throws ElementoNoEncontradoException, DatosInvalidosException
    {
        Reserva r = buscarReserva(idReserva);
        
        if (r.getEstado() != EstadoReserva.CONFIRMADA)
        {
            throw new DatosInvalidosException("La reserva " + idReserva +
                                                      " no admite check-in porque está " + r.getEstado() + ".");
        }
        
        LocalDate hoy = LocalDate.now();
        if (hoy.isBefore(r.getDesde()) || !hoy.isBefore(r.getHasta()))
        {
            throw new DatosInvalidosException("El check-in de la reserva " + idReserva +
                                                      " solo puede hacerse entre el " + r.getDesde() + " y el " + r.getHasta().minusDays(1) + ".");
        }
        
        int nro = r.getHabitacion().getId();
        if (tieneEstadiaActiva(nro))
        {
            throw new DatosInvalidosException("La habitación " + nro + " ya tiene un check-in activo.");
        }
        
        ArrayList<Pasajero> ocupantes = new ArrayList<>();
        ocupantes.add(r.getTitular());
        for (String dni : dnisAcompanantes)
        {
            Pasajero p = buscarPasajeroPorDni(dni);
            if (!ocupantes.contains(p))
            {
                ocupantes.add(p);
            }
        }
        
        Estadia e = new Estadia(proximoIdEstadia, r.getHabitacion(), ocupantes);
        proximoIdEstadia++;
        estadias.add(e);
        r.setEstado(EstadoReserva.EN_CURSO);
        return e;
    }
    
    public Estadia checkOut(int nro) throws ElementoNoEncontradoException
    {
        Estadia e = buscarEstadiaActiva(nro);
        e.finalizar();
        
        LocalDate hoy = LocalDate.now();
        for (Reserva r : reservas)
        {
            if (r.getHabitacion().getId() == nro && r.getEstado() == EstadoReserva.EN_CURSO)
            {
                r.setEstado(EstadoReserva.FINALIZADA);
            }
        }
        return e;
    }
    
    private int proximoIdBloqueo = 1;
    
    public BloqueoHabitacion bloquearHabitacion(int nro, MotivoBloqueo motivo, String detalle, LocalDate desde, int dias)
            throws DatosInvalidosException, ElementoNoEncontradoException, HabitacionNoDisponibleException
    {
        if (dias < 1)
        {
            throw new DatosInvalidosException("Error, no se puede hacer una reserva de " + dias + " días.");
        }
        LocalDate hasta = desde.plusDays(dias);
        Habitacion h = habitaciones.buscar(nro);
        if (!estaDisponible(nro, desde, hasta))
        {
            throw new HabitacionNoDisponibleException(nro, desde, hasta);
        }
        
        BloqueoHabitacion b = new BloqueoHabitacion(proximoIdBloqueo, h, motivo, detalle, desde, hasta);
        bloqueos.add(b);
        proximoIdBloqueo++;
        return b;
    }
    
    public ArrayList<Estadia> listarOcupadas()
    {
        ArrayList<Estadia> ocupadas = new ArrayList<>();
        for (Estadia e : estadias)
        {
            if (e.getEstado() == EstadoEstadia.ACTIVA)
            {
                ocupadas.add(e);
            }
        }
        return ocupadas;
    }
    
    public ArrayList<Habitacion> listarDisponibles()
    {
        ArrayList<Habitacion> disponibles = new ArrayList<>();
        LocalDate hoy = LocalDate.now();
        for (Habitacion h : habitaciones.listar())
        {
            if (estaLibre(h.getId(), hoy, hoy.plusDays(1)))
            {
                disponibles.add(h);
            }
        }
        return disponibles;
    }
    
    public ArrayList<BloqueoHabitacion> listarNoDisponibles()
    {
        ArrayList<BloqueoHabitacion> noDisponibles = new ArrayList<>();
        for (BloqueoHabitacion b : bloqueos)
        {
            if (b.estaActivo())
            {
                noDisponibles.add(b);
            }
        }
        return noDisponibles;
    }
    
    private int proximoIdEvento = 1;
    
    private Evento buscarEvento(int idEvento) throws ElementoNoEncontradoException
    {
        for (Evento e : eventos)
        {
            if (e.getId() == idEvento)
            {
                return e;
            }
        }
        throw new ElementoNoEncontradoException("No existe un evento con id " + idEvento + ".");
    }
    
    public Evento reservarSalon(int idSalon, LocalDate fecha, String organizador, int asistentes)
            throws DatosInvalidosException, ElementoNoEncontradoException, SalonNoDisponibleException
    {
        if (fecha.isBefore(LocalDate.now()))
        {
            throw new DatosInvalidosException("No se puede reservar un salón en una fecha pasada.");
        }
        
        Salon salon = salones.buscar(idSalon);
        
        if (asistentes < 1 || asistentes > salon.getCapacidad())
        {
            throw new DatosInvalidosException("La cantidad de asistentes debe estar entre 1 y " +
                                                      salon.getCapacidad() + " (capacidad del salón " + salon.getNombre() + ").");
        }
        
        
        for (Evento e : eventos)
        {
            if (e.getSalon().getId() == idSalon
                    && e.getEstado() == EstadoEvento.CONFIRMADO
                    && e.getFecha().equals(fecha))
            {
                throw new SalonNoDisponibleException(salon.getNombre(), fecha);
            }
        }
        
        Evento evento = new Evento(proximoIdEvento, salon, organizador, fecha, asistentes);
        proximoIdEvento++;
        eventos.add(evento);
        return evento;
    }
    
    public void cancelarEvento(int idEvento) throws ElementoNoEncontradoException, DatosInvalidosException
    {
        Evento e = buscarEvento(idEvento);
        
        if (e.getEstado() != EstadoEvento.CONFIRMADO)
        {
            throw new DatosInvalidosException("El evento " + idEvento +
                                                      " no se puede cancelar porque está " + e.getEstado() + ".");
        }
        
        e.cancelar();
    }
    
    public ArrayList<Estadia> historialPasajero(String dni) throws ElementoNoEncontradoException
    {
        Pasajero p = buscarPasajeroPorDni(dni);
        
        ArrayList<Estadia> historial = new ArrayList<>();
        for (Estadia e : estadias)
        {
            if (e.getOcupantes().contains(p))
            {
                historial.add(e);
            }
        }
        return historial;
    }
    public ArrayList<Pasajero> listarPasajeros()
    {
        return pasajeros.listar();
    }
    public ArrayList<Reserva> reservasDePasajero(String dni)
            throws ElementoNoEncontradoException
    {
        Pasajero pasajero = buscarPasajeroPorDni(dni);
        
        ArrayList<Reserva> reservasPasajero = new ArrayList<>();
        
        for (Reserva reserva : reservas)
        {
            if (reserva.getTitular().getDni().equals(pasajero.getDni()))
            {
                reservasPasajero.add(reserva);
            }
        }
        
        return reservasPasajero;
    }
}

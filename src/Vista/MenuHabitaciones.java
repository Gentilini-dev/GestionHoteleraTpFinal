package Vista;

import Enums.MotivoBloqueo;
import Enums.TipoHabitacion;
import Excepciones.DatosInvalidosException;
import Excepciones.ElementoDuplicadoException;
import Excepciones.ElementoNoEncontradoException;
import Excepciones.ErrorConexionSQLException;
import Excepciones.HabitacionNoDisponibleException;
import Gestor.GestorHotel;
import Modelos.BloqueoHabitacion;
import Modelos.Estadia;
import Modelos.Habitacion;
import Modelos.Pasajero;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

// Submenú de habitaciones: altas, listados, disponibilidad y bloqueos
public class MenuHabitaciones
{
    private GestorHotel gestor;
    private Scanner sc;
    
    public MenuHabitaciones(GestorHotel gestor, Scanner sc)
    {
        this.gestor = gestor;
        this.sc = sc;
    }
    
    public void iniciar()
    {
        String opcion;
        
        do
        {
            System.out.println();
            System.out.println("----- MENÚ HABITACIONES -----");
            System.out.println("1. Agregar habitación");
            System.out.println("2. Listar habitaciones disponibles");
            System.out.println("3. Listar habitaciones ocupadas");
            System.out.println("4. Listar bloqueos activos");
            System.out.println("5. Consultar disponibilidad por fechas");
            System.out.println("6. Bloquear habitación");
            System.out.println("0. Volver");
            System.out.print("Elegí una opción: ");
            
            opcion = sc.nextLine();
            
            switch (opcion)
            {
                case "1":
                    agregarHabitacion();
                    break;
                
                case "2":
                    listarDisponibles();
                    break;
                
                case "3":
                    listarOcupadas();
                    break;
                
                case "4":
                    listarBloqueos();
                    break;
                
                case "5":
                    consultarDisponibilidad();
                    break;
                
                case "6":
                    bloquearHabitacion();
                    break;
                
                case "0":
                    System.out.println("Volviendo al menu principal.");
                    break;
                
                default:
                    System.out.println("Opcion invalida.");
            }
            
        }
        while (!opcion.equals("0"));
    }
    
    private void agregarHabitacion()
    {
        try
        {
            System.out.println();
            System.out.println("----- AGREGAR HABITACIÓN -----");
            
            System.out.print("Número de habitación: ");
            int numero = Integer.parseInt(sc.nextLine());
            
            System.out.print("Piso: ");
            int piso = Integer.parseInt(sc.nextLine());
            
            System.out.println("Tipos disponibles:");
            
            for (TipoHabitacion tipo : TipoHabitacion.values())
            {
                System.out.println("- " + tipo);
            }
            
            System.out.print("Tipo de habitación: ");
            String textoTipo = sc.nextLine().trim().toUpperCase();
            
            TipoHabitacion tipo = TipoHabitacion.valueOf(textoTipo);
            
            Habitacion habitacion = new Habitacion(numero, piso, tipo);
            
            gestor.agregarHabitacion(habitacion);
            
            System.out.println("Habitación agregada correctamente.");
        }
        catch (NumberFormatException e)
        {
            System.out.println("El número y el piso deben ser números enteros.");
        }
        catch (IllegalArgumentException e)
        {
            System.out.println("El tipo de habitación ingresado no es válido.");
        }
        catch (ElementoDuplicadoException | ErrorConexionSQLException e)
        {
            System.out.println(e.getMessage());
        }
    }
    
    private void listarDisponibles()
    {
        ArrayList<Habitacion> disponibles = gestor.listarDisponibles();
        
        System.out.println();
        System.out.println("----- HABITACIONES DISPONIBLES -----");
        
        if (disponibles.isEmpty())
        {
            System.out.println("No hay habitaciones disponibles.");
        }
        else
        {
            for (Habitacion habitacion : disponibles)
            {
                System.out.println(habitacion);
            }
        }
    }
    
    private void listarOcupadas()
    {
        ArrayList<Estadia> ocupadas = gestor.listarOcupadas();
        
        System.out.println();
        System.out.println("----- HABITACIONES OCUPADAS -----");
        
        if (ocupadas.isEmpty())
        {
            System.out.println("No hay habitaciones ocupadas.");
        }
        else
        {
            for (Estadia estadia : ocupadas)
            {
                System.out.println(estadia);
                System.out.println("Ocupantes:");
                
                for (Pasajero pasajero : estadia.getOcupantes())
                {
                    System.out.println("- " + pasajero);
                }
                
                System.out.println();
            }
        }
    }
    
    private void listarBloqueos()
    {
        ArrayList<BloqueoHabitacion> bloqueos = gestor.listarNoDisponibles();
        
        System.out.println();
        System.out.println("----- BLOQUEOS ACTIVOS -----");
        
        if (bloqueos.isEmpty())
        {
            System.out.println("No hay bloqueos activos.");
        }
        else
        {
            for (BloqueoHabitacion bloqueo : bloqueos)
            {
                System.out.println(bloqueo);
            }
        }
    }
    
    // Si se puede ocupar una habitación entre dos fechas
    private void consultarDisponibilidad()
    {
        try
        {
            System.out.println();
            System.out.println("----- CONSULTAR DISPONIBILIDAD -----");
            
            System.out.print("Número de habitación: ");
            int numero = Integer.parseInt(sc.nextLine());
            
            System.out.print("Desde (AAAA-MM-DD): ");
            LocalDate desde = LocalDate.parse(sc.nextLine().trim());
            
            System.out.print("Hasta (AAAA-MM-DD): ");
            LocalDate hasta = LocalDate.parse(sc.nextLine().trim());
            
            boolean disponible = gestor.estaDisponible(numero, desde, hasta);
            
            if (disponible)
            {
                System.out.println("La habitación está disponible para esas fechas.");
            }
            else
            {
                System.out.println("La habitación no está disponible para esas fechas.");
            }
        }
        catch (NumberFormatException e)
        {
            System.out.println("El número de habitación debe ser un número entero.");
        }
        catch (DateTimeParseException e)
        {
            System.out.println("Fecha inválida. Usá el formato AAAA-MM-DD.");
        }
        catch (ElementoNoEncontradoException | DatosInvalidosException e)
        {
            System.out.println(e.getMessage());
        }
    }
    
    private void bloquearHabitacion()
    {
        try
        {
            System.out.println();
            System.out.println("----- BLOQUEAR HABITACIÓN -----");
            
            System.out.print("Número de habitación: ");
            int numero = Integer.parseInt(sc.nextLine());
            
            System.out.println("Motivos disponibles:");
            
            for (MotivoBloqueo motivo : MotivoBloqueo.values())
            {
                System.out.println("- " + motivo);
            }
            
            System.out.print("Motivo: ");
            String textoMotivo = sc.nextLine().trim().toUpperCase();
            
            MotivoBloqueo motivo = MotivoBloqueo.valueOf(textoMotivo);
            
            System.out.print("Detalle: ");
            String detalle = sc.nextLine();
            
            System.out.print("Desde (AAAA-MM-DD): ");
            LocalDate desde = LocalDate.parse(sc.nextLine().trim());
            
            System.out.print("Cantidad de días: ");
            int dias = Integer.parseInt(sc.nextLine());
            
            BloqueoHabitacion bloqueo = gestor.bloquearHabitacion(
                    numero,
                    motivo,
                    detalle,
                    desde,
                    dias
                                                                 );
            
            System.out.println("Habitación bloqueada correctamente.");
            System.out.println(bloqueo);
        }
        catch (NumberFormatException e)
        {
            System.out.println("La habitación y los días deben ser números enteros.");
        }
        catch (DateTimeParseException e)
        {
            System.out.println("Fecha inválida. Usá el formato AAAA-MM-DD.");
        }
        catch (IllegalArgumentException e)
        {
            System.out.println("El motivo ingresado no es válido.");
        }
        catch (DatosInvalidosException |
               ElementoNoEncontradoException |
               HabitacionNoDisponibleException |
               ErrorConexionSQLException e)
        {
            System.out.println(e.getMessage());
        }
    }
}
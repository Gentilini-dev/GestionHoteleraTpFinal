package Vista;

import Enums.TipoSalon;
import Excepciones.DatosInvalidosException;
import Excepciones.ElementoDuplicadoException;
import Excepciones.ElementoNoEncontradoException;
import Excepciones.ErrorConexionSQLException;
import Excepciones.SalonNoDisponibleException;
import Gestor.GestorHotel;
import Modelos.Evento;
import Modelos.Salon;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

// Submenú de salones y eventos. Acá solo se piden los datos, la lógica está en el gestor
public class MenuEventos
{
    private GestorHotel gestor;
    private Scanner sc;
    
    public MenuEventos(GestorHotel gestor, Scanner sc)
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
            System.out.println("----- MENÚ SALONES Y EVENTOS -----");
            System.out.println("1. Agregar salón");
            System.out.println("2. Listar salones");
            System.out.println("3. Reservar salón para un evento");
            System.out.println("4. Listar eventos");
            System.out.println("5. Cancelar evento");
            System.out.println("0. Volver");
            System.out.print("Elegí una opción: ");
            
            opcion = sc.nextLine();
            
            switch (opcion)
            {
                case "1":
                    agregarSalon();
                    break;
                
                case "2":
                    listarSalones();
                    break;
                
                case "3":
                    reservarSalon();
                    break;
                
                case "4":
                    listarEventos();
                    break;
                
                case "5":
                    cancelarEvento();
                    break;
                
                case "0":
                    break;
                
                default:
                    System.out.println("Opción inválida.");
            }
        }
        while (!opcion.equals("0"));
    }
    
    private void agregarSalon()
    {
        try
        {
            System.out.println();
            System.out.println("----- AGREGAR SALÓN -----");
            
            System.out.print("Número de salón: ");
            int id = Integer.parseInt(sc.nextLine());
            
            System.out.print("Nombre: ");
            String nombre = sc.nextLine();
            
            System.out.print("Capacidad (personas): ");
            int capacidad = Integer.parseInt(sc.nextLine());
            
            System.out.println("Tipos disponibles:");
            for (TipoSalon tipo : TipoSalon.values())
            {
                System.out.println("- " + tipo);
            }
            
            System.out.print("Tipo de salón: ");
            TipoSalon tipo = TipoSalon.valueOf(sc.nextLine().trim().toUpperCase());
            
            gestor.agregarSalon(new Salon(id, nombre, capacidad, tipo));
            
            System.out.println("Salón agregado correctamente.");
        }
        catch (NumberFormatException e)
        {
            System.out.println("El número y la capacidad deben ser números enteros.");
        }
        catch (IllegalArgumentException e)
        {
            System.out.println("El tipo de salón ingresado no es válido.");
        }
        catch (ElementoDuplicadoException | ErrorConexionSQLException e)
        {
            System.out.println(e.getMessage());
        }
    }
    
    private void listarSalones()
    {
        ArrayList<Salon> salones = gestor.listarSalones();
        
        System.out.println();
        System.out.println("----- SALONES -----");
        
        if (salones.isEmpty())
        {
            System.out.println("No hay salones cargados.");
            return;
        }
        
        for (Salon salon : salones)
        {
            System.out.println(salon);
        }
    }
    
    private void reservarSalon()
    {
        try
        {
            System.out.println();
            System.out.println("----- RESERVAR SALÓN -----");
            
            System.out.print("Número de salón: ");
            int idSalon = Integer.parseInt(sc.nextLine());
            
            System.out.print("Fecha del evento (AAAA-MM-DD): ");
            LocalDate fecha = LocalDate.parse(sc.nextLine().trim());
            
            System.out.print("Organizador: ");
            String organizador = sc.nextLine();
            
            System.out.print("Cantidad de asistentes: ");
            int asistentes = Integer.parseInt(sc.nextLine());
            
            Evento evento = gestor.reservarSalon(idSalon, fecha, organizador, asistentes);
            
            System.out.println("Evento reservado correctamente.");
            System.out.println(evento);
        }
        catch (NumberFormatException e)
        {
            System.out.println("El salón y los asistentes deben ser números enteros.");
        }
        catch (DateTimeParseException e)
        {
            System.out.println("Fecha inválida. Usá el formato AAAA-MM-DD.");
        }
        catch (DatosInvalidosException |
               ElementoNoEncontradoException |
               SalonNoDisponibleException |
               ErrorConexionSQLException e)
        {
            System.out.println(e.getMessage());
        }
    }
    
    private void listarEventos()
    {
        ArrayList<Evento> eventos = gestor.listarEventos();
        
        System.out.println();
        System.out.println("----- EVENTOS -----");
        
        if (eventos.isEmpty())
        {
            System.out.println("No hay eventos registrados.");
            return;
        }
        
        for (Evento evento : eventos)
        {
            System.out.println(evento);
        }
    }
    
    private void cancelarEvento()
    {
        try
        {
            System.out.println();
            System.out.print("ID del evento a cancelar: ");
            int idEvento = Integer.parseInt(sc.nextLine());
            
            gestor.cancelarEvento(idEvento);
            
            System.out.println("Evento cancelado correctamente.");
        }
        catch (NumberFormatException e)
        {
            System.out.println("El ID debe ser un número entero.");
        }
        catch (ElementoNoEncontradoException | DatosInvalidosException | ErrorConexionSQLException e)
        {
            System.out.println(e.getMessage());
        }
    }
}

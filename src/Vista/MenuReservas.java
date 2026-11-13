package Vista;

import Excepciones.DatosInvalidosException;
import Excepciones.ElementoNoEncontradoException;
import Excepciones.HabitacionNoDisponibleException;
import Gestor.GestorHotel;
import Modelos.Estadia;
import Modelos.Reserva;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class MenuReservas
{
    private GestorHotel gestor;
    private Scanner sc;
    
    public MenuReservas(GestorHotel gestor, Scanner sc)
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
            System.out.println("----- MENÚ RESERVAS Y ESTADÍAS -----");
            System.out.println("1. Crear reserva");
            System.out.println("2. Cancelar reserva");
            System.out.println("3. Realizar check-in");
            System.out.println("4. Realizar check-out");
            System.out.println("0. Volver");
            System.out.print("Elegí una opción: ");
            
            opcion = sc.nextLine();
            
            switch (opcion)
            {
                case "1":
                    crearReserva();
                    break;
                
                case "2":
                    cancelarReserva();
                    break;
                
                case "3":
                    realizarCheckIn();
                    break;
                
                case "4":
                    realizarCheckOut();
                    break;
                
                case "0":
                    System.out.println("Volviendo al menú principal.");
                    break;
                
                default:
                    System.out.println("Opción inválida.");
            }
            
        }
        while (!opcion.equals("0"));
    }
    
    private void crearReserva()
    {
        try
        {
            System.out.println();
            System.out.println("----- CREAR RESERVA -----");
            
            System.out.print("DNI del titular: ");
            String dni = sc.nextLine();
            
            System.out.print("Número de habitación: ");
            int numero = Integer.parseInt(sc.nextLine());
            
            System.out.print("Fecha de ingreso (AAAA-MM-DD): ");
            LocalDate desde = LocalDate.parse(sc.nextLine().trim());
            
            System.out.print("Cantidad de noches: ");
            int noches = Integer.parseInt(sc.nextLine());
            
            Reserva reserva = gestor.crearReserva(
                    dni,
                    numero,
                    desde,
                    noches
                                                 );
            
            System.out.println("Reserva creada correctamente.");
            System.out.println(reserva);
        }
        catch (NumberFormatException e)
        {
            System.out.println("La habitación y las noches deben ser números enteros.");
        }
        catch (DateTimeParseException e)
        {
            System.out.println("Fecha inválida. Usá el formato AAAA-MM-DD.");
        }
        catch (DatosInvalidosException |
               ElementoNoEncontradoException |
               HabitacionNoDisponibleException e)
        {
            System.out.println(e.getMessage());
        }
    }
    
    private void cancelarReserva()
    {
        try
        {
            System.out.println();
            System.out.println("----- CANCELAR RESERVA -----");
            
            System.out.print("ID de la reserva: ");
            int idReserva = Integer.parseInt(sc.nextLine());
            
            gestor.cancelarReserva(idReserva);
            
            System.out.println("Reserva cancelada correctamente.");
        }
        catch (NumberFormatException e)
        {
            System.out.println("El ID debe ser un número entero.");
        }
        catch (ElementoNoEncontradoException | DatosInvalidosException e)
        {
            System.out.println(e.getMessage());
        }
    }
    
    private void realizarCheckIn()
    {
        try
        {
            System.out.println();
            System.out.println("----- REALIZAR CHECK-IN -----");
            
            System.out.print("ID de la reserva: ");
            int idReserva = Integer.parseInt(sc.nextLine());
            
            ArrayList<String> dnisAcompanantes = new ArrayList<>();
            
            System.out.println("Ingresá los DNI de los acompañantes.");
            System.out.println("Dejá vacío y presioná Enter para terminar.");
            
            String dni;
            
            do
            {
                System.out.print("DNI del acompañante: ");
                dni = sc.nextLine().trim();
                
                if (!dni.isEmpty())
                {
                    dnisAcompanantes.add(dni);
                }
                
            }
            while (!dni.isEmpty());
            
            Estadia estadia = gestor.checkIn(idReserva, dnisAcompanantes);
            
            System.out.println("Check-in realizado correctamente.");
            System.out.println(estadia);
        }
        catch (NumberFormatException e)
        {
            System.out.println("El ID debe ser un número entero.");
        }
        catch (ElementoNoEncontradoException | DatosInvalidosException e)
        {
            System.out.println(e.getMessage());
        }
    }
    
    private void realizarCheckOut()
    {
        try
        {
            System.out.println();
            System.out.println("----- REALIZAR CHECK-OUT -----");
            
            System.out.print("Número de habitación: ");
            int numero = Integer.parseInt(sc.nextLine());
            
            Estadia estadia = gestor.checkOut(numero);
            
            System.out.println("Check-out realizado correctamente.");
            System.out.println(estadia);
        }
        catch (NumberFormatException e)
        {
            System.out.println("El número de habitación debe ser un número entero.");
        }
        catch (ElementoNoEncontradoException e)
        {
            System.out.println(e.getMessage());
        }
    }
}
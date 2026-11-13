package Vista;

import Excepciones.ElementoDuplicadoException;
import Excepciones.ElementoNoEncontradoException;
import Gestor.GestorHotel;
import Modelos.Estadia;
import Modelos.Pasajero;
import Modelos.Reserva;

import java.util.ArrayList;
import java.util.Scanner;

public class MenuPasajeros
{
    private GestorHotel gestor;
    private Scanner sc;
    
    public MenuPasajeros(GestorHotel gestor, Scanner sc)
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
            System.out.println("----- MENÚ PASAJEROS -----");
            System.out.println("1. Registrar pasajero");
            System.out.println("2. Listar pasajeros");
            System.out.println("3. Buscar pasajero por DNI");
            System.out.println("0. Volver");
            System.out.print("Elegí una opción: ");
            
            opcion = sc.nextLine();
            
            switch (opcion)
            {
                case "1":
                    registrarPasajero();
                    break;
                
                case "2":
                    listarPasajeros();
                    break;
                
                case "3":
                    buscarPasajero();
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
    
    private void registrarPasajero()
    {
        try
        {
            System.out.println();
            System.out.println("----- REGISTRAR PASAJERO -----");
            
            System.out.print("ID: ");
            int id = Integer.parseInt(sc.nextLine());
            
            System.out.print("Nombre: ");
            String nombre = sc.nextLine();
            
            System.out.print("Apellido: ");
            String apellido = sc.nextLine();
            
            System.out.print("DNI: ");
            String dni = sc.nextLine();
            
            System.out.print("Domicilio de origen: ");
            String domicilioOrigen = sc.nextLine();
            
            System.out.print("Origen: ");
            String origen = sc.nextLine();
            
            Pasajero pasajero = new Pasajero(
                    nombre,
                    apellido,
                    dni,
                    domicilioOrigen,
                    origen,
                    id
            );
            
            gestor.registrarPasajero(pasajero);
            
            System.out.println("\nPasajero registrado correctamente.");
        }
        catch (NumberFormatException e)
        {
            System.out.println("\nEl ID debe ser un número.");
        }
        catch (ElementoDuplicadoException e)
        {
            System.out.println(e.getMessage());
        }
    }
    
    private void listarPasajeros()
    {
        ArrayList<Pasajero> lista = gestor.listarPasajeros();
        
        System.out.println();
        System.out.println("\n----- LISTA DE PASAJEROS -----");
        
        if (lista.isEmpty())
        {
            System.out.println("\nNo hay pasajeros registrados.");
        }
        else
        {
            for (Pasajero pasajero : lista)
            {
                System.out.println(pasajero);
            }
        }
    }
    
    private void buscarPasajero()
    {
        
        System.out.println("----- BUSCAR PASAJERO -----");
        
        System.out.print("Ingresa el DNI: ");
        String dni = sc.nextLine();
        
        try
        {
            Pasajero pasajero = gestor.buscarPasajeroPorDni(dni);
            
            ArrayList<Reserva> reservas = gestor.reservasDePasajero(dni);
            
            ArrayList<Estadia> estadias = gestor.historialPasajero(dni);
            
            
            System.out.println("\n----- DATOS DEL PASAJERO -----");
            System.out.println(pasajero);
            
            
            System.out.println("\n----- RESERVAS COMO TITULAR -----");
            
            if (reservas.isEmpty())
            {
                System.out.println("\nNo tiene reservas registradas.");
            }
            else
            {
                for (Reserva reserva : reservas)
                {
                    System.out.println(reserva);
                }
            }
            
            System.out.println();
            System.out.println("\n----- HISTORIAL DE ESTADÍAS -----");
            
            if (estadias.isEmpty())
            {
                System.out.println("\nNo tiene estadias registradas.");
            }
            else
            {
                for (Estadia estadia : estadias)
                {
                    System.out.println(estadia);
                }
            }
        }
        catch (ElementoNoEncontradoException e)
        {
            System.out.println(e.getMessage());
        }
    }
}
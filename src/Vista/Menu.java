package Vista;

import Gestor.GestorHotel;
import Modelos.Usuario;
import Modelos.UsuarioAdministrador;
import Modelos.UsuarioRecepcionista;

import java.util.Scanner;

public class Menu
{
    private GestorHotel gestor;
    private Scanner sc;
    private Usuario usuarioActual;
    
    public Menu(GestorHotel gestor, Scanner sc)
    {
        this.gestor = gestor;
        this.sc = sc;
    }
    
    public void iniciar()
    {
        System.out.println();
        System.out.println("----- INICIO DE SESIÓN -----");
        
        System.out.print("Usuario: ");
        String nombreUsuario = sc.nextLine();
        
        System.out.print("Contraseña: ");
        String password = sc.nextLine();
        
        usuarioActual = buscarUsuario(nombreUsuario, password);
        
        if (usuarioActual == null)
        {
            System.out.println("Usuario o contraseña incorrectos.");
            return;
        }
        
        System.out.println("Bienvenido/a, " + usuarioActual.getNombre());
        
        if (usuarioActual instanceof UsuarioAdministrador)
        {
            menuAdministrador();
        }
        else if (usuarioActual instanceof UsuarioRecepcionista)
        {
            menuRecepcionista();
        }
        else
        {
            System.out.println("El usuario no tiene un rol reconocido.");
        }
    }
    
    private Usuario buscarUsuario(String nombreUsuario, String password)
    {
        
        Usuario administrador = new UsuarioAdministrador(1, "Administrador", "admin", "admin123");
        
        Usuario recepcionista = new UsuarioRecepcionista(2, "Recepcionista", "recepcion", "recepcion123");
        
        if (administrador.validarUsuario(nombreUsuario, password))
        {
            return administrador;
        }
        
        if (recepcionista.validarUsuario(nombreUsuario, password))
        {
            return recepcionista;
        }
        
        return null;
    }
    
    private void menuAdministrador()
    {
        String opcion;
        
        do
        {
            System.out.println();
            System.out.println("----- MENÚ ADMINISTRADOR -----");
            System.out.println("1. Gestión de pasajeros");
            System.out.println("2. Gestión de habitaciones");
            System.out.println("3. Gestión de reservas y estadías");
            System.out.println("0. Salir");
            System.out.print("Elegí una opción: ");
            
            opcion = sc.nextLine();
            
            switch (opcion)
            {
                case "1":
                    abrirMenuPasajeros();
                    break;
                
                case "2":
                    abrirMenuHabitaciones();
                    break;
                
                case "3":
                    abrirMenuReservas();
                    break;
                
                case "0":
                    System.out.println("Saliendo del sistema.");
                    break;
                
                default:
                    System.out.println("Opción inválida.");
            }
            
        }
        while (!opcion.equals("0"));
    }
    
    private void menuRecepcionista()
    {
        String opcion;
        
        do
        {
            System.out.println();
            System.out.println("----- MENÚ RECEPCIONISTA -----");
            System.out.println("1. Gestión de pasajeros");
            System.out.println("2. Gestión de habitaciones");
            System.out.println("3. Gestión de reservas y estadías");
            System.out.println("0. Salir");
            System.out.print("Elegí una opción: ");
            
            opcion = sc.nextLine();
            
            switch (opcion)
            {
                case "1":
                    abrirMenuPasajeros();
                    break;
                
                case "2":
                    abrirMenuHabitaciones();
                    break;
                
                case "3":
                    abrirMenuReservas();
                    break;
                
                case "0":
                    System.out.println("Saliendo del sistema.");
                    break;
                
                default:
                    System.out.println("Opción inválida.");
            }
            
        }
        while (!opcion.equals("0"));
    }
    
    private void abrirMenuPasajeros()
    {
        MenuPasajeros menuPasajeros = new MenuPasajeros(gestor, sc);
        
        menuPasajeros.iniciar();
    }
    
    private void abrirMenuHabitaciones()
    {
        MenuHabitaciones menuHabitaciones = new MenuHabitaciones(gestor, sc);
        
        menuHabitaciones.iniciar();
    }
    
    private void abrirMenuReservas()
    {
        MenuReservas menuReservas = new MenuReservas(gestor, sc);
        
        menuReservas.iniciar();
    }
}
package Vista;

import Enums.RolUsuario;
import Excepciones.DatosInvalidosException;
import Excepciones.ElementoDuplicadoException;
import Excepciones.ErrorArchivoException;
import Excepciones.ErrorConexionSQLException;
import Gestor.GestorHotel;
import Modelos.Usuario;

import java.util.ArrayList;
import java.util.Scanner;

// Menú principal: login y después el menú que corresponda según el usuario
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

    // Pide usuario y contraseña hasta que sean correctos. Al cerrar sesión vuelve acá,
    // así puede entrar otro usuario. Escribiendo "salir" se termina el programa
    public void iniciar()
    {
        while (true)
        {
            System.out.println();
            System.out.println("----- INICIO DE SESIÓN -----");
            System.out.println("(escribí 'salir' como usuario para cerrar el programa)");

            System.out.print("Usuario: ");
            String nombreUsuario = sc.nextLine().trim();

            if (nombreUsuario.equalsIgnoreCase("salir"))
            {
                System.out.println("Saliendo del sistema.");
                return;
            }

            System.out.print("Contraseña: ");
            String password = sc.nextLine();

            try
            {
                usuarioActual = gestor.iniciarSesion(nombreUsuario, password);
            }
            catch (DatosInvalidosException e)
            {
                System.out.println(e.getMessage());
                continue;
            }

            System.out.println("Bienvenido/a, " + usuarioActual.getNombre());

            // Polimorfismo: cada tipo de usuario abre su propio menú
            usuarioActual.abrirMenu(this);

            // Si llegamos acá es porque eligió cerrar sesión
            System.out.println("Sesión cerrada.");
            usuarioActual = null;
        }
    }

    // Lo llama UsuarioAdministrador.abrirMenu()
    public void menuAdministrador()
    {
        String opcion;

        do
        {
            System.out.println();
            System.out.println("----- MENÚ ADMINISTRADOR -----");
            System.out.println("1. Gestión de pasajeros");
            System.out.println("2. Gestión de habitaciones");
            System.out.println("3. Gestión de reservas y estadías");
            System.out.println("4. Gestión de salones y eventos");
            System.out.println("5. Crear usuario");
            System.out.println("6. Listar usuarios");
            System.out.println("7. Realizar backup");
            System.out.println("8. Ver último backup");
            System.out.println("0. Cerrar sesión");
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

                case "4":
                    abrirMenuEventos();
                    break;

                case "5":
                    crearUsuario();
                    break;

                case "6":
                    listarUsuarios();
                    break;

                case "7":
                    realizarBackup();
                    break;

                case "8":
                    verBackup();
                    break;

                case "0":
                    break;

                default:
                    System.out.println("Opción inválida.");
            }

        }
        while (!opcion.equals("0"));
    }

    // Lo llama UsuarioRecepcionista.abrirMenu()
    public void menuRecepcionista()
    {
        String opcion;

        do
        {
            System.out.println();
            System.out.println("----- MENÚ RECEPCIONISTA -----");
            System.out.println("1. Gestión de pasajeros");
            System.out.println("2. Gestión de habitaciones");
            System.out.println("3. Gestión de reservas y estadías");
            System.out.println("4. Gestión de salones y eventos");
            System.out.println("0. Cerrar sesión");
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

                case "4":
                    abrirMenuEventos();
                    break;

                case "0":
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

    private void abrirMenuEventos()
    {
        MenuEventos menuEventos = new MenuEventos(gestor, sc);

        menuEventos.iniciar();
    }

    // Guarda todos los datos del hotel en un archivo de texto (solo administrador)
    private void realizarBackup()
    {
        try
        {
            String ruta = gestor.realizarBackup();
            System.out.println("Backup realizado correctamente en: " + ruta);
        }
        catch (ErrorArchivoException e)
        {
            System.out.println(e.getMessage());
        }
    }

    // Lee el archivo de backup y lo muestra por pantalla
    private void verBackup()
    {
        try
        {
            ArrayList<String> lineas = gestor.leerBackup();
            System.out.println();
            for (String linea : lineas)
            {
                System.out.println(linea);
            }
        }
        catch (ErrorArchivoException e)
        {
            System.out.println(e.getMessage());
        }
    }

    // Alta de usuarios, solo desde el menú del administrador
    private void crearUsuario()
    {
        try
        {
            System.out.println();
            System.out.println("----- CREAR USUARIO -----");

            System.out.print("Nombre y apellido: ");
            String nombre = sc.nextLine();

            System.out.print("Usuario: ");
            String nombreUsuario = sc.nextLine().trim();

            System.out.print("Contraseña: ");
            String password = sc.nextLine().trim();

            System.out.println("Roles disponibles:");
            for (RolUsuario rol : RolUsuario.values())
            {
                System.out.println("- " + rol);
            }
            System.out.print("Rol: ");
            RolUsuario rol = RolUsuario.valueOf(sc.nextLine().trim().toUpperCase());

            Usuario nuevo = gestor.crearUsuario(nombre, nombreUsuario, password, rol);

            System.out.println("Usuario creado correctamente.");
            System.out.println(nuevo);
        }
        catch (IllegalArgumentException e)
        {
            System.out.println("El rol ingresado no es válido.");
        }
        catch (ElementoDuplicadoException | DatosInvalidosException | ErrorConexionSQLException e)
        {
            System.out.println(e.getMessage());
        }
    }

    private void listarUsuarios()
    {
        System.out.println();
        System.out.println("----- USUARIOS -----");

        for (Usuario u : gestor.listarUsuarios())
        {
            System.out.println(u);
        }
    }
}

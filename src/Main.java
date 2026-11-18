import Excepciones.ErrorConexionSQLException;
import Gestor.GestorHotel;
import Vista.Menu;

import java.util.Scanner;

/*
 * TP Final - Programación II - UTN TUP
 * Sistema de gestión hotelera
 * Integrantes: Felipe Fernández Barreneche y Santiago Gentilini
 */
public class Main
{
<<<<<<< HEAD
    public static void main(String[] args)
    {
        GestorHotel gestor = new GestorHotel();

        // Antes de mostrar el menú se crean las tablas que falten y se carga lo guardado
        try
        {
            gestor.cargarDatos();
        }
        catch (ErrorConexionSQLException e)
        {
            System.out.println("No se pudo iniciar el sistema: " + e.getMessage());
            return;
        }

        Scanner sc = new Scanner(System.in);
        Menu menu = new Menu(gestor, sc);
        menu.iniciar();
        sc.close();
=======
    try (Scanner sc = new Scanner(System.in))
    {
        GestorHotel gestor = new GestorHotel();
        
        gestor.cargarDatos();
        
        Menu menu = new Menu(gestor, sc);
        menu.iniciar();
    }
    catch (ErrorConexionSQLException e)
    {
        System.out.println("No se pudo iniciar el sistema.");
        System.out.println(e.getMessage());
>>>>>>> 6b2daa87a0e20892962a447fd084c9441f25e43b
    }
}

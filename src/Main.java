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
    }
}
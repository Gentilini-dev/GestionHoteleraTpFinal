import Excepciones.ErrorConexionSQLException;
import Gestor.GestorHotel;
import Vista.Menu;
import java.util.Scanner;

void main()
{
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
    }
}

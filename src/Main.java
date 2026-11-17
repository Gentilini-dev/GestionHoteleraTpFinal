import Gestor.GestorHotel;
import Vista.Menu;
import java.util.Scanner;

void main()
{
    GestorHotel gestor = new GestorHotel();
    Scanner sc = new Scanner(System.in);
    
    Menu menu = new Menu(gestor, sc);
    
    menu.iniciar();
}

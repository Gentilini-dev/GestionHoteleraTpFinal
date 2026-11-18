package Modelos;

import Enums.RolUsuario;
import Vista.Menu;

// Recepcionista: atiende a los pasajeros (reservas, check-in/out, habitaciones y eventos)
public class UsuarioRecepcionista extends Usuario
{
   
    
    public UsuarioRecepcionista(int id, String nombre, String usuario, String password)
    {
        super(id, nombre, usuario,password);
        
    }
    
    @Override
    public void abrirMenu(Menu menu)
    {
        menu.menuRecepcionista();
    }

    @Override
    public RolUsuario getRol()
    {
        return RolUsuario.RECEPCIONISTA;
    }

    @Override
    public String toString()
    {
        return "UsuarioRecepcionista:"+ super.toString();
    }
}

package Modelos;

import java.util.ArrayList;

public class UsuarioRecepcionista extends Usuario
{
   
    
    public UsuarioRecepcionista(int id, String nombre, String usuario, String password)
    {
        super(id, nombre, usuario,password);
        
    }
    
    @Override
    public String toString()
    {
        return "UsuarioRecepcionista:"+ super.toString();
    }
}

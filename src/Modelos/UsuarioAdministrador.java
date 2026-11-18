package Modelos;

import Enums.RolUsuario;
import Vista.Menu;

// Administrador: además de la gestión del hotel, realiza los backups del sistema
public class UsuarioAdministrador extends Usuario
{
    
    public UsuarioAdministrador(int id, String nombre, String usuario, String password)
    {
        super(id, nombre, usuario, password);
    }
    
    @Override
    public void abrirMenu(Menu menu)
    {
        menu.menuAdministrador();
    }

    @Override
    public RolUsuario getRol()
    {
        return RolUsuario.ADMINISTRADOR;
    }

    @Override
    public String toString()
    {
        return "UsuarioAdministrador:"+ super.toString();
    }
}

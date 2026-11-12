package Modelos;

public class UsuarioAdministrador extends Usuario
{
    
    public UsuarioAdministrador(int id, String nombre, String usuario, String password)
    {
        super(id, nombre, usuario, password);
    }
    
    @Override
    public String toString()
    {
        return "UsuarioAdministrador:"+ super.toString();
    }
}

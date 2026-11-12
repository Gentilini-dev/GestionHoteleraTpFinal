package Modelos;

import java.util.Objects;

public abstract class Usuario
{
    private int id;
    private String nombre;
    private String usuario;
    private String password;
    
    public Usuario(int id, String nombre, String usuario, String password)
    {
        this.id = id;
        this.nombre = nombre;
        this.usuario = usuario;
        this.password = password;
    }
    
    public boolean validarUsuario(String usuario, String contrasenia)
    {
        
        if (this.usuario.equals(usuario) && this.password.equals(contrasenia))
        {
            return true;
        }
        
        return false;
        
    }
   
    
    public int getId()
    {
        return id;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    public String getNombre()
    {
        return nombre;
    }
    
    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }
    
    public String getUsuario()
    {
        return usuario;
    }
    
    public void setUsuario(String usuario)
    {
        this.usuario = usuario;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof Usuario usuario))
        {
            return false;
        }
        return id == usuario.id;
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hashCode(id)*22;
    }
    
    @Override
    public String toString()
    {
        return
                "id:" + id +
                ", nombre:'" + nombre + '\'' +
                ", usuario:'" + usuario + '\'' ;
    }
}

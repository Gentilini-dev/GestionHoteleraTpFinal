package Persistencia.JDBC;

import Enums.RolUsuario;
import Excepciones.ErrorConexionSQLException;
import Interfaz.IConexion;
import Modelos.Usuario;
import Modelos.UsuarioAdministrador;
import Modelos.UsuarioRecepcionista;

import java.sql.*;
import java.util.ArrayList;

// Tabla de usuarios del sistema. El rol se guarda como texto y al leer
// se crea el objeto de la subclase que corresponde
public class ConexionUsuario implements IConexion<Usuario>
{
    @Override
    public void crearTabla() throws ErrorConexionSQLException
    {
        try
        {
            Connection con = ConexionManager.getConnection();
            Statement stmt = con.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS usuarios (" +
                    "id INT NOT NULL," +
                    "nombre VARCHAR(50) NOT NULL," +
                    "usuario VARCHAR(30) NOT NULL UNIQUE," +
                    "password VARCHAR(50) NOT NULL," +
                    "rol VARCHAR(20) NOT NULL," +
                    "PRIMARY KEY (id))");
            stmt.close();
            con.close();
        }
        catch (SQLException e)
        {
            throw new ErrorConexionSQLException("No se pudo crear la tabla usuarios.");
        }
    }

    @Override
    public void insertar(Usuario u) throws ErrorConexionSQLException
    {
        try
        {
            Connection con = ConexionManager.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO usuarios (id, nombre, usuario, password, rol) VALUES (?, ?, ?, ?, ?)");
            ps.setInt(1, u.getId());
            ps.setString(2, u.getNombre());
            ps.setString(3, u.getUsuario());
            ps.setString(4, u.getPassword());
            ps.setString(5, u.getRol().name());
            ps.executeUpdate();
            ps.close();
            con.close();
        }
        catch (SQLException e)
        {
            throw new ErrorConexionSQLException("Error al guardar el usuario " + u.getUsuario() + ".");
        }
    }

    @Override
    public void actualizar(Usuario u) throws ErrorConexionSQLException
    {
        try
        {
            Connection con = ConexionManager.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE usuarios SET nombre = ?, usuario = ?, password = ?, rol = ? WHERE id = ?");
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getUsuario());
            ps.setString(3, u.getPassword());
            ps.setString(4, u.getRol().name());
            ps.setInt(5, u.getId());
            ps.executeUpdate();
            ps.close();
            con.close();
        }
        catch (SQLException e)
        {
            throw new ErrorConexionSQLException("Error al actualizar el usuario " + u.getUsuario() + ".");
        }
    }

    @Override
    public void eliminar(int id) throws ErrorConexionSQLException
    {
        try
        {
            Connection con = ConexionManager.getConnection();
            PreparedStatement ps = con.prepareStatement("DELETE FROM usuarios WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
            con.close();
        }
        catch (SQLException e)
        {
            throw new ErrorConexionSQLException("Error al eliminar el usuario con id " + id + ".");
        }
    }

    @Override
    public ArrayList<Usuario> traerTodos() throws ErrorConexionSQLException
    {
        ArrayList<Usuario> usuarios = new ArrayList<>();
        try
        {
            Connection con = ConexionManager.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM usuarios");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String usuario = rs.getString("usuario");
                String password = rs.getString("password");
                RolUsuario rol = RolUsuario.valueOf(rs.getString("rol"));

                if (rol == RolUsuario.ADMINISTRADOR)
                {
                    usuarios.add(new UsuarioAdministrador(id, nombre, usuario, password));
                }
                else
                {
                    usuarios.add(new UsuarioRecepcionista(id, nombre, usuario, password));
                }
            }
            rs.close();
            ps.close();
            con.close();
        }
        catch (SQLException e)
        {
            throw new ErrorConexionSQLException("Error al traer los usuarios de la base de datos.");
        }
        return usuarios;
    }
}

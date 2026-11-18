package JDBC;

import Excepciones.ErrorConexionSQLException;
import Interfaz.IConexion;
import Modelos.Pasajero;

import java.sql.*;
import java.util.ArrayList;

/**
 * Acceso a la tabla "pasajeros" de la base de datos.
 * El DNI es la clave primaria (lo usa la FOREIGN KEY de reservas) y el id es UNIQUE,
 * porque el catálogo del gestor organiza a los pasajeros por id.
 */
public class ConexionPasajero implements IConexion<Pasajero>
{
    @Override
    public void crearTabla() throws ErrorConexionSQLException
    {
        try
        {
            Connection con = ConexionManager.getConnection();
            Statement stmt = con.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS pasajeros (" +
                    "id INT NOT NULL UNIQUE," +
                    "dni VARCHAR(20) NOT NULL," +
                    "nombre VARCHAR(50) NOT NULL," +
                    "apellido VARCHAR(50) NOT NULL," +
                    "origen VARCHAR(50) NOT NULL," +
                    "domicilioOrigen VARCHAR(100) NOT NULL," +
                    "PRIMARY KEY (dni))");
            stmt.close();
            con.close();
        }
        catch (SQLException e)
        {
            throw new ErrorConexionSQLException("No se pudo crear la tabla pasajeros.");
        }
    }

    @Override
    public void insertar(Pasajero p) throws ErrorConexionSQLException
    {
        try
        {
            Connection con = ConexionManager.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO pasajeros (id, dni, nombre, apellido, origen, domicilioOrigen) " +
                    "VALUES (?, ?, ?, ?, ?, ?)");
            ps.setInt(1, p.getId());
            ps.setString(2, p.getDni());
            ps.setString(3, p.getNombre());
            ps.setString(4, p.getApellido());
            ps.setString(5, p.getOrigen());
            ps.setString(6, p.getDomicilioOrigen());
            ps.executeUpdate();
            ps.close();
            con.close();
        }
        catch (SQLException e)
        {
            throw new ErrorConexionSQLException("Error al guardar el pasajero con DNI " + p.getDni() + ".");
        }
    }

    // Actualiza los datos del pasajero. El DNI no se modifica porque es la clave primaria.
    @Override
    public void actualizar(Pasajero p) throws ErrorConexionSQLException
    {
        try
        {
            Connection con = ConexionManager.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE pasajeros SET nombre = ?, apellido = ?, origen = ?, domicilioOrigen = ? " +
                    "WHERE id = ?");
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getApellido());
            ps.setString(3, p.getOrigen());
            ps.setString(4, p.getDomicilioOrigen());
            ps.setInt(5, p.getId());
            ps.executeUpdate();
            ps.close();
            con.close();
        }
        catch (SQLException e)
        {
            throw new ErrorConexionSQLException("Error al actualizar el pasajero con DNI " + p.getDni() + ".");
        }
    }

    @Override
    public void eliminar(int id) throws ErrorConexionSQLException
    {
        try
        {
            Connection con = ConexionManager.getConnection();
            PreparedStatement ps = con.prepareStatement("DELETE FROM pasajeros WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
            con.close();
        }
        catch (SQLException e)
        {
            throw new ErrorConexionSQLException("Error al eliminar el pasajero con id " + id + ".");
        }
    }

    @Override
    public ArrayList<Pasajero> traerTodos() throws ErrorConexionSQLException
    {
        ArrayList<Pasajero> pasajeros = new ArrayList<>();
        try
        {
            Connection con = ConexionManager.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM pasajeros");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                int id = rs.getInt("id");
                String dni = rs.getString("dni");
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");
                String origen = rs.getString("origen");
                String domicilioOrigen = rs.getString("domicilioOrigen");

                // Orden del constructor: nombre, apellido, dni, domicilioOrigen, origen, id
                pasajeros.add(new Pasajero(nombre, apellido, dni, domicilioOrigen, origen, id));
            }
            rs.close();
            ps.close();
            con.close();
        }
        catch (SQLException e)
        {
            throw new ErrorConexionSQLException("Error al traer los pasajeros de la base de datos.");
        }
        return pasajeros;
    }
}

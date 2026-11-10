package JDBC;

import Excepciones.ErrorConexionSQLException;
import Interfaz.IConexion;
import Modelos.Pasajero;

import java.sql.*;
import java.util.ArrayList;

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
                                 "id INT NOT NULL" +
                                 "dni VARCHAR(20) NOT NULL," +
                                 "nombre VARCHAR(50) NOT NULL," +
                                 "apellido VARCHAR(50) NOT NULL," +
                                 "origen VARCHAR(20) NOT NULL," +
                                 "domicilioOrigen VARCHAR(50) NOT NULL" +
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
    public void insertar(Pasajero pasajero) throws ErrorConexionSQLException
    {
        try
        {
            Connection con = ConexionManager.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO pasajeros VALUES(?,?,?,?,?,?) ");
            ps.setInt(1, pasajero.getId());
            ps.setString(2, pasajero.getDni());
            ps.setString(3, pasajero.getNombre());
            ps.setString(4, pasajero.getApellido());
            ps.setString(5, pasajero.getOrigen());
            ps.setString(6, pasajero.getDomicilioOrigen());
            ps.execute();
            ps.close();
            con.close();
            
        }
        catch (Exception e)
        {
            throw new ErrorConexionSQLException("Error al insertar el pasajero.");
        }
    }
    
    @Override
    public void actualizar(Pasajero pasajero) throws ErrorConexionSQLException
    {
        try
        {
            Connection con = ConexionManager.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE Pasajeros set id=?;dni =?,nombre =?,apellido=?,origen=?,domicilioOrigen=?");
            ps.setInt(1, pasajero.getId());
            ps.setString(2, pasajero.getDni());
            ps.setString(3, pasajero.getNombre());
            ps.setString(4, pasajero.getApellido());
            ps.setString(5, pasajero.getOrigen());
            ps.setString(6, pasajero.getDomicilioOrigen());
            ps.executeUpdate();
            ps.close();
            con.close();
        }
        catch (SQLException e)
        {
            throw new ErrorConexionSQLException("Error al actualizar la tabla pasajero. ");
        }
    }
    
    @Override
    public void eliminar(int id) throws ErrorConexionSQLException
    {
        try
        {
            Connection con = ConexionManager.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM pasajeros WHERE id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
            con.close();
        }
        catch (Exception e)
        {
            throw new ErrorConexionSQLException("Error al eliminar el pasajero");
        }
    }
    
    @Override
    public ArrayList<Pasajero> traerTodos() throws ErrorConexionSQLException
    {
        ArrayList<Pasajero> pasajeros = new ArrayList<>();
        try
        {
            Connection con = ConexionManager.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT  * FROM salones");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");
                String dni = rs.getString("dni");
                String origen = rs.getString("origen");
                String domicilioOrigen = rs.getString("domicilioOrigen");
                
                pasajeros.add(new Pasajero(nombre, apellido, dni, origen, domicilioOrigen));
            }
            ps.executeUpdate();
            ps.close();
            con.close();
        }
        catch (SQLException e)
        {
            throw new ErrorConexionSQLException("Error al traer los pasajeros");
        }
        return pasajeros;
    }
}

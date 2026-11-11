package JDBC;

import Enums.TipoHabitacion;
import Excepciones.ErrorConexionSQLException;
import Interfaz.IConexion;
import Modelos.Habitacion;

import java.sql.*;
import java.util.ArrayList;

public class ConexionHabitacion implements IConexion<Habitacion>
{
    @Override
    public void crearTabla() throws ErrorConexionSQLException
    {
        try
        {
            Connection con = ConexionManager.getConnection();
            Statement stmt = con.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS habitaciones ("
                                 + "numeroHabitacion INT NOT NULL,"
                                 + "piso INT NOT NULL,"
                                 + "tipoHabitacion VARCHAR(20) NOT NULL,"
                                 + "PRIMARY KEY (numeroHabitacion))");
            
            stmt.close();
            con.close();
        }
        catch (SQLException e)
        {
            throw new ErrorConexionSQLException("Error al crear la tabla habitaciones. ");
        }
    }
    
    @Override
    public void insertar(Habitacion habitacion) throws ErrorConexionSQLException
    {
        try
        {
            Connection con = ConexionManager.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO habitaciones "
                            + "(numeroHabitacion, piso, tipoHabitacion) "
                            + "VALUES (?, ?, ?)"
                                                       );
            
            ps.setInt(1, habitacion.getNumeroHabitacion());
            ps.setInt(2, habitacion.getPiso());
            ps.setString(3, habitacion.getTipoHabitacion().name());
            
            ps.executeUpdate();
            ps.close();
            con.close();
        }
        catch (SQLException e)
        {
            throw new ErrorConexionSQLException("Error al insertar la habitación.");
        }
    }
    
    @Override
    public void actualizar(Habitacion habitacion) throws ErrorConexionSQLException
    {
        try
        {
            Connection con = ConexionManager.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE habitaciones "
                            + "SET piso = ?, tipoHabitacion = ? "
                            + "WHERE numeroHabitacion = ?"
                                                       );
            
            ps.setInt(1, habitacion.getPiso());
            ps.setString(2, habitacion.getTipoHabitacion().name());
            ps.setInt(3, habitacion.getNumeroHabitacion());
            
            ps.executeUpdate();
            ps.close();
            con.close();
        }
        catch (SQLException e)
        {
            throw new ErrorConexionSQLException("Error al actualizar la habitación.");
        }
    }
    
    @Override
    public void eliminar(int numeroHabitacion) throws ErrorConexionSQLException
    {
        try
        {
            Connection con = ConexionManager.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM habitaciones WHERE numeroHabitacion = ?"
                                                       );
            
            ps.setInt(1, numeroHabitacion);
            
            ps.executeUpdate();
            ps.close();
            con.close();
        }
        catch (SQLException e)
        {
            throw new ErrorConexionSQLException("Error al eliminar la habitación.");
        }
    }
    
    @Override
    public ArrayList<Habitacion> traerTodos() throws ErrorConexionSQLException
    {
        ArrayList<Habitacion> habitaciones = new ArrayList<>();
        
        try
        {
            Connection con = ConexionManager.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM habitaciones");
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next())
            {
                int numeroHabitacion = rs.getInt("numeroHabitacion");
                
                int piso = rs.getInt("piso");
                
                TipoHabitacion tipoHabitacion = TipoHabitacion.valueOf(rs.getString("tipoHabitacion"));
                
                Habitacion habitacion = new Habitacion(numeroHabitacion, piso, tipoHabitacion);
                
                habitaciones.add(habitacion);
            }
            
            rs.close();
            ps.close();
            con.close();
        }
        catch (SQLException e)
        {
            throw new ErrorConexionSQLException("Error al traer las habitaciones.");
        }
        
        return habitaciones;
    }
}


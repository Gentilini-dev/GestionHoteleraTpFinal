package JDBC;

import Enums.MotivoBloqueo;
import Modelos.BloqueoHabitacion;
import Modelos.Habitacion;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import Interfaz.IConexion;
import Excepciones.*;

public class ConexionBloqueoHabitacion implements IConexion<BloqueoHabitacion>
{
    // Habitaciones ya cargadas (clave: número de habitación), para reconstruir el objeto
    private HashMap<Integer, Habitacion> habitaciones;

    public ConexionBloqueoHabitacion(HashMap<Integer, Habitacion> habitaciones)
    {
        this.habitaciones = habitaciones;
    }

    @Override
    public void crearTabla() throws ErrorConexionSQLException
    {
        try
        {
            Connection con = ConexionManager.getConnection();
            Statement stmt = con.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS bloqueos (" +
                    "id INT NOT NULL," +
                    "numero_habitacion INT NOT NULL," +
                    "motivo VARCHAR(20) NOT NULL," +
                    "detalle VARCHAR(100)," +
                    "desde DATE NOT NULL," +
                    "hasta DATE NOT NULL," +
                    "PRIMARY KEY (id))");
            stmt.close();
            con.close();
        }
        catch (SQLException e)
        {
            throw new ErrorConexionSQLException("No se pudo crear la tabla bloqueos.");
        }
    }

    @Override
    public void insertar(BloqueoHabitacion b) throws ErrorConexionSQLException
    {
        try
        {
            Connection con = ConexionManager.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO bloqueos VALUES (?,?,?,?,?,?)");
            ps.setInt(1, b.getId());
            ps.setInt(2, b.getHabitacion().getNumeroHabitacion());
            ps.setString(3, b.getMotivo().name());
            ps.setString(4, b.getDetalle());
            ps.setDate(5, Date.valueOf(b.getDesde()));
            ps.setDate(6, Date.valueOf(b.getHasta()));
            ps.executeUpdate();
            ps.close();
            con.close();
        }
        catch (SQLException e)
        {
            throw new ErrorConexionSQLException("Error al insertar el bloqueo.");
        }
    }

    @Override
    public ArrayList<BloqueoHabitacion> traerTodos() throws ErrorConexionSQLException
    {
        ArrayList<BloqueoHabitacion> bloqueos = new ArrayList<>();
        try
        {
            Connection con = ConexionManager.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM bloqueos");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                int id = rs.getInt("id");
                Habitacion habitacion = habitaciones.get(rs.getInt("numero_habitacion"));
                MotivoBloqueo motivo = MotivoBloqueo.valueOf(rs.getString("motivo"));
                String detalle = rs.getString("detalle");
                LocalDate desde = rs.getDate("desde").toLocalDate();
                LocalDate hasta = rs.getDate("hasta").toLocalDate();
                bloqueos.add(new BloqueoHabitacion(id, habitacion, motivo, detalle, desde, hasta));
            }
            rs.close();
            ps.close();
            con.close();
        }
        catch (SQLException e)
        {
            throw new ErrorConexionSQLException("Error al traer los bloqueos.");
        }
        return bloqueos;
    }

    @Override
    public void actualizar(BloqueoHabitacion b) throws ErrorConexionSQLException
    {
        try
        {
            Connection con = ConexionManager.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE bloqueos SET numero_habitacion = ?, motivo = ?, detalle = ?, " +
                            "desde = ?, hasta = ? WHERE id = ?");
            ps.setInt(1, b.getHabitacion().getNumeroHabitacion());
            ps.setString(2, b.getMotivo().name());
            ps.setString(3, b.getDetalle());
            ps.setDate(4, Date.valueOf(b.getDesde()));
            ps.setDate(5, Date.valueOf(b.getHasta()));
            ps.setInt(6, b.getId());
            ps.executeUpdate();
            ps.close();
            con.close();
        }
        catch (SQLException e)
        {
            throw new ErrorConexionSQLException("Error al actualizar el bloqueo.");
        }
    }

    @Override
    public void eliminar(int id) throws ErrorConexionSQLException
    {
        try
        {
            Connection con = ConexionManager.getConnection();
            PreparedStatement ps = con.prepareStatement("DELETE FROM bloqueos WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
            con.close();
        }
        catch (SQLException e)
        {
            throw new ErrorConexionSQLException("Error al eliminar el bloqueo.");
        }
    }
}

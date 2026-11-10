package JDBC;

import Enums.TipoSalon;
import Excepciones.ErrorConexionSQLException;
import Modelos.Salon;
import java.sql.*;
import java.util.ArrayList;

public class ConexionSalon implements IConexion<Salon>
{
    @Override
    public void crearTabla() throws ErrorConexionSQLException
    {
        try
        {
            Connection con = ConexionManager.getConnection();
            Statement stmt = con.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS salones (" +
                    "id INT NOT NULL," +
                    "nombre VARCHAR(50) NOT NULL," +
                    "capacidad INT NOT NULL," +
                    "tipo VARCHAR(20) NOT NULL," +
                    "PRIMARY KEY (id))");
            stmt.close();
            con.close();
        }
        catch (SQLException e)
        {
            throw new ErrorConexionSQLException("No se pudo crear la tabla salones.");
        }
    }


    @Override
    public void insertar(Salon s) throws ErrorConexionSQLException
    {
        try
        {
            Connection con = ConexionManager.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO salones VALUES (?,?,?,?)");
            ps.setInt(1, s.getId());
            ps.setString(2, s.getNombre());
            ps.setInt(3, s.getCapacidad());
            ps.setString(4, s.getTipo().name());
            ps.execute();
            ps.close();
            con.close();
        }
        catch (SQLException e)
        {
            throw new ErrorConexionSQLException("Error al insertar el salón.");
        }
    }

    @Override
    public ArrayList<Salon> traerTodos() throws ErrorConexionSQLException
    {
        Connection con = ConexionManager.getConnection();
        ArrayList<Salon> salones = new ArrayList<>();
        try
        {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM salones");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                int capacidad = rs.getInt("capacidad");
                TipoSalon tipo = TipoSalon.valueOf(rs.getString("tipo"));   // texto -> enum
                salones.add(new Salon(id, nombre, capacidad, tipo));
            }
            rs.close();
            ps.close();
            con.close();
        }
        catch (SQLException e)
        {
            throw new ErrorConexionSQLException("Error al traer los salones.");
        }
        return salones;
    }
}

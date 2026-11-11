package JDBC;

import Enums.EstadoEvento;
import Interfaz.IConexion;
import Modelos.Evento;
import Modelos.Salon;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import Excepciones.*;

public class ConexionEvento implements IConexion<Evento>
{
    // Salones ya cargados, para reconstruir el objeto Salon a partir del id guardado en la tabla
    private HashMap<Integer, Salon> salones;

    public ConexionEvento(HashMap<Integer, Salon> salones)
    {
        this.salones = salones;
    }

    @Override
    public void crearTabla() throws ErrorConexionSQLException
    {
        try
        {
            Connection con = ConexionManager.getConnection();
            Statement stmt = con.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS eventos (" +
                    "id INT NOT NULL," +
                    "id_salon INT NOT NULL," +
                    "organizador VARCHAR(50) NOT NULL," +
                    "fecha DATE NOT NULL," +
                    "asistentes INT NOT NULL," +
                    "estado VARCHAR(20) NOT NULL," +
                    "PRIMARY KEY (id)," +
                    "FOREIGN KEY (id_salon) REFERENCES salones(id))");
            stmt.close();
            con.close();
        }
        catch (SQLException e)
        {
            throw new ErrorConexionSQLException("No se pudo crear la tabla eventos.");
        }
    }

    @Override
    public void insertar(Evento e) throws ErrorConexionSQLException
    {
        try
        {
            Connection con = ConexionManager.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO eventos VALUES (?,?,?,?,?,?)");
            ps.setInt(1, e.getId());
            ps.setInt(2, e.getSalon().getId());
            ps.setString(3, e.getOrganizador());
            ps.setDate(4, Date.valueOf(e.getFecha()));
            ps.setInt(5, e.getAsistentes());
            ps.setString(6, e.getEstado().name());
            ps.executeUpdate();
            ps.close();
            con.close();
        }
        catch (SQLException ex)
        {
            throw new ErrorConexionSQLException("Error al insertar el evento.");
        }
    }

    @Override
    public ArrayList<Evento> traerTodos() throws ErrorConexionSQLException
    {
        ArrayList<Evento> eventos = new ArrayList<>();
        try
        {
            Connection con = ConexionManager.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM eventos");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                int id = rs.getInt("id");
                Salon salon = salones.get(rs.getInt("id_salon"));
                String organizador = rs.getString("organizador");
                LocalDate fecha = rs.getDate("fecha").toLocalDate();
                int asistentes = rs.getInt("asistentes");
                EstadoEvento estado = EstadoEvento.valueOf(rs.getString("estado"));
                eventos.add(new Evento(id, salon, organizador, fecha, asistentes, estado));
            }
            rs.close();
            ps.close();
            con.close();
        }
        catch (SQLException ex)
        {
            throw new ErrorConexionSQLException("Error al traer los eventos.");
        }
        return eventos;
    }

    @Override
    public void actualizar(Evento e) throws ErrorConexionSQLException
    {
        try
        {
            Connection con = ConexionManager.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE eventos SET id_salon = ?, organizador = ?, fecha = ?, " +
                            "asistentes = ?, estado = ? WHERE id = ?");
            ps.setInt(1, e.getSalon().getId());
            ps.setString(2, e.getOrganizador());
            ps.setDate(3, Date.valueOf(e.getFecha()));
            ps.setInt(4, e.getAsistentes());
            ps.setString(5, e.getEstado().name());
            ps.setInt(6, e.getId());
            ps.executeUpdate();
            ps.close();
            con.close();
        }
        catch (SQLException ex)
        {
            throw new ErrorConexionSQLException("Error al actualizar el evento.");
        }
    }

    @Override
    public void eliminar(int id) throws ErrorConexionSQLException
    {
        try
        {
            Connection con = ConexionManager.getConnection();
            PreparedStatement ps = con.prepareStatement("DELETE FROM eventos WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
            con.close();
        }
        catch (SQLException ex)
        {
            throw new ErrorConexionSQLException("Error al eliminar el evento.");
        }
    }
}

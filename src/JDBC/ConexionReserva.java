package JDBC;

import Enums.EstadoReserva;
import Enums.TipoHabitacion;
import Excepciones.ErrorConexionSQLException;
import Interfaz.IConexion;
import Modelos.Habitacion;
import Modelos.Pasajero;
import Modelos.Reserva;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class ConexionReserva implements IConexion<Reserva>
{
    @Override
    public void crearTabla() throws ErrorConexionSQLException
    {
        try
        {
            Connection con = ConexionManager.getConnection();
            Statement stmt = con.createStatement();
            
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS reservas ("
                            + "id INT NOT NULL,"
                            + "dniPasajero VARCHAR(20) NOT NULL,"
                            + "numeroHabitacion INT NOT NULL,"
                            + "desde DATE NOT NULL,"
                            + "hasta DATE NOT NULL,"
                            + "estado VARCHAR(20) NOT NULL,"
                            + "PRIMARY KEY (id),"
                            + "FOREIGN KEY (dniPasajero) "
                            + "REFERENCES pasajeros(dni),"
                            + "FOREIGN KEY (numeroHabitacion) "
                            + "REFERENCES habitaciones(numeroHabitacion))"
                        );
            
            stmt.close();
            con.close();
        }
        catch (SQLException e)
        {
            throw new ErrorConexionSQLException("No se pudo crear la tabla reservas.");
        }
    }
    
    @Override
    public void insertar(Reserva reserva) throws ErrorConexionSQLException
    {
        try
        {
            Connection con = ConexionManager.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO reservas "
                            + "(id, dniPasajero, numeroHabitacion, desde, hasta, estado) "
                            + "VALUES (?, ?, ?, ?, ?, ?)"
                                                       );
            
            ps.setInt(1, reserva.getId());
            ps.setString(2, reserva.getTitular().getDni());
            ps.setInt(3, reserva.getHabitacion().getNumeroHabitacion());
            ps.setDate(4, Date.valueOf(reserva.getDesde()));
            ps.setDate(5, Date.valueOf(reserva.getHasta()));
            ps.setString(6, reserva.getEstado().name());
            
            ps.executeUpdate();
            ps.close();
            con.close();
        }
        catch (SQLException e)
        {
            throw new ErrorConexionSQLException("Error al insertar la reserva.");
        }
    }
    
    @Override
    public void actualizar(Reserva reserva) throws ErrorConexionSQLException
    {
        try
        {
            Connection con = ConexionManager.getConnection();
            
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE reservas "
                            + "SET dniPasajero = ?, "
                            + "numeroHabitacion = ?, "
                            + "desde = ?, "
                            + "hasta = ?, "
                            + "estado = ? "
                            + "WHERE id = ?"
                                                       );
            
            ps.setString(1, reserva.getTitular().getDni());
            ps.setInt(2, reserva.getHabitacion().getNumeroHabitacion());
            ps.setDate(3, Date.valueOf(reserva.getDesde()));
            ps.setDate(4, Date.valueOf(reserva.getHasta()));
            ps.setString(5, reserva.getEstado().name());
            ps.setInt(6, reserva.getId());
            
            ps.executeUpdate();
            ps.close();
            con.close();
        }
        catch (SQLException e)
        {
            throw new ErrorConexionSQLException("Error al actualizar la reserva.");
        }
    }
    
    @Override
    public void eliminar(int id) throws ErrorConexionSQLException
    {
        try
        {
            Connection con = ConexionManager.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM reservas WHERE id = ?"
                                                       );
            
            ps.setInt(1, id);
            
            ps.executeUpdate();
            ps.close();
            con.close();
        }
        catch (SQLException e)
        {
            throw new ErrorConexionSQLException("Error al eliminar la reserva.");
        }
        
    }
    
    @Override
    public ArrayList<Reserva> traerTodos() throws ErrorConexionSQLException
    {
        ArrayList<Reserva> reservas = new ArrayList<>();
        
        try
        {
            Connection con = ConexionManager.getConnection();
            
            PreparedStatement ps = con.prepareStatement(
                    "SELECT "
                            + "r.id AS idReserva, "
                            + "r.desde, "
                            + "r.hasta, "
                            + "r.estado, "
                            + "p.id AS idPasajero, "
                            + "p.nombre, "
                            + "p.apellido, "
                            + "p.dni, "
                            + "p.origen, "
                            + "p.domicilioOrigen, "
                            + "h.numeroHabitacion, "
                            + "h.piso, "
                            + "h.tipoHabitacion "
                            + "FROM reservas r "
                            + "JOIN pasajeros p "
                            + "ON r.dniPasajero = p.dni "
                            + "JOIN habitaciones h "
                            + "ON r.numeroHabitacion = h.numeroHabitacion"
                                                       );
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next())
            {
                
                int idPasajero = rs.getInt("idPasajero");
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");
                String dni = rs.getString("dni");
                String origen = rs.getString("origen");
                String domicilioOrigen = rs.getString("domicilioOrigen");
                
                Pasajero pasajero = new Pasajero(nombre, apellido, dni, origen, domicilioOrigen, idPasajero);
                
                int numeroHabitacion = rs.getInt("numeroHabitacion");
                int piso = rs.getInt("piso");
                
                TipoHabitacion tipoHabitacion = TipoHabitacion.valueOf(rs.getString("tipoHabitacion"));
                
                Habitacion habitacion = new Habitacion(numeroHabitacion, piso, tipoHabitacion);
                
                
                int idReserva = rs.getInt("idReserva");
                LocalDate desde = rs.getDate("desde").toLocalDate();
                LocalDate hasta = rs.getDate("hasta").toLocalDate();
                EstadoReserva estado = EstadoReserva.valueOf(rs.getString("estado"));
                
                Reserva reserva = new Reserva(idReserva, pasajero, habitacion, desde, hasta, estado);
                reservas.add(reserva);
            }
            rs.close();
            ps.close();
            con.close();
        }
        catch (SQLException e)
        {
            throw new ErrorConexionSQLException("Error al traer las reservas.");
        }
        return reservas;
    }
}

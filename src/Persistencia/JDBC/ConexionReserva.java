package Persistencia.JDBC;

import Enums.EstadoReserva;
import Excepciones.ErrorConexionSQLException;
import Interfaz.IConexion;
import Modelos.Habitacion;
import Modelos.Pasajero;
import Modelos.Reserva;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

// Tabla reservas
public class ConexionReserva implements IConexion<Reserva>
{
    // Pasajeros y habitaciones que ya cargó el gestor, así cada reserva usa esos mismos objetos
    private HashMap<Integer, Pasajero> pasajeros;
    private HashMap<Integer, Habitacion> habitaciones;

    public ConexionReserva(HashMap<Integer, Pasajero> pasajeros, HashMap<Integer, Habitacion> habitaciones)
    {
        this.pasajeros = pasajeros;
        this.habitaciones = habitaciones;
    }

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
            // La tabla guarda el DNI del titular, con el JOIN sacamos su id para buscarlo en el catálogo
            PreparedStatement ps = con.prepareStatement(
                    "SELECT r.id, r.numeroHabitacion, r.desde, r.hasta, r.estado, p.id AS idPasajero "
                            + "FROM reservas r "
                            + "JOIN pasajeros p ON r.dniPasajero = p.dni");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                int idReserva = rs.getInt("id");
                Pasajero titular = pasajeros.get(rs.getInt("idPasajero"));             // id -> objeto
                Habitacion habitacion = habitaciones.get(rs.getInt("numeroHabitacion")); // número -> objeto
                LocalDate desde = rs.getDate("desde").toLocalDate();
                LocalDate hasta = rs.getDate("hasta").toLocalDate();
                EstadoReserva estado = EstadoReserva.valueOf(rs.getString("estado"));

                reservas.add(new Reserva(idReserva, titular, habitacion, desde, hasta, estado));
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

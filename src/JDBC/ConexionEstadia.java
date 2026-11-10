package JDBC;

import Enums.EstadoEstadia;
import Modelos.Estadia;
import Modelos.Habitacion;
import Modelos.Pasajero;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import Excepciones.*;
import Interfaz.*;

/*
 * Una estadía tiene varios ocupantes y un pasajero puede tener varias estadías,
 * así que se usan dos tablas:
 *   estadias           -> los datos de la estadía
 *   estadia_ocupantes  -> qué pasajeros estuvieron en cada estadía (id_estadia, id_pasajero)
 */
public class ConexionEstadia implements IConexion<Estadia>
{
    private HashMap<Integer, Habitacion> habitaciones;   // clave: número de habitación
    private HashMap<Integer, Pasajero> pasajeros;        // clave: id del pasajero

    public ConexionEstadia(HashMap<Integer, Habitacion> habitaciones,
                           HashMap<Integer, Pasajero> pasajeros)
    {
        this.habitaciones = habitaciones;
        this.pasajeros = pasajeros;
    }

    @Override
    public void crearTabla() throws ErrorConexionSQLException
    {
        try
        {
            Connection con = ConexionManager.getConnection();
            Statement stmt = con.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS estadias (" +
                    "id INT NOT NULL," +
                    "numero_habitacion INT NOT NULL," +
                    "fecha_ingreso DATE NOT NULL," +
                    "fecha_egreso DATE," +                    // puede ser NULL (estadía activa)
                    "estado VARCHAR(20) NOT NULL," +
                    "PRIMARY KEY (id))");
            stmt.execute("CREATE TABLE IF NOT EXISTS estadia_ocupantes (" +
                    "id_estadia INT NOT NULL," +
                    "id_pasajero INT NOT NULL," +
                    "PRIMARY KEY (id_estadia, id_pasajero)," +
                    "FOREIGN KEY (id_estadia) REFERENCES estadias(id))");
            stmt.close();
            con.close();
        }
        catch (SQLException e)
        {
            throw new ErrorConexionSQLException("No se pudieron crear las tablas de estadías.");
        }
    }

    @Override
    public void insertar(Estadia e) throws ErrorConexionSQLException
    {
        try
        {
            Connection con = ConexionManager.getConnection();

            // 1) La estadía
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO estadias VALUES (?,?,?,?,?)");
            ps.setInt(1, e.getId());
            ps.setInt(2, e.getHabitacion().getNumeroHabitacion());
            ps.setDate(3, Date.valueOf(e.getFechaIngreso()));
            if (e.getFechaEgreso() != null)
            {
                ps.setDate(4, Date.valueOf(e.getFechaEgreso()));
            }
            else
            {
                ps.setNull(4, Types.DATE);
            }
            ps.setString(5, e.getEstado().name());
            ps.executeUpdate();
            ps.close();

            // 2) Un registro por cada ocupante
            PreparedStatement psOcupante = con.prepareStatement(
                    "INSERT INTO estadia_ocupantes VALUES (?,?)");
            for (Pasajero p : e.getOcupantes())
            {
                psOcupante.setInt(1, e.getId());
                psOcupante.setInt(2, p.getId());
                psOcupante.executeUpdate();
            }
            psOcupante.close();
            con.close();
        }
        catch (SQLException ex)
        {
            throw new ErrorConexionSQLException("Error al insertar la estadía.");
        }
    }

    @Override
    public ArrayList<Estadia> traerTodos() throws ErrorConexionSQLException
    {
        ArrayList<Estadia> estadias = new ArrayList<>();
        HashMap<Integer, Estadia> porId = new HashMap<>();   // para ubicar rápido cada estadía en el paso 2
        try
        {
            Connection con = ConexionManager.getConnection();

            // 1) Las estadías, todavía sin ocupantes
            PreparedStatement ps = con.prepareStatement("SELECT * FROM estadias");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                int id = rs.getInt("id");
                Habitacion habitacion = habitaciones.get(rs.getInt("numero_habitacion"));
                LocalDate ingreso = rs.getDate("fecha_ingreso").toLocalDate();
                LocalDate egreso = null;
                Date fechaEgreso = rs.getDate("fecha_egreso");
                if (fechaEgreso != null)
                {
                    egreso = fechaEgreso.toLocalDate();
                }
                EstadoEstadia estado = EstadoEstadia.valueOf(rs.getString("estado"));

                Estadia e = new Estadia(id, habitacion, ingreso, egreso, estado);
                estadias.add(e);
                porId.put(id, e);
            }
            rs.close();
            ps.close();

            // 2) Los ocupantes, cada uno a su estadía
            PreparedStatement psOcupantes = con.prepareStatement("SELECT * FROM estadia_ocupantes");
            ResultSet rsOcupantes = psOcupantes.executeQuery();
            while (rsOcupantes.next())
            {
                Estadia e = porId.get(rsOcupantes.getInt("id_estadia"));
                Pasajero p = pasajeros.get(rsOcupantes.getInt("id_pasajero"));
                e.agregarOcupante(p);
            }
            rsOcupantes.close();
            psOcupantes.close();
            con.close();
        }
        catch (SQLException ex)
        {
            throw new ErrorConexionSQLException("Error al traer las estadías.");
        }
        return estadias;
    }

    // Actualiza los datos de la estadía (se usa en el check-out). Los ocupantes no cambian.
    @Override
    public void actualizar(Estadia e) throws ErrorConexionSQLException
    {
        try
        {
            Connection con = ConexionManager.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE estadias SET numero_habitacion = ?, fecha_ingreso = ?, " +
                            "fecha_egreso = ?, estado = ? WHERE id = ?");
            ps.setInt(1, e.getHabitacion().getNumeroHabitacion());
            ps.setDate(2, Date.valueOf(e.getFechaIngreso()));
            if (e.getFechaEgreso() != null)
            {
                ps.setDate(3, Date.valueOf(e.getFechaEgreso()));
            }
            else
            {
                ps.setNull(3, Types.DATE);
            }
            ps.setString(4, e.getEstado().name());
            ps.setInt(5, e.getId());
            ps.executeUpdate();
            ps.close();
            con.close();
        }
        catch (SQLException ex)
        {
            throw new ErrorConexionSQLException("Error al actualizar la estadía.");
        }
    }

    // Primero se borran los ocupantes y después la estadía (por la FOREIGN KEY)
    @Override
    public void eliminar(int id) throws ErrorConexionSQLException
    {
        try
        {
            Connection con = ConexionManager.getConnection();
            PreparedStatement psOcupantes = con.prepareStatement(
                    "DELETE FROM estadia_ocupantes WHERE id_estadia = ?");
            psOcupantes.setInt(1, id);
            psOcupantes.executeUpdate();
            psOcupantes.close();

            PreparedStatement ps = con.prepareStatement("DELETE FROM estadias WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
            con.close();
        }
        catch (SQLException ex)
        {
            throw new ErrorConexionSQLException("Error al eliminar la estadía.");
        }
    }
}

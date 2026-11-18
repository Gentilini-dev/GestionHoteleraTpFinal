package Persistencia.JDBC;

import Excepciones.ErrorConexionSQLException;

import java.sql.*;

// Datos de la base. Si se cambia de base solo hay que tocar estas tres líneas
public class ConexionManager
{
    private static String url = "jdbc:mysql://localhost:3306/hotel";
    private static String username = "root";
    private static String password = "456311";
    
    private static Connection connection;
    
    // Abre una conexión nueva cada vez. Cada DAO la cierra cuando termina
    public static Connection getConnection()throws ErrorConexionSQLException
    {
        try
        {
            connection = DriverManager.getConnection(url, username, password);
        }
        catch (SQLException e)
        {
            throw new ErrorConexionSQLException();
        }
        return connection;
    }

    // Por ahora no se usa, cada DAO cierra su propia conexión
    public static void closeConnection()
    {
        try
        {
            connection.close();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
}

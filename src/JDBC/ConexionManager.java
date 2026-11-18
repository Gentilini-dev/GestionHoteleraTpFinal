package JDBC;

import Excepciones.ErrorConexionSQLException;

import java.sql.*;

public class ConexionManager
{
    private static String url = "jdbc:mysql://localhost:3306/hotel";
    private static String username = "root";
    private static String password = "456311";
    
    private static Connection connection;
    
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

package JDBC;

import Excepciones.ErrorConexionSQLException;

import java.sql.*;

public class ConexionManager
{
    private static String url="jdbc:mysql://172.16.1.64:3306/ProyectoFinalFERNANDEZ_GENTILINI";
    private static String username = "bdd1";
    private static String password = "bdd1";
    
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

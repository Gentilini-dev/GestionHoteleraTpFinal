package Excepciones;

public class ErrorConexionSQLException extends Exception
{
    public ErrorConexionSQLException()
    {
        super("Error al crear la conexion");
    }
}

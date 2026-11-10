package Excepciones;

public class ErrorConexionSQLException extends Exception
{
    public ErrorConexionSQLException()
    {
        super("No se pudo conectar a la base de datos.");
    }

    public ErrorConexionSQLException(String mensaje)
    {
        super(mensaje);
    }
}

package Excepciones;

// Envuelve los errores de SQL, así los DAOs no imprimen nada y el mensaje lo muestra el menú
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

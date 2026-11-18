package Excepciones;

// Cuando falla escribir o leer el backup. Reemplaza a la IOException con un mensaje nuestro
public class ErrorArchivoException extends Exception
{
    public ErrorArchivoException(String mensaje)
    {
        super(mensaje);
    }
}

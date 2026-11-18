package Excepciones;

/**
 * Se lanza cuando falla la lectura o escritura de un archivo (por ejemplo, el backup).
 * Encapsula la IOException de Java con un mensaje propio.
 */
public class ErrorArchivoException extends Exception
{
    public ErrorArchivoException(String mensaje)
    {
        super(mensaje);
    }
}

package Excepciones;

// Para datos mal cargados: fechas pasadas, cantidades en 0, estados que no corresponden, etc.
public class DatosInvalidosException extends Exception {
    public DatosInvalidosException(String mensaje)
    {
        super(mensaje);
    }
}

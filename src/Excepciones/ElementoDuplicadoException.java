package Excepciones;

// Cuando se quiere agregar algo que ya existe (mismo id, DNI o usuario)
public class ElementoDuplicadoException extends Exception {
    public ElementoDuplicadoException(int id)
    {
        super("Ya existe un elemento con id " + id + ".");
    }

    public ElementoDuplicadoException(String mensaje)
    {
        super(mensaje);
    }
}

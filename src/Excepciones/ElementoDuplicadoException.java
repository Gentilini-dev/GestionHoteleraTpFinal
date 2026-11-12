package Excepciones;

public class ElementoDuplicadoException extends Exception {
    public ElementoDuplicadoException(int id)
    {
        super("Ya existe un elemento con id " + id + ".");
    }
}

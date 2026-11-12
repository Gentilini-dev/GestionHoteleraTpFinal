package Excepciones;

public class ElementoNoEncontradoException extends Exception {
    public ElementoNoEncontradoException(int id)
    {
        super("No se encontró ningún elemento con id " + id + ".");
    }

    public ElementoNoEncontradoException(String message){
        super(message);
    }
}

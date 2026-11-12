package Colecciones;

import Excepciones.ElementoDuplicadoException;
import Excepciones.ElementoNoEncontradoException;
import Interfaz.IIdentificable;
import java.util.ArrayList;
import java.util.HashMap;

public class Catalogo<T extends IIdentificable>
{
    private HashMap<Integer, T> elementos;

    public Catalogo()
    {
        elementos = new HashMap<>();
    }

    // El id se obtiene del propio elemento, así siempre coincide
    public void agregar(T elemento) throws ElementoDuplicadoException
    {
        if (elementos.containsKey(elemento.getId()))
        {
            throw new ElementoDuplicadoException(elemento.getId());
        }
        elementos.put(elemento.getId(), elemento);
    }

    public T buscar(int id) throws ElementoNoEncontradoException
    {
        if (!elementos.containsKey(id))
        {
            throw new ElementoNoEncontradoException(id);
        }
        return elementos.get(id);
    }

    public void eliminar(int id) throws ElementoNoEncontradoException
    {
        if (!elementos.containsKey(id))
        {
            throw new ElementoNoEncontradoException(id);
        }
        elementos.remove(id);
    }

    public boolean existe(int id)
    {
        return elementos.containsKey(id);
    }

    public ArrayList<T> listar()
    {
        return new ArrayList<>(elementos.values());
    }
}

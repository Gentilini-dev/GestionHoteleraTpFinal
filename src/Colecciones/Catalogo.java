package Colecciones;

import Excepciones.ElementoDuplicadoException;
import Excepciones.ElementoNoEncontradoException;
import Interfaz.IIdentificable;
import java.util.ArrayList;
import java.util.HashMap;

// Clase genérica: guarda elementos en un HashMap usando su id como clave.
// La usamos para usuarios, pasajeros, habitaciones y salones
public class Catalogo<T extends IIdentificable>
{
    private HashMap<Integer, T> elementos;

    public Catalogo()
    {
        elementos = new HashMap<>();
    }

    // El id lo sacamos del elemento, así nunca queda guardado con otra clave
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

    // Lo usan los DAOs para encontrar los objetos ya cargados cuando leen de la base
    // (por ejemplo, la habitación de una reserva)
    public HashMap<Integer, T> getMapa()
    {
        return elementos;
    }

    public ArrayList<T> listar()
    {
        return new ArrayList<>(elementos.values());
    }
}

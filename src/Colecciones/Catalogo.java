package Colecciones;

import java.util.ArrayList;
import java.util.HashMap;

public class Catalogo<T>
{
    HashMap<Integer, T> Elementos;
    
    public Catalogo()
    {
        Elementos = new HashMap<>();
    }
    
    public void agregar(int id, T t)
    {
        Elementos.put(id, t);
    }
    
    public T buscar(int id)
    {
        return Elementos.get(id);
    }
    
    public void eliminar(int id)
    {
        Elementos.remove(id);
    }
    
    public boolean existe(int id)
    {
        if (Elementos.containsKey(id))
        {
            return true;
        }
        return false;
    }
    
    public ArrayList<T> listar()
    {
        ArrayList<T> lista = new ArrayList<>();
        for (T t : Elementos.values())
        {
            lista.add(t);
        }
        return lista;
    }
}

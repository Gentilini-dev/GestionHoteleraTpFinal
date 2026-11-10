package Interfaz;

import java.util.ArrayList;

import Excepciones.ErrorConexionSQLException;
import Modelos.Salon;

public interface IConexion<T>
{
    void crearTabla() throws ErrorConexionSQLException;

    void insertar(T t)throws ErrorConexionSQLException;

    void actualizar(T t)throws ErrorConexionSQLException;

    void eliminar(int id)throws ErrorConexionSQLException;

    ArrayList<T> traerTodos()throws ErrorConexionSQLException;
}

package Interfaz;

import java.util.ArrayList;

import Excepciones.ErrorConexionSQLException;

public interface IConexiones<T>
{
    void CrearTabla() throws ErrorConexionSQLException;
    
    void Insetar(T t)throws ErrorConexionSQLException;;
    
    void Actulizar(T t)throws ErrorConexionSQLException;;
    
    void Eliminar(int id)throws ErrorConexionSQLException;;
    
    ArrayList<T> traerTodos()throws ErrorConexionSQLException;;
}

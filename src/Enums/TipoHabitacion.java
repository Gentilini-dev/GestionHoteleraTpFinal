package Enums;

public enum TipoHabitacion
{
    SIMPLE(30000), DOBLE(45000), TRIPLE(60000), SUITE(90000);
    
    private  double precioNoche;
    
    TipoHabitacion(double precioNoche)
    {
        this.precioNoche = precioNoche;
    }
    
    public double getPrecioNoche()
    {
        return precioNoche;
    }
    public void setPrecionoche(double nuevoPrecio)
    {
        this.precioNoche=nuevoPrecio;
       
    }
}

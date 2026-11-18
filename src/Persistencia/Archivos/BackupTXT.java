package Persistencia.Archivos;

import Excepciones.ErrorArchivoException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

// Backup del hotel en un .txt. Cada backup es un archivo nuevo con la fecha en el nombre,
// y se puede volver a leer desde el menú del admin
public class BackupTXT
{
    private String ruta;

    public BackupTXT(String ruta)
    {
        this.ruta = ruta;
    }

    public String getRuta()
    {
        return ruta;
    }

    // Recibe un título y una lista por cada sección, y escribe una línea por elemento con su toString()
    public void exportar(ArrayList<String> titulos, ArrayList<ArrayList<?>> secciones) throws ErrorArchivoException
    {
        try
        {
            String fechaArchivo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"));
            ruta = "backup_hotel_" + fechaArchivo + ".txt";
            BufferedWriter bw = new BufferedWriter(new FileWriter(ruta));

            String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            bw.write("===== BACKUP SISTEMA HOTELERO - " + fecha + " =====");
            bw.newLine();

            for (int i = 0; i < titulos.size(); i++)
            {
                ArrayList<?> elementos = secciones.get(i);

                bw.newLine();
                bw.write("--- " + titulos.get(i) + " (" + elementos.size() + ") ---");
                bw.newLine();

                for (Object elemento : elementos)
                {
                    bw.write(elemento.toString());
                    bw.newLine();
                }
            }

            bw.close();
        }
        catch (IOException e)
        {
            throw new ErrorArchivoException("No se pudo escribir el backup en " + ruta + ".");
        }
    }

    // Devuelve el contenido del backup, línea por línea
    public ArrayList<String> leer() throws ErrorArchivoException
    {
        ArrayList<String> lineas = new ArrayList<>();
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(ruta));
            String linea = br.readLine();
            while (linea != null)
            {
                lineas.add(linea);
                linea = br.readLine();
            }
            br.close();
        }
        catch (IOException e)
        {
            throw new ErrorArchivoException("No se encontró un backup en " + ruta + ". Realizá uno primero.");
        }
        return lineas;
    }
}

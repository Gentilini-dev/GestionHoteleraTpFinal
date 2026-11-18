package Persistencia.Archivos;

import Excepciones.ErrorArchivoException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

// Backup del hotel en un .txt. Cada backup es un archivo nuevo con la fecha en el nombre
// (ej: backup_hotel_2026-11-18_17-40.txt), y desde el menú del admin se puede leer el último
public class BackupTXT
{
    private String prefijo;   // comienzo del nombre de los archivos, ej: "backup_hotel"
    private String ruta;      // archivo del último backup escrito o leído

    public BackupTXT(String prefijo)
    {
        this.prefijo = prefijo;
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
            ruta = prefijo + "_" + fechaArchivo + ".txt";
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

    // Busca en la carpeta del programa el backup más nuevo.
    // Como la fecha va en formato año-mes-día_hora-minuto, el nombre más grande alfabéticamente es el más reciente
    private String buscarUltimoBackup()
    {
        File[] archivos = new File(".").listFiles();
        String ultimo = null;

        if (archivos != null)
        {
            for (File f : archivos)
            {
                String nombre = f.getName();
                if (nombre.startsWith(prefijo + "_") && nombre.endsWith(".txt")
                        && (ultimo == null || nombre.compareTo(ultimo) > 0))
                {
                    ultimo = nombre;
                }
            }
        }
        return ultimo;
    }

    // Devuelve el contenido del último backup, línea por línea
    public ArrayList<String> leer() throws ErrorArchivoException
    {
        ruta = buscarUltimoBackup();
        if (ruta == null)
        {
            throw new ErrorArchivoException("Todavía no hay ningún backup. Realizá uno primero.");
        }

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
            throw new ErrorArchivoException("No se pudo leer el backup " + ruta + ".");
        }
        return lineas;
    }
}

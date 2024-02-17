package com.mx.credenciales.delete;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ArchivoUtils {

    private static final String RUTA_BASE = "C:\\Documents\\static";

    public static boolean eliminarArchivo(String rutaRelativa) {
        // Normalizar la ruta relativa para el sistema de archivos de Windows
        String rutaNormalizada = rutaRelativa.replace("/", "\\");
        Path rutaAbsoluta = Paths.get(RUTA_BASE + rutaNormalizada);
         System.out.println("\n\n"+rutaAbsoluta+"\n\n");
        try {
            boolean eliminado = Files.deleteIfExists(rutaAbsoluta);
            System.out.println("Eliminación del archivo " + (eliminado ? "exitosa" : "fallida") + ": " + rutaAbsoluta);
            return eliminado;
        } catch (IOException e) {
            System.err.println("Error al eliminar el archivo: " + rutaAbsoluta);
            e.printStackTrace();
            return false;
        }
    }
}

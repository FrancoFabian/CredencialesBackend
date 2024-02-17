package com.mx.credenciales.controller;

import com.mx.credenciales.entity.Credenciales;
import com.mx.credenciales.request.CredentialsWithFiles;
import com.mx.credenciales.service.CrendecialsImpl;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/Credenciales")
@CrossOrigin(origins = "http://localhost:4200")
public class CredencialsWS {
    @Autowired
    CrendecialsImpl crendecials;


    @GetMapping("listar")
    public ResponseEntity<Map<String, Object>> listarCredenciales() {
        List<Credenciales> lista = crendecials.listar();
        Map<String, Object> response = new HashMap<>();
        if (lista.isEmpty()) {
            response.put("message", "No hay credenciales que mostrar");
            response.put("data", null);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", null);
            response.put("data", lista);
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/fotoPerfil/{filename:.+}")
    public ResponseEntity<byte[]> retornarFotoPerfil(@PathVariable String filename) throws IOException {
        Path path = Paths.get("/Documents/static/fotoPerfil");
        byte[] image = Files.readAllBytes(path);
        String contentType = Files.probeContentType(path);

        if (contentType == null) {
            // Si no se puede determinar el tipo de contenido, podrías establecer un tipo por defecto
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(image);
    }
    @GetMapping("/fotoFirma/{filename:.+}")
    public ResponseEntity<byte[]> retornarFotoFirma(@PathVariable String filename) throws IOException {
        Path path = Paths.get("/Documents/static/fotoFirma");
        byte[] image = Files.readAllBytes(path);
        String contentType = Files.probeContentType(path);

        if (contentType == null) {
            // Si no se puede determinar el tipo de contenido, podrías establecer un tipo por defecto
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(image);
    }

    @GetMapping("/pdf/{nombreArchivo:.+}")
    public ResponseEntity<Resource> servirPdf(@PathVariable String nombreArchivo) {
        try {
            // Construye la ruta al archivo
            String rutaBase = "/Documents/static/pdf/";
            Path path = Paths.get(rutaBase + nombreArchivo);
            Resource resource = new UrlResource(path.toUri());

            // Verifica que el recurso exista y sea legible
            if(resource.exists() && resource.isReadable()) {
                // Devuelve el recurso como una respuesta HTTP
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(resource);
            } else {
                // Gestiona el caso en que el recurso no se encuentra o no es legible
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            // Gestiona el error en caso de una URL mal formada
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/visualizacion/{nombreArchivo:.+}")
    public ResponseEntity<Resource> servirPng(@PathVariable String nombreArchivo) {
        try {
            Path path = Paths.get("/Documents/static/visualizacion/" + nombreArchivo);
            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("subirDatosCredenciales")
    public ResponseEntity<String> uploadCredentials(@Valid @ModelAttribute CredentialsWithFiles credentials)throws IOException {
        if (!isValidImage(credentials.getFoto()) || !isValidImage(credentials.getFirma())) {
            return ResponseEntity.badRequest().body("Invalid image file format.");
        }
        // Lógica para procesar los archivos y datos
        Credenciales credenciales = crendecials.guardar(credentials);

        return ResponseEntity.ok("Successfully.");
    }


    private boolean isValidImage(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null &&
                (contentType.equals("image/png") || contentType.equals("image/jpeg"));
    }
    @DeleteMapping("/eliminar/{numEmpleado}")
    public ResponseEntity<?> eliminarCredencial(@PathVariable Long numEmpleado) {
        try {
            // Lógica para eliminar la credencial...
            String mesa = crendecials.eliminarPorNumEmpleado(numEmpleado).toString();
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", mesa);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Error al eliminar la credencial.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }




}

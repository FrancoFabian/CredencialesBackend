package com.mx.credenciales.service;
import com.mx.credenciales.delete.ArchivoUtils;
import com.mx.credenciales.entity.Credenciales;
import com.mx.credenciales.repository.RepoCredenciales;
import com.mx.credenciales.request.CredentialsWithFiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.commons.io.IOUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.apache.commons.codec.binary.Base64;


@Service
public class CrendecialsImpl implements CredencialService{
    private static final String RUTA_BASE = "/Documents/static";
    @Autowired
    RepoCredenciales repCredx;
    @Autowired
    private WebSocketService webSocketService;
    @Override
    public List<Credenciales> listar() {
        return repCredx.findAll(Sort.by(Sort.Direction.ASC,"numEmpleado"));
    }

    @Override
    public Credenciales eliminar(Long id) {
        Credenciales deleteCred = repCredx.findByIdOrNull(id);
        if (deleteCred == null) {
            return  null;
        }
        repCredx.deleteById(id);
        return deleteCred;
    }

    public String preprocesarSvg(String svgString) throws Exception {
        // Preparar el analizador de documentos XML
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        // Convertir el String SVG a un Document XML
        ByteArrayInputStream input = new ByteArrayInputStream(svgString.getBytes("UTF-8"));
        Document doc = builder.parse(input);

        // Buscar todos los elementos de imagen en el SVG
        NodeList images = doc.getElementsByTagName("image");
        for (int i = 0; i < images.getLength(); i++) {
            Element image = (Element) images.item(i);
            String href = image.getAttributeNS("http://www.w3.org/1999/xlink", "href");

            if (href.startsWith("data:image")) {
                // Extraer la información de la imagen en base64
                String base64Image = href.split(",")[1];
                byte[] imageBytes = Base64.decodeBase64(base64Image);

                // Guardar la imagen como un archivo temporal
                String imageFormat = href.split(";")[0].split("/")[1]; // Obtener el formato de la imagen (png, jpeg, etc.)
                File imageFile = File.createTempFile("image-", "." + imageFormat, new File("/path/to/temp/dir")); // Ajusta la ruta según tu entorno
                try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                    fos.write(imageBytes);
                }

                // Actualizar el atributo href del elemento de imagen para referenciar el archivo guardado
                image.setAttributeNS("http://www.w3.org/1999/xlink", "href", imageFile.toURI().toString());
            }
        }

        // Convertir el Document actualizado de nuevo a String
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);

        return writer.toString();
    }
  private String guardarFotoTwo(MultipartFile foto, String nuevoNombreBase) throws IOException {
      String rutaBase = "/Documents/static/fotoPerfil";
      Path directorioDestino = Paths.get(rutaBase);

      if (!Files.exists(directorioDestino)) {
          Files.createDirectories(directorioDestino);
      }

      String extension = StringUtils.getFilenameExtension(foto.getOriginalFilename());
      String nuevoNombreArchivo = nuevoNombreBase + "." + extension;
      Path ubicacionArchivo = directorioDestino.resolve(nuevoNombreArchivo);

      Files.copy(foto.getInputStream(), ubicacionArchivo, StandardCopyOption.REPLACE_EXISTING);

      // Devuelve la ruta relativa o el nombre del archivo para su uso posterior
      return "/fotoPerfil/"+nuevoNombreArchivo;
  }
    private String guardarFirmaTwo(MultipartFile firma, String nuevoNombreBase) throws IOException {
        String rutaBase = "/Documents/static/fotoFirma";
        Path directorioDestino = Paths.get(rutaBase);

        if (!Files.exists(directorioDestino)) {
            Files.createDirectories(directorioDestino);
        }

        String extension = StringUtils.getFilenameExtension(firma.getOriginalFilename());
        String nuevoNombreArchivo = nuevoNombreBase + "." + extension;
        Path ubicacionArchivo = directorioDestino.resolve(nuevoNombreArchivo);

        Files.copy(firma.getInputStream(), ubicacionArchivo, StandardCopyOption.REPLACE_EXISTING);

        // Devuelve la ruta relativa o el nombre del archivo para su uso posterior
        return "/fotoFirma/"+nuevoNombreArchivo;
    }

    private String guardarPdfTwo(byte[] pdfData, String nombreArchivo) throws IOException {
        // Asegurarte de limpiar el nombre del archivo para evitar caracteres inválidos
        nombreArchivo = StringUtils.cleanPath(nombreArchivo);
        // Definir la ruta base donde se guardarán los archivos PDF
        String rutaBase = "/Documents/static/pdf/";

        // Construir el objeto Path para el archivo PDF
        Path path = Paths.get(rutaBase + nombreArchivo + ".pdf");

        // Verificar si los directorios existen o crearlos
        if (!Files.exists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }

        // Guardar el arreglo de bytes en el archivo especificado
        Files.write(path, pdfData);

        // Retornar la ruta relativa donde se guardó el archivo PDF
        // Esta ruta se debe ajustar según cómo accederás a estos archivos
        // Por ejemplo, si sirves estos archivos estáticos a través de un controlador, necesitarás ajustar esta ruta
        return "/pdf/" + nombreArchivo + ".pdf";
    }


    private String guardarPng(byte[] pngData, String nombreArchivo) throws IOException {
        String rutaBase = "/Documents/static/visualizacion/";
        Path directorioDestino = Paths.get(rutaBase);

        if (!Files.exists(directorioDestino)) {
            Files.createDirectories(directorioDestino);
        }

        Path archivoPng = directorioDestino.resolve(nombreArchivo + ".png");
        Files.write(archivoPng, pngData); // Simplificado sin necesidad de FileOutputStream

        return "/visualizacion/" + nombreArchivo + ".png";
    }

    @Override
    public Credenciales guardar(CredentialsWithFiles credenciales)throws IOException{
            String numEmployee = credenciales.getNumEmpleado().toString();
            String nombreSinEspacios = credenciales.getNombre().replaceAll("\\s+", "");
            String categoriaSinEspacios = credenciales.getCategoria().replaceAll("\\s+", "");;
            String nombreArchivo = numEmployee+"Credencial"+nombreSinEspacios+categoriaSinEspacios;
            String nameFoto = numEmployee+"Foto"+nombreSinEspacios+categoriaSinEspacios;
            String nameFirma = numEmployee+"Firma"+nombreSinEspacios+categoriaSinEspacios;
           //Primer Paso
            String foto = guardarFotoTwo(credenciales.getFoto(),nameFoto);
            webSocketService.sendProgressUpdate("/topic/progress", 15);
            //Segundo Paso
            String firma = guardarFirmaTwo(credenciales.getFirma(),nameFirma);
            webSocketService.sendProgressUpdate("/topic/progress", 30);
            byte[] visualPdf;
            byte[] pdfBytes;
            String rutaRelativaPdf;
            try {
               //Tercer Paso
               visualPdf = convertSvgToPng(credenciales.getSvg().getBytes());
                webSocketService.sendProgressUpdate("/topic/progress", 50);

            } catch (Exception e) {
                // Manejar la excepción, como por ejemplo loggear el error o lanzar una excepción propia
                throw new IOException("Error al convertir SVG a PNG: " + e.getMessage(), e);
            }
            try {
                //Cuarto Paso
                pdfBytes = createPdfWithImage(visualPdf);
                webSocketService.sendProgressUpdate("/topic/progress", 75);
                //Quinto Paso
                rutaRelativaPdf = guardarPdfTwo(pdfBytes, nombreArchivo);
                webSocketService.sendProgressUpdate("/topic/progress", 85);
            }catch (Exception e){
                throw new IOException("Error al convertir PNG a PDF: " + e.getMessage(), e);
            }
           // String pdf = guardarPdf(credenciales.getPdf());
          //Sexto Paso
           String visualizacion = guardarPng(visualPdf, nombreArchivo);
           webSocketService.sendProgressUpdate("/topic/progress", 95);
            //Septimo Paso
            Credenciales crear = new Credenciales(
                    credenciales.getNumEmpleado(),
                    credenciales.getNombre(),
                    foto,
                    credenciales.getCategoria(),
                    firma,
                    rutaRelativaPdf,
                    visualizacion
            );
        webSocketService.sendProgressUpdate("/topic/progress", 100);
        return repCredx.save(crear);

    }

    @Override
    public Credenciales editar(Credenciales credenciales)throws IOException {
        return null;
    }
    public byte[] convertSvgToPng(byte[] svgData) throws Exception {
        // Crear los streams de entrada y salida
        ByteArrayInputStream svgInputStream = new ByteArrayInputStream(svgData);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();

        // Crear e inicializar el transcodificador
        PNGTranscoder transcoder = new PNGTranscoder();

        // Establecer las dimensiones deseadas
        transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, 3933f); // Ancho en píxeles
        transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, 6175f); // Alto en píxeles

        // Crear los objetos de entrada y salida para el transcodificador
        TranscoderInput input = new TranscoderInput(svgInputStream);
        TranscoderOutput output = new TranscoderOutput(pngOutputStream);

        // Realizar la transcodificación
        transcoder.transcode(input, output);

        // Asegurarse de que todos los datos se han escrito al stream de salida
        pngOutputStream.flush();

        // Devolver los datos de la imagen PNG
        return pngOutputStream.toByteArray();
    }

    public byte[] createPdfWithImage(byte[] imageData) throws Exception {
        try (PDDocument document = new PDDocument()) {
            // Primera imagen (PNG pasado como parámetro)
            PDImageXObject pdImage1 = PDImageXObject.createFromByteArray(document, imageData, "firstImage");

            // Crear una página con las dimensiones de la primera imagen
            PDPage page1 = new PDPage(new PDRectangle(pdImage1.getWidth(), pdImage1.getHeight()));
            document.addPage(page1);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page1)) {
                contentStream.drawImage(pdImage1, 0, 0, pdImage1.getWidth(), pdImage1.getHeight());
            }

            // Segunda imagen (desde los recursos del JAR)
            InputStream in = getClass().getResourceAsStream("/static/backcredencial/2.png");
            if (in == null) {
                throw new Exception("No se pudo cargar la imagen de recurso /static/backcredencial/2.png");
            }
            byte[] bytes = IOUtils.toByteArray(in); // Convertir InputStream a byte[]
            PDImageXObject pdImage2 = PDImageXObject.createFromByteArray(document, bytes, "secondImage");

            // Crear una segunda página con las dimensiones de la segunda imagen
            PDPage page2 = new PDPage(new PDRectangle(pdImage2.getWidth(), pdImage2.getHeight()));
            document.addPage(page2);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page2)) {
                contentStream.drawImage(pdImage2, 0, 0, pdImage2.getWidth(), pdImage2.getHeight());
            }

            // Guardar el documento en un arreglo de bytes
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);

            // Cerrar el InputStream
            in.close();

            // Retornar el PDF como un arreglo de bytes
            return out.toByteArray();
        }
    }
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

    public ResponseEntity<String> eliminarPorNumEmpleado(Long numEmpleado) {
        Credenciales credencial = repCredx.findByIdOrNull(numEmpleado);
        if (credencial == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Credencial no encontrada.");
        }
        // Eliminación de archivos
        eliminarArchivo(credencial.getFoto());
        eliminarArchivo(credencial.getFirma());
        eliminarArchivo(credencial.getPdf());
        eliminarArchivo(credencial.getVisualizacion());

        // Eliminación de la credencial en la base de datos
        repCredx.deleteById(credencial.getNumEmpleado());
        return ResponseEntity.ok("Credencial eliminada correctamente.");
    }



}

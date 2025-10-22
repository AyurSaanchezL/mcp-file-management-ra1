package com.dam.accesodatos.ra1;

import com.dam.accesodatos.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * IMPLEMENTACIÓN PARA ESTUDIANTES - RA1: Gestión de Ficheros
 * 
 * Esta clase contiene esqueletos de métodos que los estudiantes deben completar
 * para demostrar su comprensión del RA1 y sus criterios de evaluación.
 * 
 * INSTRUCCIONES:
 * 1. Completar cada método marcado con TODO
 * 2. Usar solo las clases Java I/O indicadas en comentarios
 * 3. Implementar manejo robusto de excepciones
 * 4. Ejecutar tests para validar implementación
 * 5. Documentar decisiones técnicas en comentarios
 * 
 * PROHIBIDO:
 * - Usar librerías externas no permitidas
 * - Copiar código sin entender
 * - Omitir manejo de excepciones
 */
@Service
public class FileUserServiceImpl implements FileUserService {

    private final ObjectMapper objectMapper;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public FileUserServiceImpl() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules(); // Para LocalDateTime
    }

    // ========================================================================================
    // CE1.a: ANÁLISIS DE CLASES RELACIONADAS CON TRATAMIENTO DE FICHEROS
    // ========================================================================================

    @Override
    public String getFileInfo(String filePath) {
        /*
         * TODO CE1.a: Implementar información detallada de archivo (actividad 1 de la presentación vista en clase)
         * 
         * Pasos requeridos:
         * 1. Crear objeto File con la ruta proporcionada
         * 2. Verificar que existe con exists()
         * 3. Determinar si es archivo o directorio con isFile()/isDirectory()
         * 4. Obtener tamaño con length() (solo para archivos)
         * 5. Obtener permisos con canRead(), canWrite(), canExecute()
         * 6. Formatear fecha de modificación con SimpleDateFormat
         * 7. Construir string con formato: "Tipo: archivo/directorio, Tamaño: X bytes, Permisos: rwx, Fecha: ..."
         * 
         * Clases requeridas:
         * - File (exists, isFile, isDirectory, length, lastModified, canRead, canWrite, canExecute)
         * - SimpleDateFormat (para formatear fecha)
         * - Date (para convertir timestamp)
         */
        
        // Paso 1: Crear objeto File con la ruta proporcionada
        File file = new File(filePath);
        
        // Paso 2: Verificar que existe
        if (!file.exists()) {
            return "Error: El archivo o directorio no existe: " + filePath;
        }
        
        // Paso 3: Determinar si es archivo o directorio
        String tipo = file.isFile() ? "archivo" : "directorio";
        
        // Paso 4: Obtener tamaño (solo para archivos)
        String tamanio;
        if (file.isFile()) {
            tamanio = file.length() + " bytes";
        } else {
            tamanio = "N/A (directorio)";
        }
        
        // Paso 5: Obtener permisos
        String permisos = "";
        permisos += file.canRead() ? "r" : "-";
        permisos += file.canWrite() ? "w" : "-";
        permisos += file.canExecute() ? "x" : "-";
        
        // Paso 6: Formatear fecha de modificación
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date fechaModificacion = new Date(file.lastModified());
        String fechaFormateada = formatter.format(fechaModificacion);
        
        // Paso 7: Construir string con formato especificado
        return String.format("Tipo: %s, Tamaño: %s, Permisos: %s, Fecha: %s", 
                           tipo, tamanio, permisos, fechaFormateada);
    }

    @Override
    public String compareIOPerformance(String filePath)  {
        /*
         *
         * 
         * Pasos requeridos:
         * 1. Validar que archivo existe
         * 2. Primera prueba: leer con FileReader (sin buffer)
         *    - Usar System.currentTimeMillis() antes y después
         *    - Leer carácter por carácter con read()
         * 3. Segunda prueba: leer con BufferedReader
         *    - Medir tiempo de la misma manera
         *    - Usar readLine() para leer líneas
         * 4. Calcular diferencia de tiempo
         * 5. Retornar comparación formateada
         * 
         * Formato sugerido:
         * "FileReader: 1250ms
         *  BufferedReader: 45ms
         *  Mejora: 96.4% más rápido con buffer"
         */

        File file = new File(filePath);

        if (!file.exists()) {
            return "Error: El archivo o directorio no existe: " + filePath;
        }

        if (!file.isFile()) {
            return "Error: La ruta indicada es un directorio: " + filePath;
        }

        long fileReaderTime;
        long bufferedReaderTime;

        // SIN BUFFER

        long startTime = System.currentTimeMillis();

        try(FileReader fileReader = new FileReader(file)) {
            // read() devuelve -1 cuando se llega al final del archivo.
            while (fileReader.read() != -1){
            }
        }catch (IOException e){
            return "Error leyendo con FileReader: " + e.getMessage();
        }

        long endTime = System.currentTimeMillis();

        // El tiempo de lectura de fileReader es el final - el inicial
        fileReaderTime = endTime - startTime;

        // CON BUFFER
        startTime = System.currentTimeMillis();

        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {

            // Cuando termina devuelve null
            while (bufferedReader.readLine() != null){

            }


        }catch (IOException e){
            return "Error leyendo con BufferedReader: " + e.getMessage();
        }
        endTime = System.currentTimeMillis();
        bufferedReaderTime = endTime - startTime;

        double mejora;
        StringBuilder resultado = new StringBuilder();
        resultado.append(String.format("\nFileReader: %dms%n", fileReaderTime));
        resultado.append(String.format("BufferedReader: %dms", bufferedReaderTime));

        if (fileReaderTime > bufferedReaderTime) {
            mejora = ((double) (fileReaderTime - bufferedReaderTime) / fileReaderTime) * 100;
            resultado.append(String.format("%nMejora: %.1f%% mas rapido con buffer\n", mejora));
        } else if (bufferedReaderTime > fileReaderTime) {
            mejora = ((double) (bufferedReaderTime - fileReaderTime) / bufferedReaderTime) * 100;
            resultado.append(String.format("%nMejora: %.1f%% mas rapido con fileReader\n", mejora));
        } else {
            resultado.append("\nNo hay diferencia de rendimiento");
        }
        return resultado.toString();
    }

    @Override
    public String compareNIOvsIO(String filePath) {
        /*
         * 
         * Pasos requeridos:
         * 1. Enfoque tradicional (java.io):
         *    - Usar File para verificar existencia
         *    - Usar BufferedReader para leer líneas
         *    - Contar líneas manualmente
         * 2. Enfoque NIO (java.nio.file):
         *    - Usar Path y Files.exists() para verificar
         *    - Usar Files.readAllLines() para leer todo de una vez
         * 3. Comparar sintaxis y funcionalidades
         * 4. Medir tiempo de ejecución de ambos
         * 5. Retornar comparación formateada
         * 
         * Formato sugerido:
         * "IO Tradicional: 15 líneas de código, 45ms
         *  NIO: 3 líneas de código, 32ms
         *  NIO es más conciso y eficiente"
         */

        File file = new File(filePath);

        if (!file.exists()){
            return "Error: El archivo o directorio no existe: " + filePath;
        }

        if(!file.isFile()){
            return "Error: La ruta indicada es un directorio: " + filePath;
        }

        // CONTAR LINEAS CON BUFFER
        long NIOReaderTime;
        long bufferedReaderTime;
        long endTime;

        long startTime = System.currentTimeMillis();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))){
            while (bufferedReader.readLine() != null){

            }
        }catch (IOException e){
            return "Error leyendo con BufferedReader: " + e.getMessage();
        }
        endTime = System.currentTimeMillis();
        bufferedReaderTime = endTime - startTime;

        // COMPROBAR Y CONTAR LINEAS CON readAllLines()
        Path path = Paths.get(filePath);
        if (!Files.exists(path)){
            return "Error: El archivo no existe: " + filePath;
        }

        startTime = System.currentTimeMillis();
        try{
            Files.readAllLines(path);

        }catch (IOException e){
            return "Error leyendo con FileReader: " + e.getMessage();
        }
        endTime = System.currentTimeMillis();
        NIOReaderTime = endTime - startTime;

        StringBuilder resultado = new StringBuilder();
        resultado.append(String.format("\nIO Tradicional: %d lineas de codigo, %dms\nNIO: %d lineas de codigo, %dms\n", 26, bufferedReaderTime, 16, NIOReaderTime));

        if (bufferedReaderTime < NIOReaderTime) {
            resultado.append("IO Tradidicional ha sido mas eficiente\n");
        }else if (NIOReaderTime < bufferedReaderTime) {
            resultado.append("NIO es mas conciso y eficiente\n");
        }else{
            resultado.append("No hay diferencia de rendimiento\n");
        }

        return resultado.toString();
    }

    // ========================================================================================
    // CE1.b: UTILIZACIÓN DE FLUJOS PARA ACCESO A INFORMACIÓN EN FICHEROS
    // ========================================================================================

    @Override
    public String searchTextInFile(String filePath, String searchText) {
        /*
         * 
         * Pasos requeridos:
         * 1. Validar que archivo existe
         * 2. Usar BufferedReader para leer línea por línea
         * 3. Para cada línea: usar String.contains() o indexOf() para buscar
         * 4. Llevar contador de línea actual
         * 5. Acumular resultados: número de línea y contenido donde aparece
         * 6. Contar total de ocurrencias
         * 7. Retornar string formateado con resultados
         * 
         * Formato sugerido:
         * "Línea 5: contenido de la línea donde aparece texto
         *  Línea 12: otra línea con el texto
         *  Total: 2 ocurrencias encontradas"
         */

        File file = new File(filePath);

        if (!file.exists()){
            return "Error: El archivo o directorio no existe: " + filePath;
        }

        if (!file.isFile()){
            return "Error: La ruta indicada es un directorio: " + filePath;
        }

        int contadorBuffer = 0;
        HashMap<Integer, String> coincidencias = new HashMap<>();
        String linea;

        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(file))){

            while ((linea = bufferedReader.readLine()) != null){
                contadorBuffer++;
                if (linea.contains(searchText)){
                    coincidencias.put(contadorBuffer, linea);
                }
            }
        }catch(IOException e){
            return "Error leyendo con BufferedReader: " + e.getMessage();
        }

        StringBuilder resultado = new StringBuilder();
        for (Map.Entry<Integer, String> entry : coincidencias.entrySet()) {
            resultado.append(String.format("Linea (%d): %s%n", entry.getKey(), entry.getValue()));
        }

        resultado.append(String.format("\nTotal: %d ocurrencias", coincidencias.size()));
        
        return resultado.toString();
    }

    @Override
    public String randomAccessRead(String filePath, long position, int length) {
        /*
         * 
         * Pasos requeridos:
         * 1. Validar que archivo existe
         * 2. Crear RandomAccessFile en modo "r" (solo lectura)
         * 3. Usar seek(position) para posicionar puntero
         * 4. Crear buffer de bytes del tamaño especificado
         * 5. Usar read(buffer) para leer datos
         * 6. Convertir bytes a String
         * 7. Manejar EOF si posición está fuera del archivo
         * 
         * Clases requeridas:
         * - RandomAccessFile (constructor con modo "r", seek, read)
         * - String constructor para convertir bytes
         */

        File file = new File(filePath);

        if (!file.exists()){
            return "Error: El archivo o directorio no existe: " + filePath;
        }

        if (!file.isFile()){
            return "Error: La ruta indicada es un directorio: " + filePath;
        }

        try(RandomAccessFile randomAccessFile = new RandomAccessFile(filePath, "r")){
            randomAccessFile.seek(position);

            // Creo el array de bytes (buffer)
            byte[] buffer = new byte[length];

            // Leo los datos y compruebo que no esté vacío
            if (randomAccessFile.read(buffer) != -1){
                // Convierto los bytes a String con el constructor de la clase String
                return new String(buffer);
            }

            // Si está vacío, devuelvo un error
            return "Error: selección vacía";


        }catch (FileNotFoundException e){
            return "Error: No he encontrado el archivo en: " + filePath;
        }catch (IOException e){ // También captura la excepción EOF
            return "Error buscando la posición: " + e.getMessage();
        }


    }

    @Override
    public boolean randomAccessWrite(String filePath, long position, String content) {
        /*
         * 
         * Pasos requeridos:
         * 1. Crear directorios padre si no existen
         * 2. Crear RandomAccessFile en modo "rw" (lectura/escritura)
         * 3. Usar seek(position) para posicionar puntero
         * 4. Convertir content a bytes con getBytes()
         * 5. Usar write(bytes) para escribir datos
         * 6. Cerrar archivo con close()
         * 7. Retornar true si exitoso
         * 
         * Clases requeridas:
         * - RandomAccessFile (constructor con modo "rw", seek, write)
         * - String.getBytes() para obtener bytes
         */

        File file = new File(filePath);

        // Si el archivo no existe, lo creo para poder escribir en él
        if (!file.exists()){
            try{
                Files.createFile(Path.of(filePath));
            }catch (IOException e){
                return false;
            }

        }else {
            if (!file.isFile()){
                return false;
            }
        }


        // Compruebo si tiene un directorio padre, si no es así lo creo con Files.createDirectories();
        if (!file.getParentFile().exists()){
            try{
                Files.createDirectories(Path.of(file.getParent()));
            }catch (IOException e){
                return false;
            }
        }

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(filePath, "rw")){
            randomAccessFile.seek(position);

            // Convierto 'content' a bytes con content.getBytes() y se lo paso al métod.o write()
            randomAccessFile.write(content.getBytes());

            randomAccessFile.close();

            // Si no ha saltado ninguna excepción, retorno 'true'
            return true;
        } catch (IOException e){ // Esta excepción también abarca la de FileNotFound
            return false;
        }

    }

    @Override
    public boolean convertFileEncoding(String sourceFile, String targetFile, 
                                     String sourceCharset, String targetCharset) {
        /*
         * 
         * Pasos requeridos:
         * 1. Validar que archivo origen existe
         * 2. Crear InputStreamReader con FileInputStream y charset origen
         * 3. Crear OutputStreamWriter con FileOutputStream y charset destino
         * 4. Envolver con BufferedReader/BufferedWriter para eficiencia
         * 5. Leer línea por línea y escribir con nueva codificación
         * 6. Usar try-with-resources para cerrar recursos
         * 7. Retornar true si conversión exitosa
         * 
         * Ejemplo: convertir de ISO-8859-1 a UTF-8
         * InputStreamReader isr = new InputStreamReader(new FileInputStream(source), "ISO-8859-1");
         * OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(target), "UTF-8");
         */

        File file = new File(sourceFile);


        if (!file.exists() || !file.isFile()){
            return false;
        }

        // Compruebo que exista el fichero final y que tenga un directorio padre
        File outFile = new File(targetFile);
        File outFileParent = outFile.getParentFile();

        if (!outFileParent.exists() || !outFileParent.isDirectory()){   // Si no tiene directorio padre, lo creo
            if(!outFileParent.mkdirs()){    // Si no he podido crear el directorio padre, devuelvo false
                return false;
            }
        }

        // Creo el BufferedReader que rodea el InputStreamReader con el enconding antiguo
        // Y también creo el BufferedWriter que rodea el OutputStreamReader con el encoding nuevo
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader (new FileInputStream(sourceFile), sourceCharset));
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile), targetCharset))){

            while (bufferedReader.readLine() != null){
                bufferedWriter.write(bufferedReader.readLine());
            }

            // Si hay éxito, devuelve 'true'
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    // ========================================================================================
    // CE1.c: UTILIZACIÓN DE CLASES PARA GESTIÓN DE FICHEROS Y DIRECTORIOS
    // ========================================================================================

    @Override
    public List<String> listUserFiles(String directoryPath) {
        /*
         * 
         * Pasos requeridos:
         * 1. Validar que directoryPath existe y es directorio
         * 2. Usar Files.list() o File.listFiles()
         * 3. Filtrar solo archivos (no directorios)
         * 4. Filtrar por extensiones: .csv, .json, .xml
         * 5. Retornar lista de nombres de archivo
         * 
         * Clases I/O requeridas:
         * - Files, Paths
         * - Stream API para filtrado
         * - FilenameFilter o predicados
         */

        File directorio = new File(directoryPath);

        // Valido que exista y que es un directorio
        if (!directorio.exists()){
            return null;
        }

        if (!directorio.isDirectory()){
            return null;
        }

        // Creo la lista que voy a rellenar y devolver
        List<String> listaFinal = new ArrayList<>();
        if (directorio.listFiles() == null){
            return listaFinal;
        }else{
            // Esta es la lista de archivos que contiene el directorio
            List<File> listaArchivos = new ArrayList<>(List.of(directorio.listFiles()));

            // Itero esa lista para filtrarla por directorios y ficheros .csv / .xml / .json
            for (File archivo : listaArchivos){
                if (!archivo.isDirectory() && archivo.getName().endsWith(".csv") || archivo.getName().endsWith(".xml") || archivo.getName().endsWith(".json")){
                    listaFinal.add(archivo.getName());
                }
            }

            return listaFinal;
        }


    }

    @Override
    public boolean validateDirectoryStructure(String basePath) {
        /*
         * TODO CE1.c: Implementar validación de estructura de directorios
         * 
         * Pasos requeridos:
         * 1. Definir estructura esperada (ej: data/, exports/, temp/)
         * 2. Para cada directorio: verificar si existe
         * 3. Crear directorios faltantes con Files.createDirectories()
         * 4. Verificar permisos de lectura/escritura
         * 5. Retornar true si todo está correcto
         * 
         * Clases I/O requeridas:
         * - Files, Paths
         * - Files.exists(), Files.isDirectory()
         * - Files.createDirectories()
         * - Files.isReadable(), Files.isWritable()
         */

        File directory = new File(basePath);

        // Si no existe, lo creo
        if (!directory.exists()){
            try{
                // Uso createDirectories() en vez de createDirectory() por si los directorios hasta la ruta indicada tampoco existen
                Files.createDirectories(Path.of(basePath));

            }catch (IOException e){
                return false;
            }

        }
        // Si no es un directorio, devuelve false
        if (!directory.isDirectory()){
            return false;
        }

        try{
            Files.createDirectory(Path.of(basePath+"/data"));
        }catch (IOException e){
            return false;
        }
        
        // TODO: Implementar aquí
        throw new UnsupportedOperationException("TODO: Implementar validateDirectoryStructure usando Files API");
        
        // Estructura sugerida:
        // basePath/
        //   ├── data/       (archivos de datos)
        //   ├── exports/    (archivos exportados)
        //   └── temp/       (archivos temporales)
    }

    @Override
    public String createTempFile(String prefix, String content) {
        /*
         * TODO CE1.c: Implementar creación de archivo temporal
         * 
         * Pasos requeridos:
         * 1. Usar File.createTempFile(prefix, ".tmp") para crear archivo temporal
         * 2. Obtener ruta absoluta con getAbsolutePath()
         * 3. Escribir contenido usando FileWriter
         * 4. Cerrar recursos correctamente
         * 5. Retornar ruta del archivo temporal creado
         * 
         * NOTA: Los archivos temporales se crean en directorio del sistema
         * (ej: C:\\Users\\usuario\\AppData\\Local\\Temp en Windows)
         * 
         * Clases requeridas:
         * - File.createTempFile() (método estático)
         * - FileWriter para escribir contenido
         */
        
        // TODO: Implementar aquí
        throw new UnsupportedOperationException("TODO: Implementar createTempFile usando File.createTempFile()");
    }

    @Override
    public String formatTextFile(String sourceFile) {
        /*
         * TODO CE1.c: Implementar formateador basado en ejemplo ArreglarFichero de la presentación vista en clase
         * 
         * Pasos requeridos (basado en ArreglarFichero de la presentación):
         * 1. Validar que archivo origen existe
         * 2. Crear archivo temporal para resultado
         * 3. Leer archivo línea por línea con BufferedReader
         * 4. Para cada línea, procesar carácter por carácter:
         *    - Eliminar espacios al principio de línea (princLinea flag)
         *    - Sustituir múltiples espacios consecutivos por uno solo (espacios flag)
         *    - Convertir primera letra de línea a mayúscula (primerLetra flag)
         *    - Mantener otros caracteres como están
         * 5. Escribir línea procesada a archivo temporal
         * 6. Retornar ruta del archivo temporal
         * 
         * Variables de control sugeridas:
         * - boolean princLinea = true (inicio de línea)
         * - boolean espacios = false (espacios consecutivos)
         * - boolean primerLetra = false (primera letra encontrada)
         */
        
        // TODO: Implementar aquí
        throw new UnsupportedOperationException("TODO: Implementar formatTextFile basado en ejemplo ArreglarFichero de la presentación vista en clase");
    }

    // ========================================================================================
    // CE1.d: ESCRITURA Y LECTURA DE INFORMACIÓN EN FORMATO XML
    // ========================================================================================

    @Override
    public List<User> readUsersFromXML(String filePath) {
        /*
         * TODO CE1.d: Implementar lectura de XML usando DOM parser
         * 
         * Pasos requeridos:
         * 1. Crear DocumentBuilderFactory y DocumentBuilder
         * 2. Usar DocumentBuilder.parse() para obtener Document
         * 3. Obtener todos los elementos "user" con getElementsByTagName()
         * 4. Para cada elemento user: extraer texto de cada campo
         * 5. Convertir texto a tipos apropiados (Long, Boolean, LocalDateTime)
         * 6. Crear objeto User con los datos extraídos
         * 
         * Clases XML requeridas:
         * - DocumentBuilderFactory, DocumentBuilder
         * - Document, Element, NodeList
         * - NO usar JAXB automático
         */
        
        List<User> users = new ArrayList<>();
        
        // TODO: Implementar aquí
        throw new UnsupportedOperationException("TODO: Implementar readUsersFromXML usando DOM parser");
        
        // ESTRUCTURA XML esperada:
        // <users>
        //   <user>
        //     <id>1</id>
        //     <name>Juan</name>
        //     ... otros campos
        //   </user>
        // </users>
    }

    @Override
    public boolean writeUsersToXML(List<User> users, String filePath) {
        /*
         * TODO CE1.d: Implementar escritura de XML usando DOM y Transformer
         * 
         * Pasos requeridos:
         * 1. Crear DocumentBuilderFactory y DocumentBuilder
         * 2. Crear nuevo Document con createElement()
         * 3. Crear elemento raíz "users"
         * 4. Para cada User: crear elemento "user" con subelementos
         * 5. Usar Transformer para escribir Document a archivo
         * 6. Configurar Transformer para pretty-print (setOutputProperty)
         * 
         * Clases XML requeridas:
         * - DocumentBuilderFactory, DocumentBuilder, Document
         * - Element (createElement, appendChild, setTextContent)
         * - TransformerFactory, Transformer
         * - DOMSource, StreamResult
         */
        
        // TODO: Implementar aquí
        throw new UnsupportedOperationException("TODO: Implementar writeUsersToXML usando DOM y Transformer");
    }

    @Override
    public List<User> readUsersFromXMLSAX(String filePath) {
        /*
         * TODO CE1.d: Implementar lectura de XML usando SAX parser (alternativa eficiente)
         * 
         * Pasos requeridos:
         * 1. Crear SAXParserFactory y SAXParser
         * 2. Implementar DefaultHandler personalizado:
         *    - startElement(): detectar inicio de elementos
         *    - characters(): capturar contenido de texto
         *    - endElement(): procesar fin de elementos
         * 3. Mantener estado durante parsing (usuario actual, campo actual)
         * 4. Usar SAXParser.parse() con el handler
         * 
         * Clases SAX requeridas:
         * - SAXParserFactory, SAXParser
         * - DefaultHandler (clase anónima o interna)
         * - Atributos de estado para tracking
         */
        
        List<User> users = new ArrayList<>();
        
        // TODO: Implementar aquí
        throw new UnsupportedOperationException("TODO: Implementar readUsersFromXMLSAX usando SAX parser");
        
        // PISTA: Crear clase que extienda DefaultHandler
        /*
        class UserSAXHandler extends DefaultHandler {
            // TODO: Implementar startElement, characters, endElement
        }
        */
    }

    // ========================================================================================
    // CE1.e: ESCRITURA Y LECTURA DE INFORMACIÓN EN FORMATO JSON
    // ========================================================================================

    @Override
    public List<User> readUsersFromJSON(String filePath) {
        /*
         * TODO CE1.e: Implementar lectura de JSON usando Jackson
         * 
         * Pasos requeridos:
         * 1. Validar que archivo existe
         * 2. Usar ObjectMapper.readValue() con TypeReference para List<User>
         * 3. Manejar excepciones de Jackson apropiadamente
         * 4. Retornar lista vacía si archivo está vacío
         * 
         * Clases requeridas:
         * - ObjectMapper (ya creado como campo)
         * - TypeReference<List<User>>
         * - File (para pasar a readValue)
         */
        
        // TODO: Implementar aquí
        throw new UnsupportedOperationException("TODO: Implementar readUsersFromJSON usando Jackson ObjectMapper");
        
        // PISTA: objectMapper.readValue(new File(filePath), new TypeReference<List<User>>() {});
    }

    @Override
    public boolean writeUsersToJSON(List<User> users, String filePath) {
        /*
         * TODO CE1.e: Implementar escritura de JSON usando Jackson
         * 
         * Pasos requeridos:
         * 1. Crear directorios padre si no existen
         * 2. Configurar ObjectMapper para pretty-print
         * 3. Usar ObjectMapper.writeValue() para escribir a archivo
         * 4. Manejar excepciones apropiadamente
         */
        
        // TODO: Implementar aquí
        throw new UnsupportedOperationException("TODO: Implementar writeUsersToJSON usando Jackson ObjectMapper");
    }

    // ========================================================================================
    // CE1.f: ESCRITURA Y LECTURA DE INFORMACIÓN EN OTROS FORMATOS ESTÁNDAR (CSV)
    // ========================================================================================

    @Override
    public List<User> readUsersFromCSV(String filePath) {
        /*
         * TODO CE1.f: Implementar lectura de CSV usando Java I/O vanilla
         * 
         * Pasos requeridos:
         * 1. Validar que el archivo existe usando Files.exists()
         * 2. Usar BufferedReader con FileReader para leer líneas
         * 3. Saltear la primera línea (cabeceras)
         * 4. Para cada línea: usar String.split(",") para separar campos
         * 5. Convertir cada línea a objeto User
         * 6. Manejar parsing de LocalDateTime desde String
         * 7. Usar try-with-resources para garantizar cierre de recursos
         * 8. Lanzar RuntimeException con mensaje descriptivo si hay errores
         * 
         * Clases Java I/O requeridas:
         * - Files, Paths (validación)
         * - FileReader, BufferedReader (lectura)
         * - String.split() (parsing)
         * - LocalDateTime.parse() (conversión fechas)
         */
        
        List<User> users = new ArrayList<>();
        
        // TODO: Implementar aquí
        throw new UnsupportedOperationException("TODO: Implementar readUsersFromCSV usando BufferedReader");
        
        // EJEMPLO de estructura esperada:
        // try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
        //     String line = reader.readLine(); // Saltear cabeceras
        //     while ((line = reader.readLine()) != null) {
        //         String[] fields = line.split(",");
        //         // TODO: Convertir fields a User
        //     }
        // } catch (IOException e) {
        //     throw new RuntimeException("Error leyendo CSV: " + e.getMessage(), e);
        // }
    }

    @Override
    public boolean writeUsersToCSV(List<User> users, String filePath) {
        /*
         * TODO CE1.f: Implementar escritura de CSV usando Java I/O vanilla
         * 
         * Pasos requeridos:
         * 1. Crear directorios padre si no existen usando Files.createDirectories()
         * 2. Usar PrintWriter con FileWriter para escribir
         * 3. Escribir línea de cabeceras CSV
         * 4. Para cada User: formatear campos separados por comas
         * 5. Manejar formato de LocalDateTime a String
         * 6. Usar try-with-resources
         * 7. Retornar true si exitoso, lanzar RuntimeException si error
         * 
         * Clases Java I/O requeridas:
         * - Files, Paths (crear directorios)
         * - FileWriter, PrintWriter (escritura)
         * - DateTimeFormatter (formateo fechas)
         */
        
        // TODO: Implementar aquí
        throw new UnsupportedOperationException("TODO: Implementar writeUsersToCSV usando PrintWriter");
    }


    // ========================================================================================
    // MÉTODOS AUXILIARES SUGERIDOS
    // ========================================================================================
    // Los estudiantes pueden crear estos métodos privados para ayudar en las implementaciones

    /**
     * TODO: Método auxiliar para convertir String CSV a User
     * @param csvLine Línea CSV con campos separados por comas
     * @return User creado desde la línea CSV
     */
    private User parseUserFromCSV(String csvLine) {
        // TODO: Implementar parsing de línea CSV a User
        throw new UnsupportedOperationException("TODO: Implementar parseUserFromCSV");
    }

    /**
     * TODO: Método auxiliar para convertir User a String CSV
     * @param user Usuario a convertir
     * @return Línea CSV con campos del usuario
     */
    private String userToCSV(User user) {
        // TODO: Implementar conversión de User a línea CSV
        throw new UnsupportedOperationException("TODO: Implementar userToCSV");
    }

    /**
     * TODO: Método auxiliar para crear directorios padre de un archivo
     * @param filePath Ruta del archivo
     */
    private void createParentDirectories(String filePath) {
        // TODO: Implementar creación de directorios padre
        // PISTA: Path parent = Paths.get(filePath).getParent();
        throw new UnsupportedOperationException("TODO: Implementar createParentDirectories");
    }
}
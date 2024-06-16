package com.alura.literalura.principal;

import com.alura.literalura.model.*;
import com.alura.literalura.repository.AutorRepository;
import com.alura.literalura.repository.LibroRepository;
import com.alura.literalura.service.ConsumoApi;
import com.alura.literalura.service.ConvierteDatos;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private final String URL_BASE = "https://gutendex.com/books/";
    private ConvierteDatos conversor = new ConvierteDatos();
    private LibroRepository libroRepository;
    private AutorRepository autorRepository;
    private List<Libro> libros;

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void muestraElMenu() throws JsonProcessingException {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                1 - Buscar libros por título
                2 - Listar libros registrados
                3 - Listar autores registrados
                4 - Listar autores vivos en un determinado año
                5 - Listar libros por idioma
                6 - Buscar autor por nombre
                0 - Salir
                """;
            System.out.println(menu);

            try {
                opcion = teclado.nextInt();
                teclado.nextLine();

                switch (opcion) {
                    case 1:
                        try {
                            buscarLibrosTitulo();
                        } catch (Exception e) {
                            System.out.println("Error al buscar libros por título: " + e.getMessage());
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        try {
                            listarLibrosRegistrados();
                        } catch (Exception e) {
                            System.out.println("Error al listar libros registrados: " + e.getMessage());
                            e.printStackTrace();
                        }
                        break;
                    case 3:
                        try {
                            listarAutoresRegistrados();
                        } catch (Exception e) {
                            System.out.println("Error al listar autores registrados: " + e.getMessage());
                            e.printStackTrace();
                        }
                        break;
                    case 4:
                        try {
                            listarAutoresVivosAnio();
                        } catch (Exception e) {
                            System.out.println("Error al listar autores vivos en un determinado año: " + e.getMessage());
                            e.printStackTrace();
                        }
                        break;
                    case 5:
                        try {
                            listarLibrosIdioma();
                        } catch (Exception e) {
                            System.out.println("Error al listar libros por idioma: " + e.getMessage());
                            e.printStackTrace();
                        }
                        break;
                    case 6:
                        try {
                            listarAutoresNombre();
                        } catch (Exception e) {
                            System.out.println("Error al buscar autores por nombre: " + e.getMessage());
                            e.printStackTrace();
                        }
                        break;
                    case 0:
                        System.out.println("Cerrando la aplicación...");
                        break;
                    default:
                        System.out.println("Opción inválida");
                }
            } catch (InputMismatchException e) {
                System.out.println("Ingrese una opción válida (número).");
                teclado.nextLine();
            } catch (Exception e) {
                System.out.println("Se produjo un error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void listarAutoresNombre() {
        System.out.println("Ingrese el nombre o parte del nombre del autor que desea buscar:");
        String nombreAutor = teclado.nextLine();
        List<Autor> autoresEncontrados = autorRepository.findAutorByNombreContainingIgnoreCase(nombreAutor);
        if (autoresEncontrados.isEmpty()) {
            System.out.println("No se encontraron autores con el nombre especificado.");
        } else {
            System.out.println("Autores encontrados:");
            for (Autor autor : autoresEncontrados) {
                System.out.println(autor);
            }
        }
    }


    @Transactional
    protected List<DatosLibro> getDatosLibros(String nombreLibro) throws JsonProcessingException {
        var json = consumoApi.obtenerDatos(URL_BASE + "?search=" + nombreLibro);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(json);
        List<DatosLibro> datosLibros = new ArrayList<>();
        for (JsonNode resultNode : node.get("results")) {
            JsonNode idiomasNode = resultNode.get("languages");
            String idiomaApi = idiomasNode.get(0).asText();
            Idioma idioma = Idioma.fromString(idiomaApi);

            String titulo = resultNode.get("title").asText();
            List<DatosAutor> datosAutores = new ArrayList<>();
            List<Autor> autores = new ArrayList<>();
            for (JsonNode autorNode : resultNode.get("authors")) {
                String nombre = autorNode.get("name").asText();
                String anioNacimiento = autorNode.get("birth_year").asText();
                String anioFallecimiento = autorNode.get("death_year").asText();
                DatosAutor datosAutor = new DatosAutor(nombre, anioNacimiento, anioFallecimiento);
                Autor autor = autorRepository.findByNombre(nombre);
                if (autor == null) {
                    autor = new Autor(datosAutor);
                    autorRepository.save(autor);
                }
                datosAutores.add(datosAutor);
                autores.add(autor);
            }
            String descargas = resultNode.get("download_count").asText();
            DatosLibro datosLibro = new DatosLibro(titulo, datosAutores, idioma, descargas);
            datosLibros.add(datosLibro);
            List<Libro> librosExistentes = libroRepository.findByTituloContainingIgnoreCase(titulo);
            if (librosExistentes.isEmpty()){
                Libro libro = new Libro(datosLibro,autores);
                try{
                    libroRepository.save(libro);
                }catch (Exception ignored){}
            }
        }
        return datosLibros;
    }

    private void listarLibrosIdioma() {
        System.out.println("Ingrese el idioma para buscar libros (Español, Ingles, Otro):");
        String idiomaInput = teclado.nextLine();
        if(idiomaInput.equalsIgnoreCase("español")||idiomaInput.equalsIgnoreCase("ingles")
        || idiomaInput.equalsIgnoreCase("otro")){
            Idioma idioma;
            try {
                idioma = Idioma.fromEspaniol(idiomaInput);
            } catch (IllegalArgumentException e) {
                System.out.println("Idioma no válido. Por favor ingrese 'Español', 'Ingles' o 'Otro'.");
                return;
            }

            List<Libro> librosEnIdioma = libroRepository.findByIdioma(idioma);
            if (librosEnIdioma.isEmpty()) {
                System.out.println("No hay libros registrados en el idioma " + idiomaInput + ".");
            } else {
                System.out.println("Libros registrados en el idioma " + idiomaInput + ":");
                for (Libro libro : librosEnIdioma) {
                    System.out.println(libro);
                }
            }
        }else
            System.out.println("Idioma no válido. Por favor ingrese 'Español', 'Ingles' o 'Otro'.");
    }

    private void listarAutoresVivosAnio() {
        System.out.println("Ingrese el año para buscar autores vivos:");
        int anio = Integer.parseInt(teclado.nextLine());

        List<Autor> autoresVivos = autorRepository.findAutoresVivosAnio(anio);
        if (autoresVivos.isEmpty()) {
            System.out.println("No hay autores vivos en el año " + anio);
        } else {
            System.out.println("Autores vivos en el año " + anio + ":");
            for (Autor autor : autoresVivos) {
                System.out.println(autor);
            }
        }
    }

    private void listarAutoresRegistrados() {
        List<Autor> autoresRegistrados = autorRepository.findAll();
        if(autoresRegistrados.isEmpty()){
            System.out.println("No hay autores registrados");
        }else{
            System.out.println("Autores registrados:");
            for (Autor autor:autoresRegistrados){
                System.out.println(autor);
            }
        }
    }

    private void listarLibrosRegistrados() {
        List<Libro> librosRegistrados = libroRepository.findAll();
        if (librosRegistrados.isEmpty()) {
            System.out.println("No hay libros registrados.");
        } else {
            System.out.println("Libros registrados:");
            for (Libro libro : librosRegistrados) {
                System.out.println(libro);
            }
        }
    }

    protected void buscarLibrosTitulo() throws JsonProcessingException {
        System.out.println("Escribe el nombre del libro que deseas buscar");
        var nombreLibro = teclado.nextLine();
        List<Libro> l = libroRepository.findByTituloContainingIgnoreCase(nombreLibro);
        System.out.println(l);
        if (l.isEmpty()) {
            List<DatosLibro> datos=getDatosLibros(nombreLibro);
            if(datos.isEmpty()){
                System.out.println("No hay libros con el titulo buscado");
            }
            for (DatosLibro datosLibro : datos) {
                List<Libro> libros = libroRepository.findByTituloContainingIgnoreCase(datosLibro.titulo());
                libros.forEach(System.out::println);
            }
        }else{
            l.forEach(System.out::println);
        }
    }
}



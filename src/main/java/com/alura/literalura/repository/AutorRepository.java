package com.alura.literalura.repository;

import com.alura.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AutorRepository extends JpaRepository<Autor,Long> {
    Autor findByNombre(String nombre);

    @Query("SELECT a FROM Autor a WHERE (a.anioNacimiento <= :anio AND a.anioNacimiento!=0) AND a.anioFallecimiento >= :anio")
    List<Autor> findAutoresVivosAnio(int anio);
    List<Autor> findAutorByNombreContainingIgnoreCase(String nombre);
}

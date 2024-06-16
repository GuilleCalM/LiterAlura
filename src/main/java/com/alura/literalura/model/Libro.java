package com.alura.literalura.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "libro")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String titulo;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "libro_autor",
            joinColumns = @JoinColumn(name = "libro_id"),
            inverseJoinColumns = @JoinColumn(name = "autor_id")
    )
    private List<Autor> autor=new ArrayList<>();
    @Enumerated(EnumType.STRING)
    private Idioma idioma;
    private int numeroDescargas;

    public Libro(DatosLibro datos,List<Autor> autores) {
        this.titulo= datos.titulo();
        this.idioma = datos.idioma();
        try{
            this.numeroDescargas= Integer.parseInt(datos.descargas());
        }catch(NumberFormatException e){
            this.numeroDescargas= 0;
        }
        this.autor=autores;
    }

    @Override
    public String toString() {
        return "Libro{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", idioma=" + idioma +
                ", numeroDescargas=" + numeroDescargas +
                '}';
    }
}

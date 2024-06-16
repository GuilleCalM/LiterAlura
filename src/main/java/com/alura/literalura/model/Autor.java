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
@Table(name = "autor")
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String nombre;
    private Integer anioNacimiento;
    private Integer anioFallecimiento;
    @ManyToMany(fetch = FetchType.EAGER,mappedBy = "autor",cascade = CascadeType.ALL)
    private List<Libro> libros=new ArrayList<>();

    public Autor(DatosAutor datos){
        this.nombre= datos.nombre();
        try{
            this.anioNacimiento= Integer.valueOf(datos.anioNacimiento());
        }catch (NumberFormatException e){
            this.anioNacimiento= 0;
        }
        try {
            this.anioFallecimiento= Integer.valueOf(datos.anioFallecimiento());
        }catch (NumberFormatException e){
            this.anioFallecimiento= 0;
        }
    }

    @Override
    public String toString() {
        return "Autor [" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", anioNacimiento=" + anioNacimiento +
                ", anioFallecimiento=" + anioFallecimiento +
                ']';
    }
}

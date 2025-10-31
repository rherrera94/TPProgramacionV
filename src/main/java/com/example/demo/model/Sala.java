package com.example.demo.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="salas")

public class Sala {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSala;

    @NotBlank(message = "El nombre de la sala no puede estar vacío")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Min(value = 1, message = "La capacidad debe ser de al menos 1")
    @Column(name = "capacidad", nullable = false)
    private int capacidad;

}

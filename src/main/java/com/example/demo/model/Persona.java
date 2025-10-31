package com.example.demo.model;

import jakarta.persistence.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

public class Persona {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "id_persona")
    private Long idPersona;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min =2, max=100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El formato del email no es válido")
    @Column(unique = true)
    private String email;

//    @NotNull(message = "El rol no puede ser nulo")
//    @ManyToOne
//    @JoinColumn(name = "rol_name")
//    private Rol rol;


//    public void setRol(Rol rol) {
//        this.rol = rol;
//    }


}

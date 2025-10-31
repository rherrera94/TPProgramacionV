package com.example.demo.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;


@Entity
@Table(name = "reservas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull(message = "La reserva debe estar asociada a una persona")
    @ManyToOne
    @JoinColumn(name="id_persona", nullable = false)
    private Persona persona;

    @ManyToOne
    @JoinColumn(name="id_sala")
    private Sala sala;

    @ManyToOne
    @JoinColumn(name="id_articulo")
    private Articulos articulo;

    @NotNull(message = "La fecha de inicio no puede estar vacía")
    @Column(name = "fecha_hora_inicio", nullable = false)
    private LocalDateTime fechaHoraInicio;


    @NotNull(message = "La fecha de fin no puede estar vacía")
    @Future(message = "La fecha de fin debe ser en el futuro")
    @Column(name="fecha_hora_fin", nullable = false)
    private LocalDateTime fechaHoraFin;

}

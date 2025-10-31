package com.example.demo.repository;

import com.example.demo.model.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional; // 1. IMPORTAMOS ESTO

@Repository
public interface Persona_Repositorio extends JpaRepository <Persona, Long> {

    /**
     * Busca una persona por su email.
     * Usamos Optional porque el email es ÚNICO.
     * Devuelve un Optional que contiene la Persona si la encuentra,
     * o un Optional vacío si no.
     */
    Optional<Persona> findByEmail(String email); // 2. CAMBIAMOS ESTO

    /**
     * Busca personas por su nombre.
     * Usamos List porque el nombre SÍ se puede repetir.
     */
    List<Persona> findByNombre (String nombre);

}
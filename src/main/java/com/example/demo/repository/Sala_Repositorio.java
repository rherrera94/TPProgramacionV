package com.example.demo.repository;

import com.example.demo.model.Sala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // 1. IMPORTAR ESTO

@Repository
public interface Sala_Repositorio extends JpaRepository<Sala, Long> {

    /**
     * Busca una sala por su nombre.
     * Usamos Optional porque el nombre debe ser ÚNICO.
     */
    Optional<Sala> findByNombre(String nombre); // 2. AGREGAR ESTO

}
package com.example.demo.repository;

import com.example.demo.model.Articulos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // 1. IMPORTAR ESTO

@Repository
public interface Articulos_Repositorio extends JpaRepository<Articulos, Long> {

    /**
     * Busca un artículo por su nombre.
     * Usamos Optional porque el nombre debe ser ÚNICO.
     */
    Optional<Articulos> findByNombre(String nombre); // 2. CAMBIAR TIPO DE RETORNO

}
package com.example.demo.repository;

import com.example.demo.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime; // 1. Importar
import java.util.List;          // 2. Importar

@Repository // 3. Mantenemos tu anotación
public interface Reserva_Repositorio extends JpaRepository<Reserva, Long> {

    // --- 4. PEGAR TODO ESTO ABAJO ---

    // --- LÓGICA DE CONFLICTOS PARA SALAS ---

    /**
     * Busca reservas de una SALA que se solapen con un rango de tiempo.
     * (StartA < EndB) AND (EndA > StartB)
     */
    @Query("SELECT r FROM Reserva r WHERE r.sala.idSala = :salaId " + // Corregido a idSala
            "AND r.fechaHoraInicio < :fin " +
            "AND r.fechaHoraFin > :inicio")
    List<Reserva> findConflictosDeSala(
            @Param("salaId") Long salaId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    /**
     * Igual que el anterior, PERO excluye una reserva existente (para actualizar).
     */
    @Query("SELECT r FROM Reserva r WHERE r.sala.idSala = :salaId " + // Corregido a idSala
            "AND r.id != :reservaId " + // <-- La diferencia clave
            "AND r.fechaHoraInicio < :fin " +
            "AND r.fechaHoraFin > :inicio")
    List<Reserva> findConflictosDeSalaActualizar(
            @Param("salaId") Long salaId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin,
            @Param("reservaId") Long reservaId);

    // --- LÓGICA DE CONFLICTOS PARA ARTÍCULOS ---

    /**
     * Busca reservas de un ARTICULO que se solapen con un rango de tiempo.
     */
    @Query("SELECT r FROM Reserva r WHERE r.articulo.idArticulo = :articuloId " +
            "AND r.fechaHoraInicio < :fin " +
            "AND r.fechaHoraFin > :inicio")
    List<Reserva> findConflictosDeArticulo(
            @Param("articuloId") Long articuloId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    /**
     * Igual, pero excluye una reserva existente (para actualizar).
     */
    @Query("SELECT r FROM Reserva r WHERE r.articulo.idArticulo = :articuloId " +
            "AND r.id != :reservaId " + // <-- La diferencia clave
            "AND r.fechaHoraInicio < :fin " +
            "AND r.fechaHoraFin > :inicio")
    List<Reserva> findConflictosDeArticuloActualizar(
            @Param("articuloId") Long articuloId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin,
            @Param("reservaId") Long reservaId);
}
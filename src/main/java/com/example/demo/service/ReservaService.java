package com.example.demo.service;

import com.example.demo.model.Reserva;
import com.example.demo.repository.Reserva_Repositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservaService {

    private final Reserva_Repositorio reservaRepository;
    private final ArticulosService articulosService;

    @Autowired
    public ReservaService(Reserva_Repositorio reservaRepository, ArticulosService articulosService) {
        this.reservaRepository = reservaRepository;
        this.articulosService = articulosService;
    }

    // Método para obtener todas las reservas
    public List<Reserva> obtenerTodasLasReservas() {
        return reservaRepository.findAll();
    }

    // Método para crear una reserva
    public Reserva crearReserva(Reserva reserva) {
        reserva.setId(null);
        validarReglasDeNegocio(reserva);

        if(reserva.getArticulo() != null){
            articulosService.marcarComoReservado(reserva.getArticulo().getIdArticulo());
        }
        return reservaRepository.save(reserva);
    }

    public Reserva actualizarReserva(Reserva reserva) {
        if(reserva.getId() == null) {
            throw new RuntimeException("Se necesita un ID para actualziar la reserva");
        }

        Reserva reservaAnterior = reservaRepository.findById(reserva.getId())
                .orElseThrow(() -> new RuntimeException("Error: Reserva no encontrada con ID " + reserva.getId()));

        validarReglasDeNegocio(reserva);

        Long idArticuloNuevo = (reserva.getArticulo() != null) ? reserva.getArticulo().getIdArticulo() : null;
        Long idArticuloAnterior = (reservaAnterior.getArticulo() != null) ? reservaAnterior.getArticulo().getIdArticulo() : null;

        if(idArticuloAnterior != null && !idArticuloAnterior.equals(idArticuloNuevo)) {
            articulosService.marcarComoLibre(idArticuloAnterior);
        }

        if(idArticuloNuevo != null && !idArticuloNuevo.equals(idArticuloAnterior)) {
            articulosService.marcarComoReservado(idArticuloNuevo);
        }

        return reservaRepository.save(reserva);

    }


    /**
     * Borra una reserva por su ID.
     */
    public void borrarReserva(Long id) {
        // (Opcional: chequear si existe antes de borrar)
        Reserva reservaABorrar = reservaRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));

        if(reservaABorrar.getArticulo() != null) {
            articulosService.marcarComoLibre(reservaABorrar.getArticulo().getIdArticulo());
        }

        reservaRepository.deleteById(id);
    }

    private void validarReglasDeNegocio(Reserva reserva) {
        if(reserva.getFechaHoraFin().isBefore(reserva.getFechaHoraInicio())) {
            throw new RuntimeException("Error: La fecha de Fin no puede ser anterior a la de Inicio");
        }

        boolean tieneSala = reserva.getSala() != null;
        boolean tieneArticulo = reserva.getArticulo() != null;

        if (tieneSala && tieneArticulo) {
            throw new RuntimeException("Error: La reserva no puede ser para una sala Y un artículo");
        }

        if (!tieneSala && !tieneArticulo) {
            throw new RuntimeException("Error: La reserva debe ser para una sala O para un artículo");
        }

        List<Reserva> conflictos;
        Long reservaId = reserva.getId();
        LocalDateTime inicio = reserva.getFechaHoraInicio();
        LocalDateTime fin = reserva.getFechaHoraFin();

        if(tieneSala){
            Long salaId = reserva.getSala().getIdSala();

            if(reservaId == null){
                conflictos = reservaRepository.findConflictosDeSala(salaId, inicio, fin);
            }else {
                conflictos = reservaRepository.findConflictosDeSalaActualizar(salaId, inicio, fin, reservaId);
            }

            if(!conflictos.isEmpty()){
                throw new RuntimeException("Conflicto: La sala ya está reservada en ese horario");

            }
        }else{
            Long articuloId = reserva.getArticulo().getIdArticulo();

            if(reservaId == null){
                conflictos = reservaRepository.findConflictosDeArticulo(articuloId, inicio, fin);
            }else{
                conflictos = reservaRepository.findConflictosDeArticuloActualizar(articuloId, inicio, fin, reservaId);
            }

            if(!conflictos.isEmpty()){
                throw new RuntimeException("Conflicto: El artículo ya está reservado en ese horario");
            }
        }

    }
}

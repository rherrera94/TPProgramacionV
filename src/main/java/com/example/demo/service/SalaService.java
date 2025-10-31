package com.example.demo.service;

import com.example.demo.model.Sala;
import com.example.demo.repository.Sala_Repositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class SalaService {

    private final  Sala_Repositorio sala_Repositorio;

    @Autowired
    public SalaService(Sala_Repositorio sala_Repositorio) {
        this.sala_Repositorio = sala_Repositorio;
    }

    public List<Sala> obtenerTodasLasSalas(){
        return sala_Repositorio.findAll();
    }

    public Optional<Sala> obtenerSalaPorId(Long id){
        return sala_Repositorio.findById(id);
    }

    public Sala crearSala(Sala sala){
        // 🧠 LÓGICA DE NEGOCIO:
        // Buscamos si ya existe una sala con ese nombre
        Optional<Sala> existente = sala_Repositorio.findByNombre(sala.getNombre());
        if (existente.isPresent()) {
            throw new RuntimeException("Error: El nombre de la sala '" + sala.getNombre() + "' ya está en uso.");
        }

        // Si no existe, la creamos
        return sala_Repositorio.save(sala);
    }

    public Sala actualizarSala(Sala sala){
        if(sala.getIdSala() == null){
            throw new RuntimeException("Se necesita un ID para actualizar la sala.");
        }
        if(!sala_Repositorio.existsById(sala.getIdSala())){
            throw new RuntimeException("Error: Sala no encontrada con ID " +  sala.getIdSala());
        }

        Optional<Sala> existente = sala_Repositorio.findByNombre(sala.getNombre());
        if (existente.isPresent() && !existente.get().getIdSala().equals(sala.getIdSala())) {
            throw new RuntimeException("Error: El nombre de la sala '" + sala.getNombre() + "' Ya está en uso por otra sala");
        }

        return sala_Repositorio.save(sala);
    }

    public void borrarSala(Long id){
        if(!sala_Repositorio.existsById(id)){
            throw new RuntimeException("Error: Sala no encontrada con ID " +  id);
        }
        sala_Repositorio.deleteById(id);
    }
}

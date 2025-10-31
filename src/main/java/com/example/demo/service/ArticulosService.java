package com.example.demo.service;

import com.example.demo.model.Articulos;
import com.example.demo.repository.Articulos_Repositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArticulosService {
    private final Articulos_Repositorio articulos_repositorio;

    @Autowired

    public ArticulosService(Articulos_Repositorio articulos_repositorio) {
        this.articulos_repositorio = articulos_repositorio;
    }

    public List<Articulos> listarArticulos() {
        return articulos_repositorio.findAll();
    }

    public Optional<Articulos> buscarPorId (Long idArticulo) {
        return articulos_repositorio.findById(idArticulo);
    }

    public void marcarComoReservado(Long idArticulo){
        Articulos articulo = articulos_repositorio.findById(idArticulo)
                .orElseThrow(() -> new RuntimeException("Error: Articulo " + idArticulo + " no existe."));
        articulo.setDisponible(false);
        articulos_repositorio.save(articulo);
    }

    public void marcarComoLibre(Long idArticulo){
        Articulos articulo = articulos_repositorio.findById(idArticulo)
                .orElseThrow(() -> new RuntimeException("Error: Articulo " + idArticulo + " no existe."));

        articulo.setDisponible(true);
        articulos_repositorio.save(articulo);

    }



    public void addArticulo(Articulos articulo){
        Optional<Articulos> existente = articulos_repositorio.findByNombre(articulo.getNombre());
        if(existente.isPresent()){
            throw new RuntimeException("Error: El nombre de articulo "+ articulo.getNombre() + " ya existe");
        }

        articulos_repositorio.save(articulo);
    }

    public void updateArticulo(Articulos articulo) {
        if(articulo.getIdArticulo() == null){
            throw new RuntimeException("Error: Se necesita un ID para actualizar el articulo");
        }

        if(!articulos_repositorio.existsById(articulo.getIdArticulo())){
            throw new RuntimeException("Error: El articulo no existe con ID " + articulo.getIdArticulo());
        }

        Optional<Articulos> existente = articulos_repositorio.findByNombre(articulo.getNombre());

        if(existente.isPresent() && !existente.get().getIdArticulo().equals(articulo.getIdArticulo())){
            throw new RuntimeException("Error: El nombre del articulo " + articulo.getNombre() + " ya existe");
        }

        articulos_repositorio.save(articulo);
    }

    public void deleteArticulo(Long idArticulo) {
        if(!articulos_repositorio.existsById(idArticulo)){
            throw new RuntimeException("Error: El articulo no existe con ID: " + idArticulo);
        }

        articulos_repositorio.deleteById(idArticulo);
    }

    public Optional<Articulos> buscarPorNombre (String nombre) {
        return articulos_repositorio.findByNombre(nombre);
    }


}

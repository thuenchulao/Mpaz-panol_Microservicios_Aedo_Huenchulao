package com.mpaz.inventarioservice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mpaz.inventarioservice.entity.Equipo;
import com.mpaz.inventarioservice.repository.EquipoRepository;

@Service
public class EquipoServiceImpl implements EquipoService {

    private final EquipoRepository repository;

    public EquipoServiceImpl(EquipoRepository repository) {
        this.repository = repository;
    }

    // listar
    @Override
    public List<Equipo> listar(String categoria, String estado) {
        if (categoria != null) {
            return repository.findByCategoria(categoria);
        }
        if (estado != null) {
            return repository.findByEstado(estado);
        }
        return repository.findAll();
    }

    @Override
    public Optional<Equipo> obtenerPorId(Long id) {
        return repository.findById(id);
    }

    // crear y actualizar
    @Override
    public Equipo crear(Equipo equipo) {
        equipo.setId(null);
        return repository.save(equipo);
    }

    @Override
    public Optional<Equipo> actualizar(Long id, Equipo equipo) {
        return repository.findById(id).map(existente -> {
            existente.setNombre(equipo.getNombre());
            existente.setMarca(equipo.getMarca());
            existente.setModelo(equipo.getModelo());
            existente.setCategoria(equipo.getCategoria());
            existente.setStockTotal(equipo.getStockTotal());
            existente.setStockDisponible(equipo.getStockDisponible());
            existente.setEstado(equipo.getEstado());
            existente.setUbicacion(equipo.getUbicacion());
            return repository.save(existente);
        });
    }

    // stock
    @Override
    public Optional<Equipo> descontarStock(Long id, int cantidad) {
        return repository.findById(id).map(equipo -> {
            if (cantidad > equipo.getStockDisponible()) {
                throw new IllegalArgumentException("Stock insuficiente");
            }
            equipo.setStockDisponible(equipo.getStockDisponible() - cantidad);
            return repository.save(equipo);
        });
    }

    @Override
    public Optional<Equipo> reponerStock(Long id, int cantidad) {
        return repository.findById(id).map(equipo -> {
            int nuevo = equipo.getStockDisponible() + cantidad;
            equipo.setStockDisponible(Math.min(nuevo, equipo.getStockTotal()));
            return repository.save(equipo);
        });
    }
}
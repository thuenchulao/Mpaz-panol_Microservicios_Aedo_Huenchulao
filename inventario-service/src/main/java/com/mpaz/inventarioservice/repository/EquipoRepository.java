package com.mpaz.inventarioservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mpaz.inventarioservice.entity.Equipo;

public interface EquipoRepository extends JpaRepository<Equipo, Long> {

    // filtros
    List<Equipo> findByCategoria(String categoria);

    List<Equipo> findByEstado(String estado);
}
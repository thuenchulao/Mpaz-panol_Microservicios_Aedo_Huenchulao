package com.mpaz.inventarioservice.service;

import java.util.List;
import java.util.Optional;

import com.mpaz.inventarioservice.entity.Equipo;

public interface EquipoService {

    List<Equipo> listar(String categoria, String estado);

    Optional<Equipo> obtenerPorId(Long id);

    Equipo crear(Equipo equipo);

    Optional<Equipo> actualizar(Long id, Equipo equipo);

    // stock
    Optional<Equipo> descontarStock(Long id, int cantidad);

    Optional<Equipo> reponerStock(Long id, int cantidad);
}
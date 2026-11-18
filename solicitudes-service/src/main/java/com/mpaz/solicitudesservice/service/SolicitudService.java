package com.mpaz.solicitudesservice.service;

import java.util.List;
import java.util.Optional;

import com.mpaz.solicitudesservice.entity.Solicitud;

public interface SolicitudService {

    List<Solicitud> listar(String email, String estado);

    Optional<Solicitud> obtenerPorId(Long id);

    Solicitud crear(Solicitud solicitud);


    Optional<Solicitud> aceptar(Long id);

    Optional<Solicitud> rechazar(Long id, String comentario);

    Optional<Solicitud> entregar(Long id);

    Optional<Solicitud> devolver(Long id);
}

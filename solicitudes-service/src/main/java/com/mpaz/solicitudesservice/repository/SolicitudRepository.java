package com.mpaz.solicitudesservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mpaz.solicitudesservice.entity.Solicitud;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    List<Solicitud> findBySolicitanteEmail(String solicitanteEmail);

    List<Solicitud> findByEstado(String estado);
}

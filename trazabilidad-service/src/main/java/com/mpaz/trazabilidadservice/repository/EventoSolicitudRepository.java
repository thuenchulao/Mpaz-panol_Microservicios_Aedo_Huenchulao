package com.mpaz.trazabilidadservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mpaz.trazabilidadservice.entity.EventoSolicitud;

public interface EventoSolicitudRepository extends JpaRepository<EventoSolicitud, Long> {

    // historial de una solicitud
    List<EventoSolicitud> findBySolicitudIdOrderByFechaHoraAsc(Long solicitudId);

    // filtro por estado
    List<EventoSolicitud> findByEstado(String estado);
}
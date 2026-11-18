package com.mpaz.trazabilidadservice.service;

import java.util.List;

import com.mpaz.trazabilidadservice.entity.EventoSolicitud;

public interface EventoService {

    EventoSolicitud registrar(EventoSolicitud evento);

    List<EventoSolicitud> historial(Long solicitudId);

    List<EventoSolicitud> listar(String estado);
}
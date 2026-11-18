package com.mpaz.trazabilidadservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mpaz.trazabilidadservice.entity.EventoSolicitud;
import com.mpaz.trazabilidadservice.repository.EventoSolicitudRepository;

@Service
public class EventoServiceImpl implements EventoService {

    private static final List<String> ESTADOS =
            List.of("Solicitado", "Aceptada", "Rechazada", "Entregada", "Devuelta");

    private final EventoSolicitudRepository repository;

    public EventoServiceImpl(EventoSolicitudRepository repository) {
        this.repository = repository;
    }

    // registrar
    @Override
    public EventoSolicitud registrar(EventoSolicitud evento) {
        if (evento.getSolicitudId() == null) {
            throw new IllegalArgumentException("Falta el id de la solicitud");
        }
        if (evento.getEstado() == null || !ESTADOS.contains(evento.getEstado())) {
            throw new IllegalArgumentException("Estado no válido");
        }
        if (evento.getEstado().equals("Rechazada")
                && (evento.getComentario() == null || evento.getComentario().isBlank())) {
            throw new IllegalArgumentException("El rechazo requiere justificación");
        }
        evento.setId(null);
        evento.setFechaHora(LocalDateTime.now());
        return repository.save(evento);
    }

    // consultar
    @Override
    public List<EventoSolicitud> historial(Long solicitudId) {
        return repository.findBySolicitudIdOrderByFechaHoraAsc(solicitudId);
    }

    @Override
    public List<EventoSolicitud> listar(String estado) {
        if (estado != null) {
            return repository.findByEstado(estado);
        }
        return repository.findAll();
    }
}
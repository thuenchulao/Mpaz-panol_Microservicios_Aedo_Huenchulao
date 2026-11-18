package com.mpaz.solicitudesservice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mpaz.solicitudesservice.entity.Solicitud;
import com.mpaz.solicitudesservice.repository.SolicitudRepository;

@Service
public class SolicitudServiceImpl implements SolicitudService {

    private final SolicitudRepository repository;

    public SolicitudServiceImpl(SolicitudRepository repository) {
        this.repository = repository;
    }

    
    @Override
    public List<Solicitud> listar(String email, String estado) {
        if (email != null) {
            return repository.findBySolicitanteEmail(email);
        }
        if (estado != null) {
            return repository.findByEstado(estado);
        }
        return repository.findAll();
    }

    @Override
    public Optional<Solicitud> obtenerPorId(Long id) {
        return repository.findById(id);
    }

   
    @Override
    public Solicitud crear(Solicitud solicitud) {
        solicitud.setId(null);
        solicitud.setEstado("Pendiente");
        solicitud.setComentario(null);
        return repository.save(solicitud);
    }

    @Override
    public Optional<Solicitud> aceptar(Long id) {
        return cambiarEstado(id, "Pendiente", "Aceptada");
    }

    @Override
    public Optional<Solicitud> rechazar(Long id, String comentario) {
        if (comentario == null || comentario.isBlank()) {
            throw new IllegalArgumentException("El rechazo requiere justificación");
        }
        return repository.findById(id).map(solicitud -> {
            if (!solicitud.getEstado().equals("Pendiente")) {
                throw new IllegalArgumentException("La solicitud no está Pendiente");
            }
            solicitud.setEstado("Rechazada");
            solicitud.setComentario(comentario);
            return repository.save(solicitud);
        });
    }

    @Override
    public Optional<Solicitud> entregar(Long id) {
        return cambiarEstado(id, "Aceptada", "Entregada");
    }

    @Override
    public Optional<Solicitud> devolver(Long id) {
        return cambiarEstado(id, "Entregada", "Devuelta");
    }

  
    private Optional<Solicitud> cambiarEstado(Long id, String estadoActual, String estadoNuevo) {
        return repository.findById(id).map(solicitud -> {
            if (!solicitud.getEstado().equals(estadoActual)) {
                throw new IllegalArgumentException("La solicitud no está " + estadoActual);
            }
            solicitud.setEstado(estadoNuevo);
            return repository.save(solicitud);
        });
    }
}

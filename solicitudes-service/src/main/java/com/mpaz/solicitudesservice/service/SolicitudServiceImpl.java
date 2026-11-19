package com.mpaz.solicitudesservice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.mpaz.solicitudesservice.entity.Solicitud;
import com.mpaz.solicitudesservice.entity.SolicitudItem;
import com.mpaz.solicitudesservice.repository.SolicitudRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SolicitudServiceImpl implements SolicitudService {

    private static final String URL_INVENTARIO = "http://localhost:8081/api/equipos";
    private static final String URL_TRAZABILIDAD = "http://localhost:8083/api/eventos";

    private final SolicitudRepository repository;
    private final WebClient.Builder webClientBuilder;

    public SolicitudServiceImpl(SolicitudRepository repository, WebClient.Builder webClientBuilder) {
        this.repository = repository;
        this.webClientBuilder = webClientBuilder;
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
        solicitud.setFechaCreacion(java.time.LocalDateTime.now().toString());
        Solicitud guardada = repository.save(solicitud);
        registrarEvento(guardada, guardada.getSolicitanteEmail());
        return guardada;
    }

    @Override
    public Optional<Solicitud> aceptar(Long id) {
        return repository.findById(id).map(solicitud -> {
            validarEstado(solicitud, "Pendiente");
            descontarStock(solicitud);
            return guardarEstado(solicitud, "Aceptada", null);
        });
    }

    @Override
    public Optional<Solicitud> rechazar(Long id, String comentario) {
        if (comentario == null || comentario.isBlank()) {
            throw new IllegalArgumentException("El rechazo requiere justificación");
        }
        return repository.findById(id).map(solicitud -> {
            validarEstado(solicitud, "Pendiente");
            return guardarEstado(solicitud, "Rechazada", comentario);
        });
    }

    @Override
    public Optional<Solicitud> entregar(Long id) {
        return repository.findById(id).map(solicitud -> {
            validarEstado(solicitud, "Aceptada");
            return guardarEstado(solicitud, "Entregada", null);
        });
    }

    @Override
    public Optional<Solicitud> devolver(Long id) {
        return repository.findById(id).map(solicitud -> {
            validarEstado(solicitud, "Entregada");
            reponerStock(solicitud.getItems());
            return guardarEstado(solicitud, "Devuelta", null);
        });
    }

    // validar estado actual
    private void validarEstado(Solicitud solicitud, String estadoEsperado) {
        if (!estadoEsperado.equals(solicitud.getEstado())) {
            throw new IllegalArgumentException("La solicitud no está " + estadoEsperado);
        }
    }

    // guardar nuevo estado + evento
    private Solicitud guardarEstado(Solicitud solicitud, String estado, String comentario) {
        solicitud.setEstado(estado);
        if (comentario != null) {
            solicitud.setComentario(comentario);
        }
        Solicitud guardada = repository.save(solicitud);
        registrarEvento(guardada, "Administrador");
        return guardada;
    }

    // llamada a inventario-service: descontar (con compensación)
    private void descontarStock(Solicitud solicitud) {
        List<SolicitudItem> descontados = new ArrayList<>();
        try {
            for (SolicitudItem item : solicitud.getItems()) {
                webClientBuilder.build()
                        .patch()
                        .uri(URL_INVENTARIO + "/{id}/descontar?cantidad={c}", item.getEquipoId(), item.getCantidad())
                        .retrieve()
                        .toBodilessEntity()
                        .block();
                descontados.add(item);
            }
        } catch (Exception e) {
            log.error("Error al descontar stock: {}", e.getMessage());
            reponerStock(descontados);
            throw new IllegalArgumentException("No hay stock suficiente o un equipo no existe");
        }
    }

    // llamada a inventario-service: reponer
    private void reponerStock(List<SolicitudItem> items) {
        for (SolicitudItem item : items) {
            try {
                webClientBuilder.build()
                        .patch()
                        .uri(URL_INVENTARIO + "/{id}/reponer?cantidad={c}", item.getEquipoId(), item.getCantidad())
                        .retrieve()
                        .toBodilessEntity()
                        .block();
            } catch (Exception e) {
                log.error("Error al reponer el equipo {}: {}", item.getEquipoId(), e.getMessage());
            }
        }
    }

    // llamada a trazabilidad-service
    private void registrarEvento(Solicitud solicitud, String actor) {
        try {
            Map<String, Object> evento = new HashMap<>();
            evento.put("solicitudId", solicitud.getId());
            evento.put("estado", solicitud.getEstado());
            evento.put("actor", actor);
            evento.put("comentario", solicitud.getComentario());
            webClientBuilder.build()
                    .post()
                    .uri(URL_TRAZABILIDAD)
                    .bodyValue(evento)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            log.warn("No se pudo registrar el evento de la solicitud {}: {}", solicitud.getId(), e.getMessage());
        }
    }
}
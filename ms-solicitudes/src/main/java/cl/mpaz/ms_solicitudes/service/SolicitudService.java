package cl.mpaz.ms_solicitudes.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.mpaz.ms_solicitudes.model.Solicitud;
import cl.mpaz.ms_solicitudes.repository.SolicitudRepository;

@Service
public class SolicitudService {

    @Autowired
    private SolicitudRepository solicitudRepository;

    // admin
    public List<Solicitud> obtenerTodas() {
        return solicitudRepository.findAll();
    }

    // Listar las de un solicitante
    public List<Solicitud> obtenerPorEmail(String email) {
        return solicitudRepository.findBySolicitanteEmail(email);
    }

    // Buscar por ID
    public Solicitud buscarPorId(Long id) {
        return solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
    }

    // Crear solicitud nueva
    public Solicitud guardar(Solicitud solicitud) {
        solicitud.setEstado("Pendiente");
        return solicitudRepository.save(solicitud);
    }

    // Aceptar
    public Solicitud aceptar(Long id) {
        Solicitud solicitud = buscarPorId(id);
        if (!solicitud.getEstado().equals("Pendiente")) {
            throw new RuntimeException("Solo se pueden aceptar solicitudes pendientes");
        }
        solicitud.setEstado("Aceptada");
        return solicitudRepository.save(solicitud);
    }

    // Rechazara
    public Solicitud rechazar(Long id, String comentario) {
        Solicitud solicitud = buscarPorId(id);
        if (!solicitud.getEstado().equals("Pendiente")) {
            throw new RuntimeException("Solo se pueden rechazar solicitudes pendientes");
        }
        if (comentario == null || comentario.isBlank()) {
            throw new RuntimeException("La justificación es obligatoria");
        }
        solicitud.setEstado("Rechazada");
        solicitud.setComentario(comentario);
        return solicitudRepository.save(solicitud);
    }

    // Entregar
    public Solicitud entregar(Long id) {
        Solicitud solicitud = buscarPorId(id);
        if (!solicitud.getEstado().equals("Aceptada")) {
            throw new RuntimeException("Solo se pueden entregar solicitudes aceptadas");
        }
        solicitud.setEstado("Entregada");
        return solicitudRepository.save(solicitud);
    }

    // Devolver
    public Solicitud devolver(Long id) {
        Solicitud solicitud = buscarPorId(id);
        if (!solicitud.getEstado().equals("Entregada")) {
            throw new RuntimeException("Solo se pueden devolver solicitudes entregadas");
        }
        solicitud.setEstado("Devuelta");
        return solicitudRepository.save(solicitud);
    }
}

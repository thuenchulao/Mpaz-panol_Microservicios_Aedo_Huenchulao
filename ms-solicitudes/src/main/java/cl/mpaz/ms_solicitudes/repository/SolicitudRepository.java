package cl.mpaz.ms_solicitudes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.mpaz.ms_solicitudes.model.Solicitud;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    //el solicitante ve solo sus solicitudes
    List<Solicitud> findBySolicitanteEmail(String solicitanteEmail);
}

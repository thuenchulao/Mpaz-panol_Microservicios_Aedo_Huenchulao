package com.mpaz.trazabilidadservice.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "eventos_solicitud")
public class EventoSolicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long solicitudId;
    private String estado;
    private LocalDateTime fechaHora;
    private String actor;
    private String comentario;

    public EventoSolicitud() {
    }
}
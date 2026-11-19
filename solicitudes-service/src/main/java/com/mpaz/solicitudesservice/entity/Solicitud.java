package com.mpaz.solicitudesservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;

@Data
@Entity
@Table(name = "solicitudes")
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String solicitanteNombre;
    private String solicitanteEmail;
    private String equipos;
    private String sala;
    private String fecha;
    private String horaInicio;
    private String horaDevolucion;
    private String estado;
    private String fechaCreacion;
    private String comentario;

    public Solicitud() {
    }

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "solicitud_items", joinColumns = @JoinColumn(name = "solicitud_id"))
    private List<SolicitudItem> items = new ArrayList<>();
}

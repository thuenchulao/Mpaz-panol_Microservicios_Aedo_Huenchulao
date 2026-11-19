package com.mpaz.solicitudesservice.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class SolicitudItem {

    private Long equipoId;
    private Integer cantidad;

    public SolicitudItem() {
    }
}
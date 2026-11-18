package com.mpaz.inventarioservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "equipos")
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String marca;
    private String modelo;
    private String categoria;
    private Integer stockTotal;
    private Integer stockDisponible;
    private String estado;
    private String ubicacion;

    public Equipo() {
    }
}
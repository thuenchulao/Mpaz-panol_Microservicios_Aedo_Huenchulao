package com.mpaz.trazabilidadservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mpaz.trazabilidadservice.entity.EventoSolicitud;
import com.mpaz.trazabilidadservice.service.EventoService;

@RestController
@RequestMapping("api/eventos")
public class EventoController {

    private final EventoService service;

    public EventoController(EventoService service) {
        this.service = service;
    }

    // registrar
    @PostMapping
    public ResponseEntity<EventoSolicitud> registrar(@RequestBody EventoSolicitud evento) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(evento));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // consultar
    @GetMapping
    public List<EventoSolicitud> listar(@RequestParam(required = false) String estado) {
        return service.listar(estado);
    }

    @GetMapping("/solicitud/{solicitudId}")
    public List<EventoSolicitud> historial(@PathVariable Long solicitudId) {
        return service.historial(solicitudId);
    }
}
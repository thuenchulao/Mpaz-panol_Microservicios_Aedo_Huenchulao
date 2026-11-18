package com.mpaz.solicitudesservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mpaz.solicitudesservice.entity.Solicitud;
import com.mpaz.solicitudesservice.service.SolicitudService;

@RestController
@RequestMapping("api/solicitudes")
public class SolicitudController {

    private final SolicitudService service;

    public SolicitudController(SolicitudService service) {
        this.service = service;
    }

    // listar y filtrar
    @GetMapping
    public List<Solicitud> listar(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String estado) {
        return service.listar(email, estado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Solicitud> obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // crear
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Solicitud crear(@RequestBody Solicitud solicitud) {
        return service.crear(solicitud);
    }

    // cambios de estado
    @PatchMapping("/{id}/aceptar")
    public ResponseEntity<Solicitud> aceptar(@PathVariable Long id) {
        try {
            return service.aceptar(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<Solicitud> rechazar(@PathVariable Long id, @RequestParam String comentario) {
        try {
            return service.rechazar(id, comentario)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/entregar")
    public ResponseEntity<Solicitud> entregar(@PathVariable Long id) {
        try {
            return service.entregar(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/devolver")
    public ResponseEntity<Solicitud> devolver(@PathVariable Long id) {
        try {
            return service.devolver(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

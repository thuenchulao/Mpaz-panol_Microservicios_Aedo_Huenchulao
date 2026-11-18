package com.mpaz.inventarioservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mpaz.inventarioservice.entity.Equipo;
import com.mpaz.inventarioservice.service.EquipoService;

@RestController
@RequestMapping("api/equipos")
public class EquipoController {

    private final EquipoService service;

    public EquipoController(EquipoService service) {
        this.service = service;
    }

    // listar y filtrar
    @GetMapping
    public List<Equipo> listar(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String estado) {
        return service.listar(categoria, estado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Equipo> obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // crear y actualizar
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Equipo crear(@RequestBody Equipo equipo) {
        return service.crear(equipo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Equipo> actualizar(@PathVariable Long id, @RequestBody Equipo equipo) {
        return service.actualizar(id, equipo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // stock
    @PatchMapping("/{id}/descontar")
    public ResponseEntity<Equipo> descontar(@PathVariable Long id, @RequestParam int cantidad) {
        try {
            return service.descontarStock(id, cantidad)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/reponer")
    public ResponseEntity<Equipo> reponer(@PathVariable Long id, @RequestParam int cantidad) {
        return service.reponerStock(id, cantidad)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
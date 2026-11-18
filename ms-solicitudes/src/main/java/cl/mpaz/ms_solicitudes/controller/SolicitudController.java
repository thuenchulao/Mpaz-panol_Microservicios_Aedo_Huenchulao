package cl.mpaz.ms_solicitudes.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import cl.mpaz.ms_solicitudes.model.Solicitud;
import cl.mpaz.ms_solicitudes.service.SolicitudService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    @Autowired
    private SolicitudService solicitudService;

    @GetMapping
    public List<Solicitud> listar() {
        return solicitudService.obtenerTodas();
    }

    @GetMapping("/email/{email}")
    public List<Solicitud> listarPorEmail(@PathVariable String email) {
        return solicitudService.obtenerPorEmail(email);
    }

    @GetMapping("/{id}")
    public Solicitud buscarPorId(@PathVariable Long id) {
        return solicitudService.buscarPorId(id);
    }

    @PostMapping
    public Solicitud crear(@RequestBody Solicitud solicitud) {
        return solicitudService.guardar(solicitud);
    }

    @PutMapping("/{id}/aceptar")
    public Solicitud aceptar(@PathVariable Long id) {
        return solicitudService.aceptar(id);
    }

    @PutMapping("/{id}/rechazar")
    public Solicitud rechazar(@PathVariable Long id, @RequestParam String comentario) {
        return solicitudService.rechazar(id, comentario);
    }

    @PutMapping("/{id}/entregar")
    public Solicitud entregar(@PathVariable Long id) {
        return solicitudService.entregar(id);
    }

    @PutMapping("/{id}/devolver")
    public Solicitud devolver(@PathVariable Long id) {
        return solicitudService.devolver(id);
    }
}

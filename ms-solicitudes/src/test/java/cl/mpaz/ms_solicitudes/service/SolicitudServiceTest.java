package cl.mpaz.ms_solicitudes.service;

import cl.mpaz.ms_solicitudes.model.Solicitud;
import cl.mpaz.ms_solicitudes.repository.SolicitudRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitudServiceTest {

    // Repo falso
    @Mock 
    private SolicitudRepository solicitudRepository;

    // Service real, mock metido dentro
    @InjectMocks
    private SolicitudService solicitudService;

    private Solicitud crearSolicitud(Long id, String estado) {
        Solicitud solicitud = new Solicitud();
        solicitud.setId(id);
        solicitud.setSolicitanteNombre("Docente Prueba");
        solicitud.setSolicitanteEmail("docente@mpaz.cl");
        solicitud.setEquipos("Proyector Epson x1");
        solicitud.setSala("Sala 5");
        solicitud.setEstado(estado);
        return solicitud;
    }

    @Test
    void guardarDejaEstadoPendienteTest() {
        Solicitud solicitud = crearSolicitud(null, null);
        when(solicitudRepository.save(any(Solicitud.class))).thenReturn(solicitud);

        Solicitud resultado = solicitudService.guardar(solicitud);

        assertEquals("Pendiente", resultado.getEstado());
        verify(solicitudRepository).save(solicitud);
    }

    @Test
    void obtenerTodasTest() {
        when(solicitudRepository.findAll()).thenReturn(Arrays.asList(
                crearSolicitud(1L, "Pendiente"),
                crearSolicitud(2L, "Aceptada")));

        List<Solicitud> resultado = solicitudService.obtenerTodas();

        assertEquals(2, resultado.size());
    }

    @Test
    void buscarPorIdNoExisteTest() {
        when(solicitudRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> solicitudService.buscarPorId(99L));
    }

    @Test
    void aceptarSolicitudPendienteTest() {
        Solicitud solicitud = crearSolicitud(1L, "Pendiente");
        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
        when(solicitudRepository.save(any(Solicitud.class))).thenReturn(solicitud);

        Solicitud resultado = solicitudService.aceptar(1L);

        assertEquals("Aceptada", resultado.getEstado());
    }

    @Test
    void noSePuedeAceptarSolicitudRechazadaTest() {
        Solicitud solicitud = crearSolicitud(1L, "Rechazada");
        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));

        assertThrows(RuntimeException.class, () -> solicitudService.aceptar(1L));
        verify(solicitudRepository, never()).save(any());
    }

    @Test
    void rechazarSinJustificacionTest() {
        Solicitud solicitud = crearSolicitud(1L, "Pendiente");
        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));

        assertThrows(RuntimeException.class, () -> solicitudService.rechazar(1L, ""));
    }

    @Test
    void rechazarConJustificacionTest() {
        Solicitud solicitud = crearSolicitud(1L, "Pendiente");
        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
        when(solicitudRepository.save(any(Solicitud.class))).thenReturn(solicitud);

        Solicitud resultado = solicitudService.rechazar(1L, "Proyector en mantención");

        assertEquals("Rechazada", resultado.getEstado());
        assertEquals("Proyector en mantención", resultado.getComentario());
    }

    @Test
    void entregarYDevolverTest() {
        Solicitud solicitud = crearSolicitud(1L, "Aceptada");
        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
        when(solicitudRepository.save(any(Solicitud.class))).thenReturn(solicitud);

        assertEquals("Entregada", solicitudService.entregar(1L).getEstado());
        assertEquals("Devuelta", solicitudService.devolver(1L).getEstado());
    }
}

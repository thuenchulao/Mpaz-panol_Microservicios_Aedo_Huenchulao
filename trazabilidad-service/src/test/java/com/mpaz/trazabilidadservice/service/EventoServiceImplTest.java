package com.mpaz.trazabilidadservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mpaz.trazabilidadservice.entity.EventoSolicitud;
import com.mpaz.trazabilidadservice.repository.EventoSolicitudRepository;

@ExtendWith(MockitoExtension.class)
class EventoServiceImplTest {

    @Mock
    private EventoSolicitudRepository repository;

    @InjectMocks
    private EventoServiceImpl service;

    private EventoSolicitud evento;

    // evento base válido
    @BeforeEach
    void preparar() {
        evento = new EventoSolicitud();
        evento.setSolicitudId(1L);
        evento.setEstado("Pendiente");
        evento.setActor("docente@mpaz.cl");
    }

    // registrar
    @Test
    void registrar_eventoValido_poneLaFechaYGuarda() {
        when(repository.save(any(EventoSolicitud.class))).thenAnswer(inv -> inv.getArgument(0));

        EventoSolicitud resultado = service.registrar(evento);

        assertNotNull(resultado.getFechaHora());
        verify(repository).save(evento);
    }

    @Test
    void registrar_sinSolicitudId_lanzaError() {
        evento.setSolicitudId(null);

        assertThrows(IllegalArgumentException.class, () -> service.registrar(evento));
        verify(repository, never()).save(any());
    }

    @Test
    void registrar_estadoInvalido_lanzaError() {
        evento.setEstado("Inventado");

        assertThrows(IllegalArgumentException.class, () -> service.registrar(evento));
        verify(repository, never()).save(any());
    }

    @Test
    void registrar_rechazoSinJustificacion_lanzaError() {
        evento.setEstado("Rechazada");
        evento.setComentario("   ");

        assertThrows(IllegalArgumentException.class, () -> service.registrar(evento));
        verify(repository, never()).save(any());
    }

    @Test
    void registrar_rechazoConJustificacion_guarda() {
        evento.setEstado("Rechazada");
        evento.setComentario("Equipo en mantenimiento");
        when(repository.save(any(EventoSolicitud.class))).thenAnswer(inv -> inv.getArgument(0));

        EventoSolicitud resultado = service.registrar(evento);

        assertEquals("Rechazada", resultado.getEstado());
    }

    // consultar
    @Test
    void historial_devuelveLosEventosDeLaSolicitud() {
        when(repository.findBySolicitudIdOrderByFechaHoraAsc(1L)).thenReturn(List.of(evento));

        assertEquals(1, service.historial(1L).size());
    }

    @Test
    void listar_conEstado_filtraPorEstado() {
        when(repository.findByEstado("Pendiente")).thenReturn(List.of(evento));

        assertEquals(1, service.listar("Pendiente").size());
        verify(repository, never()).findAll();
    }
}
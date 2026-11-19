package com.mpaz.solicitudesservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mpaz.solicitudesservice.entity.Solicitud;
import com.mpaz.solicitudesservice.repository.SolicitudRepository;

import static org.mockito.Mockito.atLeastOnce;
import org.mockito.Answers;
import org.springframework.web.reactive.function.client.WebClient;
import com.mpaz.solicitudesservice.entity.SolicitudItem;

@ExtendWith(MockitoExtension.class)
class SolicitudServiceImplTest {

    @Mock
    private SolicitudRepository repository;

    // simula WebClient (inventario y trazabilidad)
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WebClient.Builder webClientBuilder;

    @InjectMocks
    private SolicitudServiceImpl service;

    private Solicitud solicitud;

    // solicitud base en estado Pendiente
    @BeforeEach
    void preparar() {
        solicitud = new Solicitud();
        solicitud.setId(1L);
        solicitud.setSolicitanteEmail("docente@mpaz.cl");
        solicitud.setEquipos("Proyector Epson x1");
        solicitud.setSala("Sala 5");
        solicitud.setEstado("Pendiente");
    }

    // crear
    @Test
    void crear_quedaPendiente() {
        when(repository.save(any(Solicitud.class))).thenAnswer(inv -> inv.getArgument(0));
        solicitud.setEstado(null);

        Solicitud resultado = service.crear(solicitud);

        assertEquals("Pendiente", resultado.getEstado());
    }

    // listar
    @Test
    void listar_conEmail_filtraPorEmail() {
        when(repository.findBySolicitanteEmail("docente@mpaz.cl")).thenReturn(List.of(solicitud));

        List<Solicitud> resultado = service.listar("docente@mpaz.cl", null);

        assertEquals(1, resultado.size());
        verify(repository, never()).findAll();
    }

    @Test
    void listar_sinFiltros_devuelveTodas() {
        when(repository.findAll()).thenReturn(List.of(solicitud));

        assertEquals(1, service.listar(null, null).size());
    }

    // cambios de estado
    @Test
    void aceptar_pendiente_quedaAceptada() {
        when(repository.findById(1L)).thenReturn(Optional.of(solicitud));
        when(repository.save(any(Solicitud.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<Solicitud> resultado = service.aceptar(1L);

        assertEquals("Aceptada", resultado.get().getEstado());
    }

    @Test
    void aceptar_rechazada_lanzaError() {
        solicitud.setEstado("Rechazada");
        when(repository.findById(1L)).thenReturn(Optional.of(solicitud));

        assertThrows(IllegalArgumentException.class, () -> service.aceptar(1L));
        verify(repository, never()).save(any());
    }

    @Test
    void aceptar_inexistente_devuelveVacio() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(service.aceptar(99L).isEmpty());
    }

    @Test
    void rechazar_sinJustificacion_lanzaError() {
        assertThrows(IllegalArgumentException.class, () -> service.rechazar(1L, " "));
        verify(repository, never()).save(any());
    }

    @Test
    void rechazar_conJustificacion_guardaComentario() {
        when(repository.findById(1L)).thenReturn(Optional.of(solicitud));
        when(repository.save(any(Solicitud.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<Solicitud> resultado = service.rechazar(1L, "Proyector en mantención");

        assertEquals("Rechazada", resultado.get().getEstado());
        assertEquals("Proyector en mantención", resultado.get().getComentario());
    }

    @Test
    void entregar_y_devolver() {
        solicitud.setEstado("Aceptada");
        when(repository.findById(1L)).thenReturn(Optional.of(solicitud));
        when(repository.save(any(Solicitud.class))).thenAnswer(inv -> inv.getArgument(0));

        assertEquals("Entregada", service.entregar(1L).get().getEstado());
        assertEquals("Devuelta", service.devolver(1L).get().getEstado());
    }

    // comunicación con inventario
    @Test
    void aceptar_conItems_descuentaStockYQuedaAceptada() {
        SolicitudItem item = new SolicitudItem();
        item.setEquipoId(1L);
        item.setCantidad(2);
        solicitud.setItems(List.of(item));
        when(repository.findById(1L)).thenReturn(Optional.of(solicitud));
        when(repository.save(any(Solicitud.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<Solicitud> resultado = service.aceptar(1L);

        assertEquals("Aceptada", resultado.get().getEstado());
        verify(webClientBuilder, atLeastOnce()).build();
    }

    @Test
    void aceptar_inventarioFalla_lanzaErrorYNoGuarda() {
        SolicitudItem item = new SolicitudItem();
        item.setEquipoId(1L);
        item.setCantidad(2);
        solicitud.setItems(List.of(item));
        when(repository.findById(1L)).thenReturn(Optional.of(solicitud));
        when(webClientBuilder.build()).thenThrow(new RuntimeException("Inventario no disponible"));

        assertThrows(IllegalArgumentException.class, () -> service.aceptar(1L));
        verify(repository, never()).save(any());
    }
}
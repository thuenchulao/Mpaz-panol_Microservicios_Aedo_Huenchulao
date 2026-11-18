package com.mpaz.inventarioservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

import com.mpaz.inventarioservice.entity.Equipo;
import com.mpaz.inventarioservice.repository.EquipoRepository;

@ExtendWith(MockitoExtension.class)
class EquipoServiceImplTest {

    @Mock
    private EquipoRepository repository;

    @InjectMocks
    private EquipoServiceImpl service;

    private Equipo equipo;

    // equipo base: 4 en total, 3 disponibles
    @BeforeEach
    void preparar() {
        equipo = new Equipo();
        equipo.setId(1L);
        equipo.setNombre("Proyector Epson");
        equipo.setStockTotal(4);
        equipo.setStockDisponible(3);
    }

    // stock
    @Test
    void descontarStock_restaLaCantidad() {
        when(repository.findById(1L)).thenReturn(Optional.of(equipo));
        when(repository.save(any(Equipo.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<Equipo> resultado = service.descontarStock(1L, 2);

        assertTrue(resultado.isPresent());
        assertEquals(1, resultado.get().getStockDisponible());
    }

    @Test
    void descontarStock_masDeLoDisponible_lanzaError() {
        when(repository.findById(1L)).thenReturn(Optional.of(equipo));

        assertThrows(IllegalArgumentException.class, () -> service.descontarStock(1L, 5));
        verify(repository, never()).save(any());
    }

    @Test
    void descontarStock_equipoInexistente_devuelveVacio() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(service.descontarStock(99L, 1).isEmpty());
    }

    @Test
    void reponerStock_noSuperaElTotal() {
        when(repository.findById(1L)).thenReturn(Optional.of(equipo));
        when(repository.save(any(Equipo.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<Equipo> resultado = service.reponerStock(1L, 10);

        assertEquals(4, resultado.get().getStockDisponible());
    }

    // listar
    @Test
    void listar_conCategoria_filtraPorCategoria() {
        when(repository.findByCategoria("Proyector")).thenReturn(List.of(equipo));

        List<Equipo> resultado = service.listar("Proyector", null);

        assertEquals(1, resultado.size());
        verify(repository, never()).findAll();
    }

    @Test
    void listar_sinFiltros_devuelveTodos() {
        when(repository.findAll()).thenReturn(List.of(equipo));

        assertEquals(1, service.listar(null, null).size());
    }
}
package com.mpaz.inventarioservice.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mpaz.inventarioservice.entity.Equipo;
import com.mpaz.inventarioservice.repository.EquipoRepository;

@Configuration
public class DatosIniciales {

    @Bean
    CommandLineRunner cargarEquipos(EquipoRepository repository) {
        return args -> {
            // solo si la tabla está vacía
            if (repository.count() > 0) {
                return;
            }
            repository.saveAll(List.of(
                    equipo("Proyector Epson", "Epson", "X49", "Proyector", 4, 4, "Disponible", "Bodega pañol"),
                    equipo("Proyector BenQ", "BenQ", "MS550", "Proyector", 2, 1, "Reservado", "Bodega pañol"),
                    equipo("Notebook Lenovo", "Lenovo", "IdeaPad 3", "Notebook", 10, 8, "Disponible", "Carro de notebooks"),
                    equipo("Notebook HP", "HP", "250 G8", "Notebook", 6, 0, "Prestado", "Carro de notebooks"),
                    equipo("Parlante portátil", "JBL", "Charge 5", "Parlante", 3, 3, "Disponible", "Bodega pañol"),
                    equipo("Alargador 5m", "Philips", "SPN3250", "Alargador", 8, 8, "Disponible", "Bodega pañol"),
                    equipo("Kit LEGO WeDo 2.0", "LEGO", "WeDo 2.0", "Robótica", 5, 4, "Disponible", "Sala de robótica"),
                    equipo("Arduino Uno", "Arduino", "UNO R3", "Robótica", 12, 10, "Disponible", "Sala de robótica"),
                    equipo("Router TP-Link", "TP-Link", "Archer C6", "Equipamiento de red", 1, 1, "Disponible", "Sala de servidores"),
                    equipo("Switch Cisco", "Cisco", "SG110", "Equipamiento de red", 1, 0, "En mantenimiento", "Sala de servidores")
            ));
        };
    }

    // arma un equipo
    private Equipo equipo(String nombre, String marca, String modelo, String categoria,
                          int total, int disponible, String estado, String ubicacion) {
        Equipo e = new Equipo();
        e.setNombre(nombre);
        e.setMarca(marca);
        e.setModelo(modelo);
        e.setCategoria(categoria);
        e.setStockTotal(total);
        e.setStockDisponible(disponible);
        e.setEstado(estado);
        e.setUbicacion(ubicacion);
        return e;
    }
}
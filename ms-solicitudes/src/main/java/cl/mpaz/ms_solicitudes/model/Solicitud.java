package cl.mpaz.ms_solicitudes.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "solicitudes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String solicitanteNombre;
    private String solicitanteEmail;
    private String equipos;          
    private String sala;
    private String fecha;            // "20/11/2026"
    private String horaInicio;       // "10:00"
    private String horaDevolucion;   // "12:00"
    private String estado;           // Pendiente, Aceptada, Rechazada, Entregada, Devuelta
    private String fechaCreacion;
    private String comentario;       // justificación si se rechaza
}

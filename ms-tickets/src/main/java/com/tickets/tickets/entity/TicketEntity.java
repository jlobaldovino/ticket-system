package com.tickets.tickets.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketEntity {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(nullable = false, length = 500)
    private String descripcion;
    @Column(nullable = false)
    private UUID usuarioId;
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
    public enum Status {ABIERTO,CERRADO}
}
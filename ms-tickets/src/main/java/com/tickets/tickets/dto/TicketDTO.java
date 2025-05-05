package com.tickets.tickets.dto;


import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketDTO {
    private UUID id;
    private String descripcion;
    private UUID usuarioId;
    private String fechaCreacion;
    private String fechaActualizacion;
    private Status status;
    public enum Status {ABIERTO,CERRADO}
}
package com.tickets.tickets.mapper;

import com.tickets.tickets.dto.ActualizarTicketDTO;
import com.tickets.tickets.dto.CrearTicketDTO;
import com.tickets.tickets.dto.TicketDTO;
import com.tickets.tickets.entity.TicketEntity;

import java.time.LocalDateTime;

public class TicketMapper {

    private TicketMapper() {
    }

    public static TicketEntity toEntity(CrearTicketDTO dto) {
        return TicketEntity.builder()
                .descripcion(dto.getDescripcion())
                .usuarioId(dto.getUsuarioId())
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .status(TicketEntity.Status.ABIERTO)
                .build();
    }

    public static TicketDTO toDTO(TicketEntity entity) {
        return TicketDTO.builder()
                .id(entity.getId())
                .descripcion(entity.getDescripcion())
                .usuarioId(entity.getUsuarioId())
                .fechaCreacion(String.valueOf(entity.getFechaCreacion()))
                .fechaActualizacion(String.valueOf(entity.getFechaActualizacion()))
                .status(TicketDTO.Status.valueOf(entity.getStatus().name()))
                .build();
    }

    public static void actualizarEntidad(TicketEntity entity, ActualizarTicketDTO dto) {
        entity.setDescripcion(dto.getDescripcion());
        entity.setStatus(TicketEntity.Status.valueOf(dto.getStatus().name()));
        entity.setFechaActualizacion(LocalDateTime.now());
    }
}
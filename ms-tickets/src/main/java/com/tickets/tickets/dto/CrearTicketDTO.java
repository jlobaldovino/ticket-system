package com.tickets.tickets.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearTicketDTO {

    @NotBlank(message = "La descripción no puede estar vacío")
    private String descripcion;
    private UUID usuarioId;

}

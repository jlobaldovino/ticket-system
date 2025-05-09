package com.tickets.users.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AuditEventDTO {

    private String timestamp;
    private String servicio;
    private String accion;
    private String usuario;
    private String data;
}

package com.tickets.tickets.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuditEventDTO {

    private String timestamp;
    private String servicio;
    private String accion;
    private String usuario;
    private String data;
}

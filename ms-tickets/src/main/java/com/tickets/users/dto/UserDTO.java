package com.tickets.users.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;


@Data
@Builder
public class UserDTO {
    private UUID id;
    private String nombres;
    private String apellidos;
    private String email;
    private String rol;
}

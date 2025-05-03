package com.tickets.users.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;


@Data
@Builder
public class AuthUserDTO {
    private UUID id;
    private String email;
    private String passwordHash;
    private String rol;
}

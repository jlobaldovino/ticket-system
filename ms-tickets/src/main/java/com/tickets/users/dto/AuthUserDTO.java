package com.tickets.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserDTO {
    private UUID id;
    private String email;
    private String passwordHash;
    private String rol;
}

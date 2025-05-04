package com.tickets.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ActualizarUsuarioDTO {

    @NotBlank(message = "Nombres no puede estar vacío")
    private String nombres;

    @NotBlank(message = "Apellidos no puede estar vacío")
    private String apellidos;

    @NotBlank(message = "Email no puede estar vacío")
    @Email(message = "Formato de email inválido")
    private String email;
}

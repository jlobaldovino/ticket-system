package com.tickets.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CrearUsuarioDTO {

    @NotBlank(message = "Nombres no puede estar vacío")
    private String nombres;

    @NotBlank(message = "Apellidos no puede estar vacío")
    private String apellidos;

    @NotBlank(message = "Email no puede estar vacío")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
}

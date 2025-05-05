package com.tickets.users.dto;

import com.tickets.users.entity.UserEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    public UserEntity toEntity() {
        return UserEntity.builder()
                .nombres(this.nombres)
                .apellidos(this.apellidos)
                .email(this.email)
                .passwordHash(this.password)
                .build();
    }
}

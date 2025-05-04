package com.tickets.users.mapper;

import com.tickets.users.dto.ActualizarUsuarioDTO;
import com.tickets.users.dto.CrearUsuarioDTO;
import com.tickets.users.dto.UserDTO;
import com.tickets.users.entity.UserEntity;

import java.time.LocalDateTime;

public class UserMapper {

    private UserMapper() {
    }

    public static UserEntity toEntity(CrearUsuarioDTO dto, String passwordHash) {
        return UserEntity.builder()
                .nombres(dto.getNombres())
                .apellidos(dto.getApellidos())
                .email(dto.getEmail())
                .passwordHash(passwordHash)
                .rol("USER")
                    .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();
    }

    public static UserEntity toEntityAdmin(CrearUsuarioDTO dto, String passwordHash) {
        return UserEntity.builder()
                .nombres(dto.getNombres())
                .apellidos(dto.getApellidos())
                .email(dto.getEmail())
                .passwordHash(passwordHash)
                .rol("ADMIN")
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();
    }

    public static UserDTO toDTO(UserEntity entity) {
        return UserDTO.builder()
                .id(entity.getId())
                .nombres(entity.getNombres())
                .apellidos(entity.getApellidos())
                .email(entity.getEmail())
                .rol(entity.getRol())
                .build();
    }

    public static void actualizarEntidad(UserEntity entity, ActualizarUsuarioDTO dto) {
        entity.setNombres(dto.getNombres());
        entity.setApellidos(dto.getApellidos());
        entity.setEmail(dto.getEmail());
        entity.setFechaActualizacion(LocalDateTime.now());
    }
}

package com.tickets.users.service;

import com.tickets.users.dto.UserDTO;
import com.tickets.users.entity.UserEntity;
import com.tickets.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserEntity crearUsuario(UserDTO dto) {
        UserEntity user = UserEntity.builder()
                .nombres(dto.getNombres())
                .apellidos(dto.getApellidos())
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();
        return userRepository.save(user);
    }
}

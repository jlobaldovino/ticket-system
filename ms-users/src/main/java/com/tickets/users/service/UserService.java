package com.tickets.users.service;

import com.tickets.users.dto.ActualizarUsuarioDTO;
import com.tickets.users.dto.AuthUserDTO;
import com.tickets.users.dto.CrearUsuarioDTO;
import com.tickets.users.dto.UserDTO;
import com.tickets.users.entity.UserEntity;
import com.tickets.users.exception.EmailYaRegistradoException;
import com.tickets.users.exception.UsuarioNoEncontradoException;
import com.tickets.users.mapper.UserMapper;
import com.tickets.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDTO crearUsuario(CrearUsuarioDTO dto) {
        validarEmailDisponible(dto.getEmail());
        UserEntity entity = UserMapper.toEntity(dto, passwordEncoder.encode(dto.getPassword()));
        userRepository.save(entity);
        log.info("Usuario creado exitosamente: {}", entity.getEmail());
        return UserMapper.toDTO(entity);
    }

    @Transactional
    public UserDTO actualizarUsuario(UUID id, ActualizarUsuarioDTO dto) {
        UserEntity usuario = userRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id));
        validarEmailDisponible(dto.getEmail());
        UserMapper.actualizarEntidad(usuario, dto);
        return UserMapper.toDTO(usuario);
    }

    public AuthUserDTO buscarPorEmailOExcepcion(String email) {
        return userRepository.findByEmail(email)
                .map(user -> AuthUserDTO.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .passwordHash(user.getPasswordHash())
                        .rol(user.getRol())
                        .build())
                .orElseThrow(() -> new UsuarioNoEncontradoException(email));
    }

    private void validarEmailDisponible(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailYaRegistradoException(email);
        }
    }
}

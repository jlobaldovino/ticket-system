package com.tickets.users.service.impl;

import com.tickets.users.dto.ActualizarUsuarioDTO;
import com.tickets.users.dto.AuthUserDTO;
import com.tickets.users.dto.CrearUsuarioDTO;
import com.tickets.users.dto.UserDTO;
import com.tickets.users.entity.UserEntity;
import com.tickets.users.exception.EmailYaRegistradoException;
import com.tickets.users.exception.UsuarioNoEncontradoException;
import com.tickets.users.mapper.UserMapper;
import com.tickets.users.repository.UserRepository;
import com.tickets.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final CacheManager cacheManager;

    @Transactional
    public UserDTO crearUsuario(CrearUsuarioDTO dto) {
        validarEmailDisponible(dto.getEmail());
        UserEntity entity = UserMapper.toEntity(dto, passwordEncoder.encode(dto.getPassword()));
        userRepository.save(entity);
        return UserMapper.toDTO(entity);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "usuario", key = "#id"),
            @CacheEvict(value = "usuario_email", allEntries = true)
    })
    public UserDTO actualizarUsuario(UUID id, ActualizarUsuarioDTO dto) {

        UserEntity usuario = userRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id));

        if (!usuario.getEmail().equals(dto.getEmail()))
            validarEmailDisponible(dto.getEmail());

        UserMapper.actualizarEntidad(usuario, dto);
        return UserMapper.toDTO(usuario);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "usuario_email", key = "#email")
    public AuthUserDTO buscarPorEmail(String email) {
        mostrarValorEnCache("usuario_email", email);
        return userRepository.findByEmail(email)
                .map(user -> AuthUserDTO.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .passwordHash(user.getPasswordHash())
                        .rol(user.getRol())
                        .build())
                .orElseThrow(() -> new UsuarioNoEncontradoException(email));
    }

    public void mostrarValorEnCache(String name, String key) {
        Cache ticketCache = cacheManager.getCache(name);
        if (ticketCache != null) {
            Object cachedValue = ticketCache.get(key, Object.class);
            if (cachedValue != null) {
                System.out.println("Valor almacenado en caché para key {"+key+"}: {"+cachedValue+"}");
            } else {
                System.out.println("No se encontró valor en caché para key {"+key+"}");
            }
        } else {
            System.out.println("Error: ticketCache != null");
        }
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> obtenerTodos(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserMapper::toDTO);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "usuario", key = "#id")
    public UserDTO obtenerPorId(UUID id) {
        UserEntity usuario = userRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id));
        return UserMapper.toDTO(usuario);
    }

    private void validarEmailDisponible(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailYaRegistradoException(email);
        }
    }
}

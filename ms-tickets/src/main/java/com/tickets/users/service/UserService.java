package com.tickets.users.service;

import com.tickets.users.dto.ActualizarUsuarioDTO;
import com.tickets.users.dto.AuthUserDTO;
import com.tickets.users.dto.CrearUsuarioDTO;
import com.tickets.users.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {

    UserDTO crearUsuario(CrearUsuarioDTO dto);

    UserDTO actualizarUsuario(UUID id, ActualizarUsuarioDTO dto);

    AuthUserDTO buscarPorEmail(String email);

    Page<UserDTO> obtenerTodos(Pageable pageable);

    UserDTO obtenerPorId(UUID id);


}

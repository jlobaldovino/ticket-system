package com.tickets.users.service;

import com.tickets.users.dto.ActualizarUsuarioDTO;
import com.tickets.users.dto.AuthUserDTO;
import com.tickets.users.dto.CrearUsuarioDTO;
import com.tickets.users.dto.UserDTO;

import java.util.UUID;

public interface IUserService {

    UserDTO crearUsuario(CrearUsuarioDTO dto);

    UserDTO actualizarUsuario(UUID id, ActualizarUsuarioDTO dto);

    AuthUserDTO buscarPorEmailOExcepcion(String email);
}

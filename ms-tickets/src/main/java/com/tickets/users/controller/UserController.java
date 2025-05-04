package com.tickets.users.controller;

import com.tickets.users.dto.ActualizarUsuarioDTO;
import com.tickets.users.dto.AuthUserDTO;
import com.tickets.users.dto.CrearUsuarioDTO;
import com.tickets.users.dto.UserDTO;
import com.tickets.users.service.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Operaciones relacionadas con usuarios")
public class UserController {

    private final UserService userService;

    @Hidden
    @GetMapping("/email/{email}")
    public ResponseEntity<AuthUserDTO> obtenerPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.buscarPorEmail(email));
    }

    @GetMapping
    @Operation(summary = "Obtener listado paginado de usuarios")
    public ResponseEntity<Page<UserDTO>> listarUsuarios(
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(userService.obtenerTodos(pageable));
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo usuario")
    public ResponseEntity<UserDTO> crearUsuario(@Valid @RequestBody CrearUsuarioDTO dto) {
        UserDTO creado = userService.crearUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> actualizarUsuario(
            @PathVariable UUID id,
            @Valid @RequestBody ActualizarUsuarioDTO dto) {
        UserDTO actualizado = userService.actualizarUsuario(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID")
    public ResponseEntity<UserDTO> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.obtenerPorId(id));
    }

}

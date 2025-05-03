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

    @Hidden
    @GetMapping("/email/{email}")
    public ResponseEntity<AuthUserDTO> obtenerPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.buscarPorEmailOExcepcion(email));
    }

}

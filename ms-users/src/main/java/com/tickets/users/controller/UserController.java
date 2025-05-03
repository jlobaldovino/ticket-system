package com.tickets.users.controller;

import com.tickets.users.dto.ActualizarUsuarioDTO;
import com.tickets.users.dto.AuthUserDTO;
import com.tickets.users.dto.CrearUsuarioDTO;
import com.tickets.users.dto.UserDTO;
import com.tickets.users.service.IUserService;
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

    private final IUserService IUserService;

    @PostMapping
    @Operation(summary = "Crear un nuevo usuario")
    public ResponseEntity<UserDTO> crearUsuario(@Valid @RequestBody CrearUsuarioDTO dto) {
        UserDTO creado = IUserService.crearUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> actualizarUsuario(
            @PathVariable UUID id,
            @Valid @RequestBody ActualizarUsuarioDTO dto) {
        UserDTO actualizado = IUserService.actualizarUsuario(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    @Hidden
    @GetMapping("/email/{email}")
    public ResponseEntity<AuthUserDTO> obtenerPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(IUserService.buscarPorEmailOExcepcion(email));
    }

}

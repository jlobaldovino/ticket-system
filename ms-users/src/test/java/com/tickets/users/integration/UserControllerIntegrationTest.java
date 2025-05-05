package com.tickets.users.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tickets.users.dto.ActualizarUsuarioDTO;
import com.tickets.users.dto.CrearUsuarioDTO;
import com.tickets.users.entity.UserEntity;
import com.tickets.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void crearUsuario_exitoso() throws Exception {
        CrearUsuarioDTO dto = new CrearUsuarioDTO();
        dto.setNombres("Juan");
        dto.setApellidos("Pérez");
        dto.setEmail("juan.perez@example.com");
        dto.setPassword("123456");

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value(dto.getEmail()));
    }

    @Test
    void crearUsuario_emailDuplicado() throws Exception {

        CrearUsuarioDTO dto = new CrearUsuarioDTO();
        dto.setNombres("Juan");
        dto.setApellidos("Pérez");
        dto.setEmail("juan.perez@example.com");
        dto.setPassword("123456");
        userRepository.save(dto.toEntity());

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"error\":\"El correo electrónico 'juan.perez@example.com' ya está registrado.\"}"));
    }

    @Test
    void crearUsuario_camposInvalidos() throws Exception {
        CrearUsuarioDTO dto = new CrearUsuarioDTO();
        dto.setNombres(""); // inválido
        dto.setApellidos("");
        dto.setEmail("correo-no-valido");
        dto.setPassword("123"); // muy corto

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()").value(4));

    }

    @Test
    void actualizarUsuario_exitoso() throws Exception {
        // Arrange: crear usuario existente
        UserEntity user = userRepository.save(UserEntity.builder()
                .nombres("Pedro")
                .apellidos("López")
                .email("pedro@example.com")
                .passwordHash("hash")
                .rol("USER")
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build());

        ActualizarUsuarioDTO dto = new ActualizarUsuarioDTO();
        dto.setNombres("Carlos");
        dto.setApellidos("Pérez");
        dto.setEmail("pedro@example.com");

        // Act + Assert
        mockMvc.perform(put("/api/usuarios/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":\"" + user.getId() +
                        "\",\"nombres\":\"Carlos\",\"apellidos\":\"Pérez\"" +
                        ",\"email\":\"pedro@example.com\",\"rol\":\"USER\"}"));
    }

    @Test
    void actualizarUsuario_idInexistente_deberiaRetornar404() throws Exception {
        ActualizarUsuarioDTO dto = new ActualizarUsuarioDTO();
        dto.setNombres("Carlos");
        dto.setApellidos("Pérez");
        dto.setEmail("nuevo@example.com");
        UUID idInexistente = UUID.randomUUID();
        mockMvc.perform(put("/api/usuarios/" + idInexistente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void actualizarUsuario_emailDuplicado_deberiaRetornar400() throws Exception {
        // Usuario 1
        userRepository.save(UserEntity.builder()
                .nombres("Ana")
                .apellidos("Ramírez")
                .email("ana@example.com")
                .passwordHash("hash")
                .rol("USER")
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build());

        // Usuario 2
        UserEntity user2 = userRepository.save(UserEntity.builder()
                .nombres("Luis")
                .apellidos("Torres")
                .email("luis@example.com")
                .passwordHash("hash")
                .rol("USER")
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build());

        // Intenta actualizar con email duplicado
        ActualizarUsuarioDTO dto = new ActualizarUsuarioDTO();
        dto.setNombres("Luis");
        dto.setApellidos("Torres");
        dto.setEmail("ana@example.com"); // Duplicado

        mockMvc.perform(put("/api/usuarios/" + user2.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("ya está registrado")));
    }



    @Test
    void obtenerUsuarioPorId_exitoso() throws Exception {
        // Arrange
        UserEntity user = userRepository.save(UserEntity.builder()
                .nombres("María")
                .apellidos("Gómez")
                .email("maria.gomez@example.com")
                .passwordHash("hash")
                .rol("USER")
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build());
        // Act + Assert
        mockMvc.perform(get("/api/usuarios/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId().toString()))
                .andExpect(jsonPath("$.email").value("maria.gomez@example.com"))
                .andExpect(jsonPath("$.nombres").value("María"))
                .andExpect(jsonPath("$.apellidos").value("Gómez"));
    }

    @Test
    void obtenerUsuarioPorId_inexistente_deberiaRetornar404() throws Exception {
        UUID idFalso = UUID.randomUUID();
        mockMvc.perform(get("/api/usuarios/" + idFalso))
                .andExpect(status().isNotFound());
    }

    @Test
    void listarUsuarios_sinResultados_deberiaRetornarListaVacia() throws Exception {
        // DB vacía

        mockMvc.perform(get("/api/usuarios?page=0&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void listarUsuarios_conOrdenamiento_deberiaRetornarOrdenCorrecto() throws Exception {
        // Arrange
        userRepository.save(UserEntity.builder().nombres("Zara").apellidos("Bravo").email("zara@example.com")
                .passwordHash("hash").rol("USER").fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now()).build());
        userRepository.save(UserEntity.builder().nombres("Andrés").apellidos("Zuluaga").email("andres@example.com")
                .passwordHash("hash").rol("USER").fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now()).build());

        // Act + Assert
        mockMvc.perform(get("/api/usuarios?page=0&size=2&sort=nombres,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nombres").value("Andrés"))
                .andExpect(jsonPath("$.content[1].nombres").value("Zara"));
    }




}

package com.tickets.users.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tickets.users.entity.UserEntity;
import com.tickets.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GatewayIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setup(@Autowired UserRepository userRepository) {
        userRepository.deleteAll();

        // Crear usuario directamente en ms-users
        UserEntity user = UserEntity.builder()
                .nombres("Pedro")
                .apellidos("Pérez")
                .email("pedro@example.com")
                .passwordHash(new BCryptPasswordEncoder().encode("123456"))
                .rol("USER")
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        userRepository.save(user);
    }

    @Test
    void loginYConsultarUsuariosPaginated_conTokenValido() throws Exception {
        // Paso 1: login
        Map<String, String> loginPayload = new HashMap<>();
        loginPayload.put("email", "juan.perez@gmail.com");
        loginPayload.put("password", "123456");

        ResponseEntity<Map> loginResponse = restTemplate.postForEntity(
                "http://localhost:3000/api/auth/login",
                loginPayload,
                Map.class
        );

        assertEquals(HttpStatus.CREATED, loginResponse.getStatusCode());
        assertTrue(loginResponse.getBody().containsKey("accessToken"));

        String token = (String) loginResponse.getBody().get("accessToken");

        // Paso 2: llamar endpoint paginado con token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:3000/api/usuarios?page=0&size=5",
                HttpMethod.GET,
                request,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("juan.perez@gmail.com"));
    }
}

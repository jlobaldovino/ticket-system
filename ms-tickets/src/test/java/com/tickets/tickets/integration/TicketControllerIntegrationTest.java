package com.tickets.tickets.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tickets.tickets.repository.TicketRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TicketControllerIntegrationTest {

    @Autowired
    TestRestTemplate restTemplate;

    @BeforeEach
    void limpiarBD(@Autowired TicketRepository userRepository) {
        userRepository.deleteAll();
    }

    @Test
    void loginYConsultarTiketsPaginated_conTokenValido() throws Exception {
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
                "http://localhost:3000/api/tickets?page=0&size=5",
                HttpMethod.GET,
                request,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("ABIERTO") || response.getBody().contains("CERRADO"));
    }

}

package com.tickets.users.service;

import com.tickets.users.dto.ActualizarUsuarioDTO;
import com.tickets.users.dto.CrearUsuarioDTO;
import com.tickets.users.dto.UserDTO;
import com.tickets.users.entity.UserEntity;
import com.tickets.users.exception.EmailYaRegistradoException;
import com.tickets.users.exception.UsuarioNoEncontradoException;
import com.tickets.users.mapper.UserMapper;
import com.tickets.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crearUsuario_deberiaCrearUsuarioExitosamente() {

        String nombre="Juan";
        String apellidos="Pérez";
        String email="juan.perez@ejemplo.com";
        String password="123456";
        String hash = "$2a$10$xxxxx";

        // Arrange
        CrearUsuarioDTO dto = new CrearUsuarioDTO();
        dto.setNombres(nombre);
        dto.setApellidos(apellidos);
        dto.setEmail(email);
        dto.setPassword(password);

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(dto.getPassword())).thenReturn(hash);

        // Simulamos guardar y devolver entidad con ID
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        doAnswer(invocation -> {
            UserEntity user = invocation.getArgument(0);
            user.setId(UUID.randomUUID());
            return user;
        }).when(userRepository).save(captor.capture());

        // Act
        UserDTO resultado = userService.crearUsuario(dto);

        // Assert
        assertEquals(dto.getEmail(), resultado.getEmail());
        assertEquals("USER", resultado.getRol());
        assertNotNull(resultado.getId());

        verify(userRepository).findByEmail(dto.getEmail());
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void crearUsuario_deberiaLanzarExcepcionSiEmailYaExiste() {

        String emailRepetido = "repetido@ejemplo.com";
        // Arrange
        CrearUsuarioDTO dto = new CrearUsuarioDTO();
        dto.setEmail(emailRepetido);

        when(userRepository.findByEmail(dto.getEmail()))
                .thenReturn(Optional.of(new UserEntity()));

        // Act & Assert
        EmailYaRegistradoException ex = assertThrows(EmailYaRegistradoException.class, () -> {
            userService.crearUsuario(dto);
        });

        assertEquals("El correo electrónico '"+emailRepetido+"' ya está registrado.", ex.getMessage());
        verify(userRepository).findByEmail(dto.getEmail());
        verify(userRepository, never()).save(any());
    }




    @Test
    void actualizarUsuario_deberiaActualizarUsuarioCorrectamente() {
        // Arrange
        UUID id = UUID.randomUUID();
        UserEntity existente = new UserEntity();
        existente.setId(id);
        existente.setEmail("actual@correo.com");

        ActualizarUsuarioDTO dto = new ActualizarUsuarioDTO();
        dto.setNombres("Nuevo");
        dto.setApellidos("Apellido");
        dto.setEmail("actual@correo.com"); // mismo email

        when(userRepository.findById(id)).thenReturn(Optional.of(existente));
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(existente)); // mismo usuario

        // Act
        UserDTO resultado = userService.actualizarUsuario(id, dto);

        // Assert
        assertEquals("Nuevo", resultado.getNombres());
        assertEquals("actual@correo.com", resultado.getEmail());
        verify(userRepository).findById(id);
        verify(userRepository).save(existente); // implícito si usas save, explícito si haces flush
    }

    @Test
    void actualizarUsuario_deberiaLanzarExcepcionSiUsuarioNoExiste() {
        // Arrange
        UUID id = UUID.randomUUID();
        ActualizarUsuarioDTO dto = new ActualizarUsuarioDTO();
        dto.setEmail("nuevo@correo.com");

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        UsuarioNoEncontradoException ex = assertThrows(UsuarioNoEncontradoException.class, () -> {
            userService.actualizarUsuario(id, dto);
        });

        assertTrue(ex.getMessage().contains(id.toString()));
        verify(userRepository).findById(id);
        verify(userRepository, never()).save(any());
    }

    @Test
    void actualizarUsuario_deberiaLanzarExcepcionSiEmailYaExiste() {
        // Arrange
        UUID id = UUID.randomUUID();

        UserEntity existente = new UserEntity();
        existente.setId(id);
        existente.setEmail("actual@correo.com");

        ActualizarUsuarioDTO dto = new ActualizarUsuarioDTO();
        dto.setNombres("Juan");
        dto.setApellidos("Cambio");
        dto.setEmail("nuevo@correo.com");

        UserEntity otroConEseEmail = new UserEntity();
        otroConEseEmail.setId(UUID.randomUUID());
        otroConEseEmail.setEmail("nuevo@correo.com");

        when(userRepository.findById(id)).thenReturn(Optional.of(existente));
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(otroConEseEmail));

        // Act & Assert
        EmailYaRegistradoException ex = assertThrows(EmailYaRegistradoException.class, () -> {
            userService.actualizarUsuario(id, dto);
        });

        assertEquals("El correo electrónico 'nuevo@correo.com' ya está registrado.", ex.getMessage());
        verify(userRepository).findById(id);
        verify(userRepository).findByEmail(dto.getEmail());
        verify(userRepository, never()).save(any());
    }
}

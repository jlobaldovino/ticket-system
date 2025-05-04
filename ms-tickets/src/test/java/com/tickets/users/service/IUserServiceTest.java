package com.tickets.users.service;

import com.tickets.users.dto.ActualizarUsuarioDTO;
import com.tickets.users.dto.AuthUserDTO;
import com.tickets.users.dto.CrearUsuarioDTO;
import com.tickets.users.dto.UserDTO;
import com.tickets.users.entity.UserEntity;
import com.tickets.users.exception.EmailYaRegistradoException;
import com.tickets.users.exception.UsuarioNoEncontradoException;
import com.tickets.users.repository.UserRepository;
import com.tickets.users.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IUserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

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




    @Test
    void obtenerTodos_deberiaRetornarUsuariosPaginados() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 2);
        List<UserEntity> lista = List.of(
                new UserEntity(UUID.randomUUID(), "Juan", "Pérez", "juan@correo.com", "hash", "USER", now(), now()),
                new UserEntity(UUID.randomUUID(), "Ana", "Gómez", "ana@correo.com", "hash", "ADMIN", now(), now())
        );
        Page<UserEntity> page = new PageImpl<>(lista, pageable, lista.size());

        when(userRepository.findAll(pageable)).thenReturn(page);

        // Act
        Page<UserDTO> resultado = userService.obtenerTodos(pageable);

        // Assert
        assertEquals(2, resultado.getTotalElements());
        assertEquals("Juan", resultado.getContent().get(0).getNombres());
        assertEquals("ana@correo.com", resultado.getContent().get(1).getEmail());
    }




    @Test
    void obtenerPorId_deberiaRetornarUsuarioCorrectamente() {
        // Arrange
        UUID id = UUID.randomUUID();
        UserEntity entidad = new UserEntity();
        entidad.setId(id);
        entidad.setNombres("Laura");
        entidad.setApellidos("Torres");
        entidad.setEmail("laura@correo.com");
        entidad.setRol("USER");

        when(userRepository.findById(id)).thenReturn(Optional.of(entidad));

        // Act
        UserDTO resultado = userService.obtenerPorId(id);

        // Assert
        assertEquals("Laura", resultado.getNombres());
        assertEquals("laura@correo.com", resultado.getEmail());
        assertEquals("USER", resultado.getRol());

        verify(userRepository).findById(id);
    }

    @Test
    void obtenerPorId_deberiaLanzarExcepcionSiNoExiste() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        UsuarioNoEncontradoException ex = assertThrows(UsuarioNoEncontradoException.class, () -> {
            userService.obtenerPorId(id);
        });

        assertTrue(ex.getMessage().contains(id.toString()));
        verify(userRepository).findById(id);
    }





    @Test
    void buscarPorEmail_deberiaRetornarAuthUserDTOCuandoUsuarioExiste() {
        // Arrange
        String email = "prueba@correo.com";
        UUID id = UUID.randomUUID();
        String hash = "$2a$10$abc123..."; // hash simulado

        UserEntity user = UserEntity.builder()
                .id(id)
                .email(email)
                .passwordHash(hash)
                .rol("USER")
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        AuthUserDTO resultado = userService.buscarPorEmail(email);

        // Assert
        assertNotNull(resultado);
        assertEquals(email, resultado.getEmail());
        assertEquals(hash, resultado.getPasswordHash());
        assertEquals("USER", resultado.getRol());
        assertEquals(id, resultado.getId());

        verify(userRepository).findByEmail(email);
    }

    @Test
    void buscarPorEmail_deberiaLanzarExcepcionCuandoNoExiste() {
        // Arrange
        String email = "inexistente@correo.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        UsuarioNoEncontradoException ex = assertThrows(
                UsuarioNoEncontradoException.class,
                () -> userService.buscarPorEmail(email)
        );

        assertTrue(ex.getMessage().contains(email));
        verify(userRepository).findByEmail(email);
    }


}

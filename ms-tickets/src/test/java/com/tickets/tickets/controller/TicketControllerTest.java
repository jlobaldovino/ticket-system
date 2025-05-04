package com.tickets.tickets.controller;

import com.tickets.tickets.dto.ActualizarTicketDTO;
import com.tickets.tickets.dto.CrearTicketDTO;
import com.tickets.tickets.dto.TicketDTO;
import com.tickets.tickets.service.TicketServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TicketControllerTest {

    @Mock
    private TicketServiceInterface ticketService;

    @InjectMocks
    private TicketController ticketController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCrearTicket() {
        CrearTicketDTO crearTicketDTO = new CrearTicketDTO();
        crearTicketDTO.setDescripcion("Nuevo ticket");
        crearTicketDTO.setUsuarioId(UUID.randomUUID());
        TicketDTO ticketDTO = new TicketDTO();
        ticketDTO.setDescripcion("Nuevo ticket");
        ticketDTO.setStatus(TicketDTO.Status.ABIERTO);
        ticketDTO.setUsuarioId(crearTicketDTO.getUsuarioId());
        when(ticketService.crearTicket(any(CrearTicketDTO.class))).thenReturn(ticketDTO);
        ResponseEntity<TicketDTO> response = ticketController.crearTicket(crearTicketDTO);
        assertEquals(201, response.getStatusCodeValue());
        assertEquals("Nuevo ticket", response.getBody().getDescripcion());
        verify(ticketService, times(1)).crearTicket(crearTicketDTO);
    }

    @Test
    void testEditarTicket() {
        UUID id = UUID.randomUUID();
        ActualizarTicketDTO actualizarTicketDTO = new ActualizarTicketDTO();
        actualizarTicketDTO.setDescripcion("Ticket actualizado");
        TicketDTO ticketDTO = new TicketDTO();
        ticketDTO.setDescripcion("Ticket actualizado"); // Aseguramos que coincida con el valor esperado
        ticketDTO.setStatus(TicketDTO.Status.ABIERTO);
        ticketDTO.setUsuarioId(UUID.randomUUID());
        when(ticketService.actualizarTicket(eq(id), any(ActualizarTicketDTO.class))).thenReturn(ticketDTO);
        ResponseEntity<TicketDTO> response = ticketController.actualizarTicket(id, actualizarTicketDTO);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Ticket actualizado", response.getBody().getDescripcion());
        verify(ticketService, times(1)).actualizarTicket(eq(id), eq(actualizarTicketDTO));
    }

    @Test
    void testEliminarTicket() {
        UUID id = UUID.randomUUID();
        doNothing().when(ticketService).eliminarTicket(id);
        ResponseEntity<Void> response = ticketController.eliminarTicket(id);
        assertEquals(204, response.getStatusCodeValue());
        verify(ticketService, times(1)).eliminarTicket(id);
    }

    @Test
    void testObtenerTicketPorId() {
        UUID id = UUID.randomUUID();
        TicketDTO ticketDTO = new TicketDTO();
        ticketDTO.setDescripcion("Ticket encontrado");
        when(ticketService.obtenerTicketPorId(id)).thenReturn(Optional.of(ticketDTO));
        ResponseEntity<TicketDTO> response = ticketController.obtenerTicketPorId(id);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Ticket encontrado", response.getBody().getDescripcion());
        verify(ticketService, times(1)).obtenerTicketPorId(id);
    }

    @Test
    void testObtenerTicketsPaginados() {
        Page<TicketDTO> tickets = new PageImpl<>(Collections.emptyList());
        when(ticketService.obtenerTicketsPaginados(any(PageRequest.class))).thenReturn(tickets);
        ResponseEntity<Page<TicketDTO>> response = ticketController.obtenerTicketsPaginados(PageRequest.of(0, 10));
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        verify(ticketService, times(1)).obtenerTicketsPaginados(any(PageRequest.class));
    }

    @Test
    void testFiltrarTickets() {
        Page<TicketDTO> tickets = new PageImpl<>(Collections.emptyList());
        when(ticketService.filtrarTickets(anyString(), any(UUID.class), any(PageRequest.class))).thenReturn(tickets);
        ResponseEntity<Page<TicketDTO>> response = ticketController.filtrarTickets("ABIERTO", UUID.randomUUID(), PageRequest.of(0, 10));
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        verify(ticketService, times(1)).filtrarTickets(anyString(), any(UUID.class), any(PageRequest.class));
    }
}
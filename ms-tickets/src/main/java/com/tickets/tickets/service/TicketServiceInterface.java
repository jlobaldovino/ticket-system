package com.tickets.tickets.service;

import com.tickets.tickets.dto.ActualizarTicketDTO;
import com.tickets.tickets.dto.CrearTicketDTO;
import com.tickets.tickets.dto.TicketDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface TicketServiceInterface {

    TicketDTO crearTicket(CrearTicketDTO crearTicketDTO);

    TicketDTO actualizarTicket(UUID id, ActualizarTicketDTO actualizarTicketDTO);

    void eliminarTicket(UUID id);

    Optional<TicketDTO> obtenerTicketPorId(UUID id);

    Page<TicketDTO> obtenerTicketsPaginados(Pageable pageable);

    Page<TicketDTO> filtrarTickets(String status, UUID usuarioId, Pageable pageable);

	
}
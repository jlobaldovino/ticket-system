package com.tickets.tickets.controller;

import com.tickets.tickets.dto.ActualizarTicketDTO;
import com.tickets.tickets.dto.CrearTicketDTO;
import com.tickets.tickets.dto.TicketDTO;
import com.tickets.tickets.service.TicketServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Tag(name = "Tickets", description = "Operaciones relacionadas con tickets")
public class TicketController {

    private final TicketServiceInterface ticketService;

    @Operation(summary = "Crear un nuevo ticket")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registro creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")})
    @PostMapping
    public ResponseEntity<TicketDTO> crearTicket(@Valid @RequestBody CrearTicketDTO crearTicketDTO) {
        TicketDTO nuevoTicket = ticketService.crearTicket(crearTicketDTO);
        return new ResponseEntity<>(nuevoTicket, HttpStatus.CREATED);
    }

    @Operation(summary = "Editar un ticket por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket actualizado"),
            @ApiResponse(responseCode = "404", description = "Ticket no encontrado")})
    @PutMapping("/{id}")
    public ResponseEntity<TicketDTO> actualizarTicket(@PathVariable UUID id, @RequestBody ActualizarTicketDTO actualizarTicketDTO) {
        TicketDTO ticketActualizado = ticketService.actualizarTicket(id, actualizarTicketDTO);
        return ResponseEntity.ok(ticketActualizado);
    }

    @Operation(summary = "Eliminar un ticket por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket encontrado"),
            @ApiResponse(responseCode = "404", description = "Ticket no encontrado")})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTicket(@PathVariable UUID id) {
        ticketService.eliminarTicket(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtener un ticket por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket encontrado"),
            @ApiResponse(responseCode = "404", description = "Ticket no encontrado")})
    @GetMapping("/{id}")
    public ResponseEntity<TicketDTO> obtenerTicketPorId(@PathVariable UUID id) {
        Optional<TicketDTO> ticket = ticketService.obtenerTicketPorId(id);
        return ticket.map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Obtener un listado de ticket paginado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket encontrado")})
    @GetMapping
    public ResponseEntity<Page<TicketDTO>> obtenerTicketsPaginados(Pageable pageable) {
        Page<TicketDTO> tickets = ticketService.obtenerTicketsPaginados(pageable);
        return ResponseEntity.ok(tickets);
    }


    @Operation(summary = "Filtrar tickets por estatus y/o usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tickets filtrados exitosamente")
    })
    @GetMapping("/filtrar")
    public ResponseEntity<Page<TicketDTO>> filtrarTickets(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID usuarioId,
            Pageable pageable) {
        Page<TicketDTO> tickets = ticketService.filtrarTickets(status, usuarioId, pageable);
        return ResponseEntity.ok(tickets);
    }
}
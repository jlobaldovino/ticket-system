package com.tickets.tickets.service.impl;

import com.tickets.tickets.dto.ActualizarTicketDTO;
import com.tickets.tickets.dto.CrearTicketDTO;
import com.tickets.tickets.dto.TicketDTO;
import com.tickets.tickets.entity.TicketEntity;
import com.tickets.tickets.exception.TicketNotFoundException;
import com.tickets.tickets.mapper.TicketMapper;
import com.tickets.tickets.repository.TicketRepository;
import com.tickets.tickets.service.TicketServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketServiceInterface {

    private final TicketRepository ticketRepository;
    private final CacheManager cacheManager;

    @Transactional
    public TicketDTO crearTicket(CrearTicketDTO crearTicketDTO) {
        try {
            TicketEntity ticketEntity = TicketMapper.toEntity(crearTicketDTO);
            TicketEntity savedEntity = ticketRepository.save(ticketEntity);
            return TicketMapper.toDTO(savedEntity);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear el ticket: " + e.getMessage(), e);
        }
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "ticket", key = "#id"),
            @CacheEvict(value = "ticket_status", key ="#status" ),
            @CacheEvict(value = "ticket_usuarioid", key ="#usuarioid" )
    })
    public TicketDTO actualizarTicket(UUID id, ActualizarTicketDTO actualizarTicketDTO) {
        TicketEntity ticketEntity = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id.toString()));
        TicketMapper.actualizarEntidad(ticketEntity, actualizarTicketDTO);
        return TicketMapper.toDTO(ticketEntity);
     }

    @Transactional
    public void eliminarTicket(UUID id) {
        ticketRepository.findById(id).orElseThrow(() -> new TicketNotFoundException(id.toString()));
        ticketRepository.deleteById(id);
    }

    @Transactional
    @Cacheable(value = "ticket", key = "#id")
    public Optional<TicketDTO> obtenerTicketPorId(UUID id) {
        mostrarValorEnCache("ticket", id.toString());
        return ticketRepository.findById(id)
            .map(TicketMapper::toDTO)
            .or(() -> {throw new TicketNotFoundException(id.toString());});
    }


    public void mostrarValorEnCache(String name, String key) {
        Cache ticketCache = cacheManager.getCache(name);
        if (ticketCache != null) {
            Object cachedValue = ticketCache.get(key, Object.class);
            if (cachedValue != null) {
                System.out.println("Valor almacenado en caché para key {"+key+"}: {"+cachedValue+"}");
            } else {
                System.out.println("No se encontró valor en caché para key {"+key+"}");
            }
        } else {
            System.out.println("Error: ticketCache != null");
        }
    }

    @Transactional
    public Page<TicketDTO> obtenerTicketsPaginados(Pageable pageable) {
        return ticketRepository.findAll(pageable).map(TicketMapper::toDTO);
    }

    @Transactional
    @Caching(cacheable = {
            @Cacheable(value = "ticket_status", key = "#status"),
            @Cacheable(value = "ticket_usuarioid", key = "#usuarioid")
    })
    public Page<TicketDTO> filtrarTickets(String status, UUID usuarioId, Pageable pageable) {
        try {
            if (status != null && usuarioId != null) {
                return ticketRepository.findByStatusAndUsuarioId(
                        TicketEntity.Status.valueOf(status.toUpperCase()), usuarioId, pageable
                ).map(TicketMapper::toDTO);
            } else if (status != null) {
                return ticketRepository.findByStatus(
                        TicketEntity.Status.valueOf(status.toUpperCase()), pageable
                ).map(TicketMapper::toDTO);
            } else if (usuarioId != null) {
                return ticketRepository.findByUsuarioId(usuarioId, pageable)
                        .map(TicketMapper::toDTO);
            } else {
                return ticketRepository.findAll(pageable).map(TicketMapper::toDTO);
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Estatus inválido: " + status, e);
        } catch (Exception e) {
            throw new RuntimeException("Error al filtrar los tickets: " + e.getMessage(), e);
        }
    }

    
    
}
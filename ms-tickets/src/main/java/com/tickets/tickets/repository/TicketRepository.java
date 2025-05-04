package com.tickets.tickets.repository;

import com.tickets.tickets.dto.TicketDTO;
import com.tickets.tickets.entity.TicketEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity, UUID> {

    Page<TicketEntity> findByUsuarioId(UUID usuarioId, Pageable pageable);
    Page<TicketEntity> findByStatusAndUsuarioId(TicketEntity.Status status, UUID usuarioId, Pageable pageable);
    Page<TicketEntity> findByStatus(TicketEntity.Status status, Pageable pageable);
}
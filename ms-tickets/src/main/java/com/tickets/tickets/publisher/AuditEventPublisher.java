package com.tickets.tickets.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tickets.tickets.config.RabbitConfig;
import com.tickets.tickets.dto.AuditEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void publish(AuditEventDTO dto) {
        try {
            String json = objectMapper.writeValueAsString(dto);
            rabbitTemplate.convertAndSend(RabbitConfig.AUDIT_QUEUE, json);
            log.debug("Evento de auditoría enviado: {}", json);
        } catch (Exception e) {
            log.error("Error enviando evento de auditoría", e);
        }
    }
}

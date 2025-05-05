package com.tickets.users.aspect;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tickets.users.annotation.AuditEvent;
import com.tickets.users.dto.AuditEventDTO;
import com.tickets.users.publisher.AuditEventPublisher;
import com.tickets.users.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
@RequiredArgsConstructor
@Aspect
@Component
@Slf4j
public class AuditAspect {

    private final AuditEventPublisher publisher;
    private final JwtUtils jwtUtils;

    private final ObjectMapper mapper = new ObjectMapper();

    @AfterReturning(
            pointcut = "@annotation(com.tickets.users.annotation.AuditEvent)",
            returning = "result")
    public void enviarEventoAuditoria(JoinPoint joinPoint, Object result) {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String token = request.getHeader("Authorization");
        String userToken = (token != null) ? jwtUtils.extractEmail(token) : "No_Auntenticado";

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AuditEvent annotation = signature.getMethod().getAnnotation(AuditEvent.class);

        Object[] args = joinPoint.getArgs();
        try {
            String dataBefore = mapper.writeValueAsString(args[0]);
            JsonNode jsonData = mapper.readTree(dataBefore);

            ObjectNode auditNode;
            if (jsonData instanceof ObjectNode) {
                auditNode = (ObjectNode) jsonData;
            } else {
                auditNode = mapper.createObjectNode();
                auditNode.put("valor", jsonData.asText());
            }
            auditNode.put("password", "OCULTO");

            String data = mapper.writeValueAsString(auditNode);
            AuditEventDTO evento = AuditEventDTO.builder()
                    .timestamp(String.valueOf(LocalDateTime.now()))
                    .accion(annotation.accion())
                    .servicio(annotation.servicio())
                    .usuario(userToken)
                    .data(data)
                    .build();

            publisher.publish(evento);
        } catch (Exception e) {
            log.error("Error generando evento de auditoría", e);
        }
    }
}
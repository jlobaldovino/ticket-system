# Ticket System

Sistema de gestión de tickets manejados por usuarios, desarrollado con arquitectura orientada a eventos y microservicios usando Java (Spring Boot) y Node.js (NestJS).

## Microservicios

- `ms-users`: CRUD de usuarios y autenticación
- `ms-tickets`: Gestión de tickets, filtrado, eventos
- `ms-gateway-auth`: Autenticación y proxy (NestJS)

## Tecnologías

- Java 17, Spring Boot
- Node.js, NestJS
- JWT, Swagger, Docker, H2

## Cómo ejecutar

```bash
docker-compose up --build

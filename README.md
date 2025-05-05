# Ticket System

Sistema de gestión de tickets manejados por usuarios, desarrollado con arquitectura orientada a eventos y microservicios usando Java (Spring Boot) y Node.js (NestJS).


## Arquitectura

Diagrama de componentes https://github.com/jlobaldovino/ticket-system/blob/master/arquitectura.png

- **Orientada a eventos** usando RabbitMQ
- **Persistencia por microservicio** (H2 embebido)
- **Seguridad y acceso** controlado mediante JWT
- **Cache distribuido** con Redis
- **Documentación** Swagger/OpenAPI
- **Pruebas unitarias e integración**


## Microservicios

| Servicio | Descripción |
|----------|-------------|
| `ms-users` | Gestión de usuarios (crear, actualizar, listar, buscar por ID o email) |
| `ms-tickets` | Gestión de tickets (crear, editar, eliminar lógico, listar con filtros) |
| `ms-gateway-auth` | Gateway en NestJS con autenticación y autorización basada en JWT |
| `ms-auditoria` | Registra eventos del sistema consumidos desde RabbitMQ y los almacena en base de datos H2 |


## Tecnologías

- **Backend Java (Spring Boot 3)**
- **NestJS (Node.js) como API Gateway**
- **RabbitMQ** (broker de eventos)
- **Redis** (cache de tickets por usuario)
- **H2 DB** (persistencia embebida)
- **Docker Compose** (orquestación de servicios)
- **JUnit + Mockito** (pruebas unitarias)
- **MockMvc + Testcontainers** (pruebas de integración)
- **Swagger/OpenAPI** (documentación de APIs)


## Endpoints principales

-Api de Swagger: http://localhost:3000/api/docs

-Colección de Postman: https://github.com/jlobaldovino/ticket-system/blob/master/tickets-system.postman_collection.json


### Autenticación
- `POST /api/auth/login` → Retorna JWT válido tras login exitoso

### Usuarios (`/api/usuarios`)
- `POST` → Crear usuario (publico)
- `PUT /{id}` → Actualizar usuario (restringido)
- `GET /{id}` → Obtener usuario por ID (restringido)
- `GET` → Listar usuarios paginados (restringido)
- `GET /email/{email}` → Obtener por email (uso interno en login) 

### Tickets (`/api/tickets`)
- `POST` → Crear ticket (restringido)
- `PUT /{id}` → Editar ticket (restringido)
- `DELETE /{id}` → Eliminación lógica (restringido)
- `GET /{id}` → Obtener ticket por ID (restringido)
- `GET` → Listar paginados con filtros: `status`, `usuario`, combinados (restringido)

### Auditoría (`/api/auditoria`)
- `GET` → Retorna todos los eventos registrados desde RabbitMQ

---

## Pruebas

- Pruebas **unitarias** para servicios y/o controladores
- Pruebas **de integración** para flujos completos (crear usuario, editar, login, etc.)
- Mock de eventos para validar publicación en RabbitMQ

---

## Cómo ejecutar


```bash git clone https://github.com/jlobaldovino/ticket-system.git bash```


```bash cd ticket-system bash```


Requisitos previos:
-Docker
-Java 17
-Node.js (v18+)
-Maven

NestJS CLI:

```bash npm install -g @nestjs/cli bash```


Ejecutar todo con Docker

```bash docker-compose up --build bash```


Esto levantará:


-ms-users (Java - Spring Boot)

-ms-tickets (Java - Spring Boot)

-ms-gateway-auth (NestJS)

-ms-audit (Java - Spring Boot)

-RabbitMQ y Redis


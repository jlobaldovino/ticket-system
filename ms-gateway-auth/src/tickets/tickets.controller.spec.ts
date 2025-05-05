import { Test, TestingModule } from '@nestjs/testing';
import { TicketsController } from './tickets.controller';
import { TicketsService } from './tickets.service';
import { JwtAuthGuard } from '../common/guards/jwt.guard';
import { TicketsRespuestaDTO, CrearTicketDTO, ActualizarTicketDTO } from './tickets.dto';
import { NotFoundException, BadRequestException } from '@nestjs/common';

describe('TicketsController', () => {
  let controller: TicketsController;
  let service: TicketsService;

  const mockTicket: TicketsRespuestaDTO = {
    id: 'uuid-123',
    descripcion: 'Ticket de prueba',
    usuarioId: 'uuid-456',
    fechaCreacion: new Date(),
    fechaActualizacion: new Date(),
    status: 'ABIERTO',
  };

  const mockTicketsService = {
    crearTicket: jest.fn().mockResolvedValue(mockTicket),
    actualizarTicket: jest.fn().mockResolvedValue(mockTicket),
    eliminarTicket: jest.fn().mockResolvedValue(mockTicket),
    obtenerPorId: jest.fn().mockResolvedValue(mockTicket),
    obtenerTodosPagin: jest.fn().mockResolvedValue([mockTicket]),
    filtrarTickets: jest.fn().mockResolvedValue([mockTicket]),
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [TicketsController],
      providers: [{ provide: TicketsService, useValue: mockTicketsService }],
    })
      .overrideGuard(JwtAuthGuard)
      .useValue({ canActivate: () => true })
      .compile();

    controller = module.get<TicketsController>(TicketsController);
    service = module.get<TicketsService>(TicketsService);
  });

  it('debería estar definido', () => {
    expect(controller).toBeDefined();
  });

  describe('crear Tiket', () => {
    it('debería crear un ticket exitosamente', async () => {
      const dto: CrearTicketDTO = {
        descripcion: 'Nuevo ticket',
        usuarioId: 'uuid-456',
      };
      const result = await controller.crear(dto);
      expect(result).toEqual(mockTicket);
      expect(service.crearTicket).toHaveBeenCalledWith(dto);
    });

    it('debería lanzar BadRequestException si ocurre un error', async () => {
      const dto: CrearTicketDTO = {
        descripcion: 'Nuevo ticket',
        usuarioId: 'uuid-456',
      };
      mockTicketsService.crearTicket.mockRejectedValue(new BadRequestException('Error al crear ticket'));
      await expect(controller.crear(dto)).rejects.toThrow(BadRequestException);
      expect(service.crearTicket).toHaveBeenCalledWith(dto);
    });
  });

  describe('actualizar tiket', () => {
    it('debería actualizar un ticket exitosamente', async () => {
      const dto: ActualizarTicketDTO = {
        descripcion: 'Ticket actualizado',
        status: 'CERRADO',
      };
      const id = 'uuid-123';
      const result = await controller.actualizar(id, dto);
      expect(result).toEqual(mockTicket);
      expect(service.actualizarTicket).toHaveBeenCalledWith(id, dto);
    });

    it('debería lanzar NotFoundException si el ticket no existe', async () => {
      const dto: ActualizarTicketDTO = {
        descripcion: 'Ticket actualizado',
        status: 'CERRADO',
      };
      const id = 'uuid-999';
      mockTicketsService.actualizarTicket.mockRejectedValue(new NotFoundException('Ticket no encontrado'));
      await expect(controller.actualizar(id, dto)).rejects.toThrow(NotFoundException);
      expect(service.actualizarTicket).toHaveBeenCalledWith(id, dto);
    });
  });

  describe('eliminar ticket', () => {
    it('debería eliminar un ticket exitosamente', async () => {
      const id = 'uuid-123';
      const result = await controller.eliminar(id);
      expect(result).toEqual(mockTicket);
      expect(service.eliminarTicket).toHaveBeenCalledWith(id);
    });

    it('debería lanzar NotFoundException si el ticket no existe', async () => {
      const id = 'uuid-999';
      mockTicketsService.eliminarTicket.mockRejectedValue(new NotFoundException('Ticket no encontrado'));
      await expect(controller.eliminar(id)).rejects.toThrow(NotFoundException);
      expect(service.eliminarTicket).toHaveBeenCalledWith(id);
    });
  });

  describe('obtenerTicketPorId', () => {
    it('debería obtener un ticket por ID exitosamente', async () => {
      const id = 'uuid-123';
      const result = await controller.obtenerTicketPorId(id);
      expect(result).toEqual(mockTicket);
      expect(service.obtenerPorId).toHaveBeenCalledWith(id);
    });

    it('debería lanzar NotFoundException si el ticket no existe', async () => {
      const id = 'uuid-999';
      mockTicketsService.obtenerPorId.mockRejectedValue(new NotFoundException('Ticket no encontrado'));
      await expect(controller.obtenerTicketPorId(id)).rejects.toThrow(NotFoundException);
      expect(service.obtenerPorId).toHaveBeenCalledWith(id);
    });
  });

  describe('listar tikets', () => {
    it('debería listar tickets paginados exitosamente', async () => {
      const query = { page: 1, size: 10 };
      const result = await controller.listarUsuarios(query);
      expect(result).toEqual([mockTicket]);
      expect(service.obtenerTodosPagin).toHaveBeenCalledWith(query);
    });

    it('debería lanzar BadRequestException si ocurre un error', async () => {
      const query = { page: 1, size: 10 };
      mockTicketsService.obtenerTodosPagin.mockRejectedValue(new BadRequestException('Error inesperado'));
      await expect(controller.listarUsuarios(query)).rejects.toThrow(BadRequestException);
      expect(service.obtenerTodosPagin).toHaveBeenCalledWith(query);
    });
  });

  describe('filtrarTickets', () => {
    it('debería filtrar tickets exitosamente', async () => {
      const query = { status: 'ABIERTO', usuarioId: 'uuid-456', page: 1, size: 10 };
      const result = await controller.filtrarTickets(query);
      expect(result).toEqual([mockTicket]);
      expect(service.filtrarTickets).toHaveBeenCalledWith(query);
    });

    it('debería lanzar NotFoundException si no se encuentran tickets', async () => {
      const query = { status: 'CERRADO', usuarioId: 'uuid-999', page: 1, size: 10 };
      mockTicketsService.filtrarTickets.mockRejectedValue(new NotFoundException('No se encontraron tickets'));
      await expect(controller.filtrarTickets(query)).rejects.toThrow(NotFoundException);
      expect(service.filtrarTickets).toHaveBeenCalledWith(query);
    });
  });
});
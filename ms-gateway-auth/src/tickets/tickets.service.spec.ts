import { Test, TestingModule } from '@nestjs/testing';
import { HttpService } from '@nestjs/axios';
import { ConfigService } from '@nestjs/config';
import { TicketsService } from './tickets.service';
import { NotFoundException, BadRequestException, UnauthorizedException } from '@nestjs/common';
import { of, throwError } from 'rxjs';
import { AxiosRequestHeaders, AxiosResponse } from 'axios';

const mockHttpService = {
  get: jest.fn(),
  post: jest.fn(),
  put: jest.fn(),
  delete: jest.fn(),
};

const mockConfigService = {
  get: jest.fn(),
};

describe('TicketsService', () => {
  let service: TicketsService;
  let httpService: HttpService;
  let configService: ConfigService;

  const mockTicketsApiUrl = 'http://mock-tickets-api-url.com';
  const mockTicketId = 'uuid-123';
  const mockTicketData = {
    id: mockTicketId,
    descripcion: 'Ticket de prueba',
    usuarioId: 'uuid-456',
    fechaCreacion: new Date(),
    fechaActualizacion: new Date(),
    status: 'ABIERTO',
  };

  beforeEach(async () => {
    jest.clearAllMocks();

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        TicketsService,
        { provide: HttpService, useValue: mockHttpService },
        { provide: ConfigService, useValue: mockConfigService },
      ],
    }).compile();

    service = module.get<TicketsService>(TicketsService);
    httpService = module.get<HttpService>(HttpService);
    configService = module.get<ConfigService>(ConfigService);

    mockConfigService.get.mockReturnValue(mockTicketsApiUrl);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  describe('crearTicket', () => {
    it('debería crear un ticket exitosamente', async () => {
      const dto = { descripcion: 'Nuevo ticket', usuarioId: 'uuid-456' };
      const mockResponse: AxiosResponse = { data: mockTicketData, status: 201, statusText: 'Created', headers: {}, config: {
        headers: {} as unknown as AxiosRequestHeaders
      } };
      mockHttpService.post.mockReturnValue(of(mockResponse));

      const result = await service.crearTicket(dto);
      expect(result).toEqual(mockTicketData);
      expect(httpService.post).toHaveBeenCalledWith(`${mockTicketsApiUrl}`, dto);
    });

    it('debería lanzar BadRequestException si ocurre un error', async () => {
      const dto = { descripcion: 'Nuevo ticket', usuarioId: 'uuid-456' };
      mockHttpService.post.mockReturnValue(throwError(() => ({ response: { data: { message: 'Error al crear ticket' }, status: 400 } })));

      await expect(service.crearTicket(dto)).rejects.toThrow(BadRequestException);
      expect(httpService.post).toHaveBeenCalledWith(`${mockTicketsApiUrl}`, dto);
    });
  });

  describe('actualizarTicket', () => {
    it('debería actualizar un ticket exitosamente', async () => {
      const dto = { descripcion: 'Ticket actualizado', status: 'CERRADO' };
      const mockResponse: AxiosResponse = { data: mockTicketData, status: 200, statusText: 'OK', headers: {}, config: {
        headers: {} as unknown as AxiosRequestHeaders
      } };
      mockHttpService.put.mockReturnValue(of(mockResponse));

      const result = await service.actualizarTicket(mockTicketId, dto);
      expect(result).toEqual(mockTicketData);
      expect(httpService.put).toHaveBeenCalledWith(`${mockTicketsApiUrl}/${mockTicketId}`, dto);
    });

    it('debería lanzar NotFoundException si el ticket no existe', async () => {
      const dto = { descripcion: 'Ticket actualizado', status: 'CERRADO' };
      mockHttpService.put.mockReturnValue(throwError(() => ({ response: { status: 404, data: 'Ticket no encontrado' } })));

      await expect(service.actualizarTicket(mockTicketId, dto)).rejects.toThrow(NotFoundException);
      expect(httpService.put).toHaveBeenCalledWith(`${mockTicketsApiUrl}/${mockTicketId}`, dto);
    });
  });

  describe('eliminarTicket', () => {
    it('debería eliminar un ticket exitosamente', async () => {
      const mockResponse: AxiosResponse = { data: mockTicketData, status: 200, statusText: 'OK', headers: {}, config: {
        headers: {} as unknown as AxiosRequestHeaders
      } };
      mockHttpService.delete.mockReturnValue(of(mockResponse));

      const result = await service.eliminarTicket(mockTicketId);
      expect(result).toEqual(mockTicketData);
      expect(httpService.delete).toHaveBeenCalledWith(`${mockTicketsApiUrl}/${mockTicketId}`);
    });

    it('debería lanzar NotFoundException si el ticket no existe', async () => {
      mockHttpService.delete.mockReturnValue(throwError(() => ({ response: { status: 404, data: 'Ticket no encontrado' } })));

      await expect(service.eliminarTicket(mockTicketId)).rejects.toThrow(NotFoundException);
      expect(httpService.delete).toHaveBeenCalledWith(`${mockTicketsApiUrl}/${mockTicketId}`);
    });
  });

  describe('obtenerPorId', () => {
    it('debería obtener un ticket por ID exitosamente', async () => {
      const mockResponse: AxiosResponse = { data: mockTicketData, status: 200, statusText: 'OK', headers: {}, config: {
        headers: {} as unknown as AxiosRequestHeaders
      } };
      mockHttpService.get.mockReturnValue(of(mockResponse));

      const result = await service.obtenerPorId(mockTicketId);
      expect(result).toEqual(mockTicketData);
      expect(httpService.get).toHaveBeenCalledWith(`${mockTicketsApiUrl}/${mockTicketId}`);
    });

    it('debería lanzar NotFoundException si el ticket no existe', async () => {
      mockHttpService.get.mockReturnValue(throwError(() => ({ response: { status: 404, data: 'Ticket no encontrado' } })));

      await expect(service.obtenerPorId(mockTicketId)).rejects.toThrow(NotFoundException);
      expect(httpService.get).toHaveBeenCalledWith(`${mockTicketsApiUrl}/${mockTicketId}`);
    });
  });

  describe('obtenerTodosPagin', () => {
    it('debería listar tickets paginados exitosamente', async () => {
      const query = { page: 1, size: 10 };
      const mockResponse: AxiosResponse = { data: [mockTicketData], status: 200, statusText: 'OK', headers: {}, config: {
        headers: {} as unknown as AxiosRequestHeaders
      } };
      mockHttpService.get.mockReturnValue(of(mockResponse));

      const result = await service.obtenerTodosPagin(query);
      expect(result).toEqual([mockTicketData]);
      expect(httpService.get).toHaveBeenCalledWith(`${mockTicketsApiUrl}`, { params: query });
    });

    it('debería lanzar UnauthorizedException si ocurre un error', async () => {
      const query = { page: 1, size: 10 };
      mockHttpService.get.mockReturnValue(throwError(() => ({ response: { status: 401, data: 'No autorizado' } })));

      await expect(service.obtenerTodosPagin(query)).rejects.toThrow(UnauthorizedException);
      expect(httpService.get).toHaveBeenCalledWith(`${mockTicketsApiUrl}`, { params: query });
    });
  });

  describe('filtrarTickets', () => {
    it('debería filtrar tickets exitosamente', async () => {
      const query = { status: 'ABIERTO', usuarioId: 'uuid-456', page: 1, size: 10 };
      const mockResponse: AxiosResponse = { data: [mockTicketData], status: 200, statusText: 'OK', headers: {}, config: {
        headers: {} as unknown as AxiosRequestHeaders
      } };
      mockHttpService.get.mockReturnValue(of(mockResponse));

      const result = await service.filtrarTickets(query);
      expect(result).toEqual([mockTicketData]);
      expect(httpService.get).toHaveBeenCalledWith(`${mockTicketsApiUrl}`, { params: query });
    });

    it('debería lanzar UnauthorizedException si ocurre un error', async () => {
      const query = { status: 'CERRADO', usuarioId: 'uuid-999', page: 1, size: 10 };
      mockHttpService.get.mockReturnValue(throwError(() => ({ response: { status: 401, data: 'No autorizado' } })));

      await expect(service.filtrarTickets(query)).rejects.toThrow(UnauthorizedException);
      expect(httpService.get).toHaveBeenCalledWith(`${mockTicketsApiUrl}`, { params: query });
    });
  });
});